import java.io.*;

public class TestGosub {
    public static void main(String[] args) {
        try {
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("test_gosub_fixed.bas"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }

            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code.toString());

            System.out.println("=== Running GOSUB test ===");
            interpreter.run();
            System.out.println("=== Test completed ===");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
