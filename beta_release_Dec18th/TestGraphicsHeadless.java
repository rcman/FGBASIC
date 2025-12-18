import java.io.*;

public class TestGraphicsHeadless {
    public static void main(String[] args) {
        String[] tests = {
            "test_color.bas",
            "test_select.bas",
            "test_input.bas",
            "test_mouse.bas"
        };

        System.out.println("=== Testing Commands Without Graphics ===\n");

        // Test 1: COLOR command without graphics window
        testCommand("COLOR without graphics", "COLOR 255, 0, 0");

        // Test 2: LINE command without graphics window
        testCommand("LINE without graphics", "LINE 0, 0, 100, 100");

        // Test 3: SPRITE command without graphics window
        testCommand("SPRITE without graphics", "SPRITE CREATE, 1, 32, 32");

        // Test 4: SELECT CASE
        testCommand("SELECT CASE",
            "LET X = 2\n" +
            "SELECT CASE X\n" +
            "CASE 1\n" +
            "PRINT \"One\"\n" +
            "CASE 2\n" +
            "PRINT \"Two\"\n" +
            "CASE 3\n" +
            "PRINT \"Three\"\n" +
            "END SELECT\n");

        // Test 5: MOUSEX/MOUSEY without input
        testCommand("MOUSEX without input", "MOUSEX\nPRINT MOUSEX");

        // Test 6: KEYPRESS without input
        testCommand("KEYPRESS without input", "KEYPRESS\nPRINT KEYPRESS");

        // Test 7: INPUT command
        testCommand("INPUT command", "INPUT X\nPRINT X");

        // Test 8: Multi-line IF
        testCommand("Multi-line IF",
            "LET X = 10\n" +
            "IF X > 5 THEN\n" +
            "PRINT \"Greater\"\n" +
            "END IF\n");

        // Test 9: String concatenation in PRINT
        testCommand("String concat PRINT",
            "LET A$ = \"Hello\"\n" +
            "LET B$ = \"World\"\n" +
            "PRINT A$ + \" \" + B$\n");

        // Test 10: ELSE clause
        testCommand("IF-ELSE",
            "LET X = 3\n" +
            "IF X > 5 THEN\n" +
            "PRINT \"Greater\"\n" +
            "ELSE\n" +
            "PRINT \"Not greater\"\n" +
            "END IF\n");

        System.out.println("\n=== All command tests completed ===");
    }

    private static void testCommand(String name, String code) {
        System.out.println("Testing: " + name);
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code);
            interpreter.run();
            System.out.println("  ✓ PASSED\n");
        } catch (Exception e) {
            System.out.println("  ✗ FAILED: " + e.getMessage());
            System.out.println("  Details: " + e.getClass().getName() + "\n");
        }
    }
}
