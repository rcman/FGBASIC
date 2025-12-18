/**
 * Quick syntax test for sprite_stress_test.bas
 */
public class QuickSyntaxTest {
    public static void main(String[] args) {
        System.out.println("=== Quick Syntax Test ===\n");

        // Test the specific syntax used in stress test
        Interpreter interp = new Interpreter();

        System.out.println("Test 1: RND with arithmetic");
        try {
            String prog = "LET A = 100 + RND(155)\nPRINT A";
            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ PASS\n");
        } catch (Exception e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        System.out.println("Test 2: MOD operator");
        interp.clear();
        try {
            String prog = "LET I = 15\nLET COL = I MOD 16\nPRINT COL";
            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ PASS\n");
        } catch (Exception e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        System.out.println("Test 3: INT function");
        interp.clear();
        try {
            String prog = "LET I = 15\nLET ROW = INT(I / 16)\nPRINT ROW";
            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ PASS\n");
        } catch (Exception e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        System.out.println("Test 4: Complex expression");
        interp.clear();
        try {
            String prog = "LET VX = (RND - 0.5) * 10\nPRINT VX";
            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ PASS\n");
        } catch (Exception e) {
            System.out.println("✗ FAIL: " + e.getMessage() + "\n");
        }

        System.out.println("Test 5: Nested FOR loops");
        interp.clear();
        try {
            String prog =
                "LET COUNT = 0\n" +
                "FOR I = 0 TO 2\n" +
                "  FOR X = 0 TO 2\n" +
                "    FOR Y = 0 TO 2\n" +
                "      LET COUNT = COUNT + 1\n" +
                "    NEXT Y\n" +
                "  NEXT X\n" +
                "NEXT I\n" +
                "PRINT \"Count: \", COUNT";
            interp.loadProgram(prog);
            interp.run();
            System.out.println("✓ PASS\n");
        } catch (Exception e) {
            System.out.println("✗ FAIL: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Tests Complete ===");
    }
}
