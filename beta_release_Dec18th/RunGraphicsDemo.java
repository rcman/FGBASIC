import java.io.*;

public class RunGraphicsDemo {
    public static void main(String[] args) {
        try {
            // Read the BASIC program
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("graphics_demo.bas"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }

            // Create graphics window
            GraphicsWindow graphics = new GraphicsWindow();
            graphics.setVisible(true);

            // Create interpreter and link to graphics
            Interpreter interpreter = new Interpreter();
            interpreter.setGraphicsWindow(graphics);

            // Load and run program
            interpreter.loadProgram(code.toString());

            System.out.println("Running graphics demo...");
            System.out.println("Check the graphics window for results!");

            interpreter.run();

            System.out.println("Demo completed successfully!");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
