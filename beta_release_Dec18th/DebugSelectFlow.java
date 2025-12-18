import java.io.*;

public class DebugSelectFlow {
    public static void main(String[] args) {
        String code =
            "PRINT \"Line 0\"\n" +           // Line 0
            "LET X = 2\n" +                  // Line 1
            "SELECT CASE X\n" +              // Line 2
            "CASE 1\n" +                     // Line 3
            "PRINT \"Case 1\"\n" +           // Line 4
            "CASE 2\n" +                     // Line 5
            "PRINT \"Case 2\"\n" +           // Line 6
            "CASE 3\n" +                     // Line 7
            "PRINT \"Case 3\"\n" +           // Line 8
            "END SELECT\n" +                 // Line 9
            "PRINT \"After\"\n";             // Line 10

        try {
            // Create custom interpreter with debug output
            Interpreter interpreter = new Interpreter() {
                private int lastLine = -1;
                @Override
                public void run() throws Exception {
                    boolean running = true;
                    int currentLine = 0;

                    while (running && currentLine < 11) {
                        if (currentLine != lastLine) {
                            System.err.println("DEBUG: Executing line " + currentLine);
                            lastLine = currentLine;
                        }
                        try {
                            // Call parent run which will execute
                            super.run();
                            break;
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                }
            };

            interpreter.loadProgram(code);
            interpreter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
