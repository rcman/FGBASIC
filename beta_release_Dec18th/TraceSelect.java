import java.io.*;

public class TraceSelect {
    public static void main(String[] args) {
        String code =
            "LET X = 2\n" +            // Line 0
            "SELECT CASE X\n" +        // Line 1
            "CASE 2\n" +               // Line 2 - MATCH
            "PRINT \"Case 2\"\n" +     // Line 3
            "CASE 3\n" +               // Line 4
            "PRINT \"Case 3\"\n" +     // Line 5
            "END SELECT\n" +           // Line 6
            "PRINT \"After\"\n";       // Line 7

        System.out.println("Expected execution order:");
        System.out.println("  Line 0: LET X = 2");
        System.out.println("  Line 1: SELECT CASE X (finds CASE 2, jumps to line 2)");
        System.out.println("  Line 2: CASE 2 (executes case block)");
        System.out.println("  Line 3: PRINT \"Case 2\"");
        System.out.println("  (skip to END SELECT)");
        System.out.println("  Line 6: END SELECT");
        System.out.println("  Line 7: PRINT \"After\"");
        System.out.println("\nActual output:\n");

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code);
            interpreter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
