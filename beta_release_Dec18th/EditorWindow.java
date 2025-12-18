import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class EditorWindow extends JFrame {
    private JTextPane textPane;
    private JTextArea lineNumbers;
    private JScrollPane scrollPane;
    private Interpreter interpreter;
    private GraphicsWindow graphicsWindow;
    private DebugPanel debugPanel;
    private Thread runningThread;
    private SyntaxHighlighter highlighter;
    
    public EditorWindow(Interpreter interpreter, GraphicsWindow graphicsWindow, String filename) {
        this.interpreter = interpreter;
        this.graphicsWindow = graphicsWindow;
        this.highlighter = new SyntaxHighlighter();
        
        setTitle("BASIC Editor");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        createMenuBar();
        createEditor();
        createDebugPanel();
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(debugPanel, BorderLayout.EAST);
        if (filename != null) {
            readFileData(filename);
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem compileItem = new JMenuItem("Compile to Executable...");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e -> newProgram());
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openProgram());
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveProgram());
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        compileItem.addActionListener(e -> compileProgram());
        compileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(compileItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edit");
        JMenuItem gotoItem = new JMenuItem("Go to Line...");
        gotoItem.addActionListener(e -> showGotoDialog());
        gotoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        editMenu.add(gotoItem);
        
        JMenu runMenu = new JMenu("Run");
        JMenuItem runItem = new JMenuItem("Run");
        JMenuItem stopItem = new JMenuItem("Stop");
        JMenuItem stepItem = new JMenuItem("Step");
        JMenuItem toggleBreakpointItem = new JMenuItem("Toggle Breakpoint");
        
        runItem.addActionListener(e -> runProgram());
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        stopItem.addActionListener(e -> stopProgram());
        stepItem.addActionListener(e -> stepProgram());
        stepItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
        toggleBreakpointItem.addActionListener(e -> toggleBreakpoint());
        toggleBreakpointItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        
        runMenu.add(runItem);
        runMenu.add(stopItem);
        runMenu.add(stepItem);
        runMenu.addSeparator();
        runMenu.add(toggleBreakpointItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(runMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createEditor() {
        textPane = new JTextPane();
        textPane.setBackground(new Color(20, 20, 30));
        textPane.setForeground(new Color(0, 255, 255));
        textPane.setCaretColor(Color.WHITE);
        textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
        
        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(40, 40, 50));
        lineNumbers.setForeground(new Color(150, 150, 150));
        lineNumbers.setFont(new Font("Courier New", Font.PLAIN, 14));
        lineNumbers.setEditable(false);
        lineNumbers.setPreferredSize(new Dimension(50, 0));
        
        textPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                updateLineNumbers();
                applySyntaxHighlighting();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                updateLineNumbers();
                applySyntaxHighlighting();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                updateLineNumbers();
            }
        });
        
        scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    private void createDebugPanel() {
        debugPanel = new DebugPanel(interpreter);
    }
    
    private void applySyntaxHighlighting() {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = textPane.getStyledDocument();
            highlighter.highlight(doc, textPane.getText());
        });
    }
    
    private void updateLineNumbers() {
        int lines = textPane.getDocument().getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }
    
    private void newProgram() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Clear current program?", "New", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            textPane.setText("");
            interpreter.clear();
            graphicsWindow.clear();
        }
    }

    public boolean readFileData(String filename) {
           try (BufferedReader reader = new BufferedReader(
                    new FileReader(filename))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                textPane.setText(sb.toString());
                interpreter.clear();
                graphicsWindow.clear();
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
                return false;
            }
    }

    private void openProgram() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            readFileData(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void saveProgram() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(chooser.getSelectedFile()))) {
                writer.write(textPane.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }

    private void compileProgram() {
        // Get program name
        String programName = JOptionPane.showInputDialog(this,
            "Enter program name (without extension):",
            "Compile to Executable",
            JOptionPane.QUESTION_MESSAGE);

        if (programName == null || programName.trim().isEmpty()) {
            return;
        }

        programName = programName.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        // Choose output directory
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Output Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File outputDir = chooser.getSelectedFile();

        // Show progress dialog
        JDialog progressDialog = new JDialog(this, "Compiling...", true);
        JLabel progressLabel = new JLabel("Compiling program, please wait...");
        progressLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        progressDialog.add(progressLabel);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(this);

        // Compile in background thread
        final String finalProgramName = programName;
        new Thread(() -> {
            try {
                boolean success = Compiler.compile(
                    textPane.getText(),
                    finalProgramName,
                    outputDir
                );

                SwingUtilities.invokeLater(() -> {
                    progressDialog.setVisible(false);
                    progressDialog.dispose();

                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Compilation successful!\n\n" +
                            "Output files created in:\n" +
                            outputDir.getAbsolutePath() + "\n\n" +
                            "Files:\n" +
                            "  " + finalProgramName + ".jar (executable JAR)\n" +
                            "  " + finalProgramName + ".sh (Linux/Mac launcher)\n" +
                            "  " + finalProgramName + ".bat (Windows launcher)\n\n" +
                            "To run:\n" +
                            "  Linux/Mac: ./" + finalProgramName + ".sh\n" +
                            "  Windows: " + finalProgramName + ".bat\n" +
                            "  Or: java -jar " + finalProgramName + ".jar",
                            "Compilation Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Compilation failed. Check console for errors.\n\n" +
                            "Make sure you are running with JDK (not JRE).",
                            "Compilation Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    progressDialog.setVisible(false);
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Compilation error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
                e.printStackTrace();
            }
        }).start();

        // Show progress dialog (blocks until compilation completes)
        progressDialog.setVisible(true);
    }

    private void showGotoDialog() {
        String input = JOptionPane.showInputDialog(this, "Go to line:");
        if (input != null) {
            try {
                int line = Integer.parseInt(input.trim());
                Element root = textPane.getDocument().getDefaultRootElement();
                if (line >= 1 && line <= root.getElementCount()) {
                    Element elem = root.getElement(line - 1);
                    textPane.setCaretPosition(elem.getStartOffset());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid line number");
            }
        }
    }

    private void runProgram() {
        if (runningThread != null && runningThread.isAlive()) {
            JOptionPane.showMessageDialog(this, "Program already running");
            return;
        }

        graphicsWindow.clear();
        interpreter.loadProgram(textPane.getText());
        interpreter.setDebugPanel(debugPanel);

        runningThread = new Thread(() -> {
            try {
                interpreter.run();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Runtime error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
        runningThread.start();
    }

    private void stopProgram() {
        interpreter.stop();
        if (runningThread != null) {
            runningThread.interrupt();
        }
    }

    private void stepProgram() {
        if (runningThread != null && runningThread.isAlive()) {
            interpreter.step();
        } else {
            graphicsWindow.clear();
            interpreter.loadProgram(textPane.getText());
            interpreter.setDebugPanel(debugPanel);
            interpreter.setStepMode(true);

            runningThread = new Thread(() -> {
                try {
                    interpreter.run();
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Runtime error: " + e.getMessage()));
                }
            });
            runningThread.start();
        }
    }

    private void toggleBreakpoint() {
        try {
            int caretPos = textPane.getCaretPosition();
            Element root = textPane.getDocument().getDefaultRootElement();
            int editorLineNum = root.getElementIndex(caretPos);

            // Toggle breakpoint in interpreter
            interpreter.toggleBreakpoint(editorLineNum);
            debugPanel.updateBreakpoints(interpreter.getBreakpoints());

            // Visual feedback
            Set<Integer> breakpoints = interpreter.getBreakpoints();
            if (breakpoints.contains(editorLineNum)) {
                System.out.println("Breakpoint set at line " + (editorLineNum + 1));
            } else {
                System.out.println("Breakpoint removed from line " + (editorLineNum + 1));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error toggling breakpoint: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
