import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;

public class DebugPanel extends JPanel {
    private JTextArea variablesArea;
    private JTextArea breakpointsArea;
    private JLabel currentLineLabel;
    private Interpreter interpreter;
    
    public DebugPanel(Interpreter interpreter) {
        this.interpreter = interpreter;
        setPreferredSize(new Dimension(250, 0));
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(30, 30, 40));
        
        // Variables tab
        variablesArea = new JTextArea();
        variablesArea.setEditable(false);
        variablesArea.setBackground(new Color(20, 20, 30));
        variablesArea.setForeground(new Color(0, 255, 255));
        variablesArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane varScroll = new JScrollPane(variablesArea);
        tabbedPane.addTab("Variables", varScroll);
        
        // Breakpoints tab
        breakpointsArea = new JTextArea();
        breakpointsArea.setEditable(false);
        breakpointsArea.setBackground(new Color(20, 20, 30));
        breakpointsArea.setForeground(new Color(255, 100, 100));
        breakpointsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane bpScroll = new JScrollPane(breakpointsArea);
        tabbedPane.addTab("Breakpoints", bpScroll);
        
        // Current line display
        currentLineLabel = new JLabel("Current Line: -");
        currentLineLabel.setForeground(Color.WHITE);
        currentLineLabel.setBackground(new Color(50, 50, 60));
        currentLineLabel.setOpaque(true);
        currentLineLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(currentLineLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Update timer
        Timer updateTimer = new Timer(100, e -> updateDisplay());
        updateTimer.start();
    }
    
    public void updateDisplay() {
        if (interpreter == null) return;
        
        // Update variables
        StringBuilder varText = new StringBuilder();
        Map<String, Double> vars = interpreter.getVariables();
        for (Map.Entry<String, Double> entry : vars.entrySet()) {
            varText.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        variablesArea.setText(varText.toString());
        
        // Update current line
        currentLineLabel.setText("Current Line: " + interpreter.getCurrentLine());
    }
    
    public void updateBreakpoints(Set<Integer> breakpoints) {
        StringBuilder bpText = new StringBuilder();
        for (Integer bp : breakpoints) {
            bpText.append("Line ").append(bp).append("\n");
        }
        breakpointsArea.setText(bpText.toString());
    }
}
