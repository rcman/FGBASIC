import java.io.*;

public class DebugInterpreter {
    public static void main(String[] args) {
        String code = "LET X = 2\n" +
                     "PRINT \"Line 1\"\n" +
                     "SELECT CASE X\n" +
                     "CASE 2\n" +
                     "PRINT \"Line 4\"\n" +
                     "END SELECT\n" +
                     "PRINT \"Line 6\"\n";

        try {
            Interpreter interp = new Interpreter();
            interp.loadProgram(code);

            System.out.println("Program loaded. Total lines: " + 7);
            System.out.println("Line contents:");
            for (int i = 0; i < 7; i++) {
                System.out.println("  Line " + i + ": [content]");
            }

            System.out.println("\nRunning...\n");
            interp.run();
            System.out.println("\nProgram ended. Current line should be >= 7");

        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
