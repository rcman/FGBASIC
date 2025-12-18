import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Interpreter interpreter = new Interpreter();
            GraphicsWindow graphicsWindow = new GraphicsWindow();
            String filename = null;
            try {
                filename = args[0];
            } catch(Exception fe) {}
            EditorWindow editorWindow = new EditorWindow(interpreter, graphicsWindow, filename);

            interpreter.setGraphicsWindow(graphicsWindow);

            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    interpreter.shutdown();
                } catch (Exception e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }));

            editorWindow.setVisible(true);
            graphicsWindow.setVisible(true);
        });
    }
}

