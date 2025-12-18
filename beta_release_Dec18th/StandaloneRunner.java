import javax.swing.*;

/**
 * StandaloneRunner - Executes a compiled BASIC program as a standalone application
 * This class is used when compiling BASIC programs to executables
 */
public class StandaloneRunner {
    private static final String PROGRAM_CODE = "<<PROGRAM_CODE>>";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Interpreter interpreter = new Interpreter();
            GraphicsWindow graphicsWindow = new GraphicsWindow();

            interpreter.setGraphicsWindow(graphicsWindow);

            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    interpreter.shutdown();
                } catch (Exception e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }));

            graphicsWindow.setVisible(true);

            // Load and run the embedded program
            try {
                interpreter.loadProgram(PROGRAM_CODE);
                new Thread(() -> {
                    try {
                        interpreter.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                            "Program error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to load program: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
