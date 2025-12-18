import java.io.*;

/**
 * Debug sprite test to find the issue
 */
public class SpriteTestDebug {
    public static void main(String[] args) {
        System.out.println("=== Sprite Test Debug ===\n");

        // Test 1: Simple sprite creation
        System.out.println("Test 1: Basic sprite creation");
        try {
            Interpreter interp = new Interpreter();
            GraphicsWindow gfx = new GraphicsWindow();
            interp.setGraphicsWindow(gfx);

            String prog =
                "CLS\n" +
                "SPRITE CREATE, 0, 16, 16\n" +
                "COLOR 255, 0, 0\n" +
                "SPRITE PIXEL, 0, 8, 8\n" +
                "SPRITE MOVE, 0, 100, 100\n" +
                "SPRITE SHOW, 0\n" +
                "PRINT \"Sprite created\"\n";

            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ Basic creation works\n");
        } catch (Exception e) {
            System.out.println("✗ Failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 2: RND function
        System.out.println("\nTest 2: RND function");
        try {
            Interpreter interp = new Interpreter();
            String prog =
                "LET A = RND\n" +
                "PRINT \"RND = \", A\n" +
                "LET B = RND(100)\n" +
                "PRINT \"RND(100) = \", B\n";

            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ RND works\n");
        } catch (Exception e) {
            System.out.println("✗ Failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 3: MOD operator
        System.out.println("\nTest 3: MOD operator");
        try {
            Interpreter interp = new Interpreter();
            String prog =
                "LET A = 15\n" +
                "LET B = A MOD 16\n" +
                "PRINT \"15 MOD 16 = \", B\n";

            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ MOD works\n");
        } catch (Exception e) {
            System.out.println("✗ Failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 4: ANIMATE command
        System.out.println("\nTest 4: ANIMATE command");
        try {
            Interpreter interp = new Interpreter();
            GraphicsWindow gfx = new GraphicsWindow();
            interp.setGraphicsWindow(gfx);

            String prog =
                "SPRITE CREATE, 0, 16, 16\n" +
                "COLOR 255, 0, 0\n" +
                "SPRITE PIXEL, 0, 8, 8\n" +
                "SPRITE MOVE, 0, 100, 100\n" +
                "SPRITE SHOW, 0\n" +
                "ANIMATE START, 0, 2, 3, 60, 1\n" +
                "WAIT 2\n" +
                "ANIMATE STOP, 0\n" +
                "PRINT \"Animation test complete\"\n";

            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ ANIMATE works\n");
        } catch (Exception e) {
            System.out.println("✗ Failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 5: Run the actual stress test
        System.out.println("\nTest 5: Loading sprite_stress_test.bas");
        try {
            String filename = "sprite_stress_test.bas";
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }

            Interpreter interp = new Interpreter();
            GraphicsWindow gfx = new GraphicsWindow();
            interp.setGraphicsWindow(gfx);
            gfx.setVisible(true);

            System.out.println("Loaded program, running...");
            interp.loadProgram(code.toString());
            interp.run();

            System.out.println("✓ Stress test completed\n");
        } catch (FileNotFoundException e) {
            System.out.println("✗ File not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Failed at line: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Debug Complete ===");
    }
}
