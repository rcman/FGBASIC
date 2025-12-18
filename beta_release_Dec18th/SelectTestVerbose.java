import java.io.*;

public class SelectTestVerbose {
    public static void main(String[] args) {
        String code = "LET X = 2\n" +
                     "PRINT \"Before SELECT\"\n" +
                     "SELECT CASE X\n" +
                     "CASE 2\n" +
                     "PRINT \"In CASE 2\"\n" +
                     "END SELECT\n" +
                     "PRINT \"After SELECT\"\n";

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code);
            System.out.println("=== Program Start ===");
            interpreter.run();
            System.out.println("=== Program End ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
