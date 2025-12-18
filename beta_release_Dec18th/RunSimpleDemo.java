import java.io.*;

public class RunSimpleDemo {
    public static void main(String[] args) {
        try {
            StringBuilder code = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("simple_demo.bas"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }

            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code.toString());
            interpreter.run();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
