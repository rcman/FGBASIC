import java.io.*;

public class CommandTest {
    public static void main(String[] args) {
        try {
            // Read the test program
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("test_all_commands.bas"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }

            // Create interpreter
            Interpreter interpreter = new Interpreter();

            System.out.println("Loading program...");
            interpreter.loadProgram(code.toString());

            System.out.println("Running program...");
            interpreter.run();

            System.out.println("\n=== Test completed ===");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
