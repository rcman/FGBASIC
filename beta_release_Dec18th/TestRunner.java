import java.io.*;
import java.nio.file.*;

/**
 * Automated test runner for FGBasic interpreter
 * Tests bug fixes in headless mode
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== FGBasic Bug Fixes Test Runner ===\n");

        boolean allPassed = true;

        // Test 1: Basic arithmetic and control flow
        allPassed &= runTest("Basic Arithmetic", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET X = 10\nLET Y = 5\nLET Z = X + Y\nPRINT Z");
            interp.run();
            return interp.getVariables().get("Z") == 15.0;
        });

        // Test 2: Division by zero handling (logged but doesn't crash)
        allPassed &= runTest("Division by Zero Handling", () -> {
            Interpreter interp = new Interpreter();
            // Expression parser catches division by zero and returns 0
            // This is intentional - BASIC should handle errors gracefully
            interp.loadProgram("LET A = 10\nLET B = 0\nLET C = A / B\nLET D = 1");
            interp.run();
            // If we got here without crashing, test passes
            return interp.getVariables().get("D") == 1.0;
        });

        // Test 3: Array operations
        allPassed &= runTest("Array Operations", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("DIM ARR(10)\nLET ARR(0) = 100\nLET ARR(5) = 500\nLET ARR(10) = 1000");
            interp.run();
            double[] arr = interp.getArrays().get("ARR");
            return arr != null && arr[0] == 100.0 && arr[5] == 500.0 && arr[10] == 1000.0;
        });

        // Test 4: While loop with AST optimization
        allPassed &= runTest("While Loop AST", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET COUNT = 0\nWHILE COUNT < 100\nLET COUNT = COUNT + 1\nWEND");
            interp.run();
            return interp.getVariables().get("COUNT") == 100.0;
        });

        // Test 5: Do-While loop
        allPassed &= runTest("Do-While Loop", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET N = 0\nDO WHILE N < 50\nLET N = N + 1\nLOOP");
            interp.run();
            return interp.getVariables().get("N") == 50.0;
        });

        // Test 6: For loop
        allPassed &= runTest("For Loop", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET SUM = 0\nFOR I = 1 TO 10\nLET SUM = SUM + I\nNEXT I");
            interp.run();
            return interp.getVariables().get("SUM") == 55.0;
        });

        // Test 7: DATA/READ with fixed indentation
        allPassed &= runTest("DATA/READ System", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("DATA 10, 20, 30\nREAD X, Y, Z");
            interp.run();
            return interp.getVariables().get("X") == 10.0 &&
                   interp.getVariables().get("Y") == 20.0 &&
                   interp.getVariables().get("Z") == 30.0;
        });

        // Test 8: String variables
        allPassed &= runTest("String Variables", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET MSG$ = \"Hello\"\nLET NAME$ = \"World\"");
            interp.run();
            return true; // Just checking it doesn't crash
        });

        // Test 9: Math functions
        allPassed &= runTest("Math Functions", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("LET X = SQR(16)\nLET Y = ABS(-5)\nLET Z = INT(3.7)");
            interp.run();
            return interp.getVariables().get("X") == 4.0 &&
                   interp.getVariables().get("Y") == 5.0 &&
                   interp.getVariables().get("Z") == 3.0;
        });

        // Test 10: Nested loops
        allPassed &= runTest("Nested Loops", () -> {
            Interpreter interp = new Interpreter();
            String prog = "LET TOTAL = 0\n" +
                         "FOR I = 1 TO 5\n" +
                         "  FOR J = 1 TO 3\n" +
                         "    LET TOTAL = TOTAL + 1\n" +
                         "  NEXT J\n" +
                         "NEXT I";
            interp.loadProgram(prog);
            interp.run();
            return interp.getVariables().get("TOTAL") == 15.0;
        });

        // Test 11: Comparison operators
        allPassed &= runTest("Comparison Operators", () -> {
            Interpreter interp = new Interpreter();
            String prog = "LET A = 5\nLET B = 10\n" +
                         "IF A < B THEN LET R1 = 1\n" +
                         "IF A > B THEN LET R2 = 1 ELSE LET R2 = 0\n" +
                         "IF A = 5 THEN LET R3 = 1";
            interp.loadProgram(prog);
            interp.run();
            return interp.getVariables().get("R1") == 1.0 &&
                   interp.getVariables().get("R2") == 0.0 &&
                   interp.getVariables().get("R3") == 1.0;
        });

        // Test 12: GOSUB/RETURN
        allPassed &= runTest("GOSUB/RETURN", () -> {
            Interpreter interp = new Interpreter();
            String prog = "LET X = 0\nGOSUB 100\nGOTO 200\n" +
                         "100 LET X = 42\nRETURN\n" +
                         "200 END";
            interp.loadProgram(prog);
            interp.run();
            return interp.getVariables().get("X") == 42.0;
        });

        System.out.println("\n" + ("=".repeat(50)));
        if (allPassed) {
            System.out.println("✓ ALL TESTS PASSED!");
        } else {
            System.out.println("✗ SOME TESTS FAILED");
        }
        System.out.println("=".repeat(50));
    }

    private static boolean runTest(String testName, TestCase test) {
        System.out.print(String.format("%-30s", testName + "..."));
        try {
            boolean result = test.run();
            if (result) {
                System.out.println(" ✓ PASS");
            } else {
                System.out.println(" ✗ FAIL");
            }
            return result;
        } catch (Exception e) {
            System.out.println(" ✗ ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    interface TestCase {
        boolean run() throws Exception;
    }
}
