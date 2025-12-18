import java.io.*;

public class RunFullGraphicsTest {
    public static void main(String[] args) {
        try {
            // Read the BASIC program
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("full_graphics_test.bas"))) {
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

            System.out.println("========================================");
            System.out.println("FGBasic Full Graphics Test");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Running comprehensive graphics test...");
            System.out.println("Watch the graphics window for visual results!");
            System.out.println();
            System.out.println("Tests include:");
            System.out.println("  - CLS (Clear Screen)");
            System.out.println("  - COLOR (RGB color setting)");
            System.out.println("  - PIXEL/PSET (Individual pixels)");
            System.out.println("  - LINE (Lines in all directions)");
            System.out.println("  - CIRCLE (Outline and filled)");
            System.out.println("  - RECT/BOX (Rectangles)");
            System.out.println("  - ELLIPSE (Ellipses)");
            System.out.println("  - POLYGON (Multi-sided shapes)");
            System.out.println("  - TEXT (Text rendering)");
            System.out.println("  - FONT (Font properties)");
            System.out.println("  - NOREFRESH/REFRESH (Batch drawing)");
            System.out.println("  - Combined scene demo");
            System.out.println();
            System.out.println("The test will cycle through each command...");
            System.out.println("Each test screen displays for 2-3 seconds.");
            System.out.println();

            interpreter.run();

            System.out.println();
            System.out.println("========================================");
            System.out.println("Graphics test completed successfully!");
            System.out.println("All 12 graphics commands tested.");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
