import java.io.*;

public class TestSelectDebug {
    public static void main(String[] args) {
        String code =
            "LET X = 2\n" +
            "PRINT \"Before SELECT\"\n" +
            "SELECT CASE X\n" +
            "CASE 1\n" +
            "PRINT \"Case 1\"\n" +
            "CASE 2\n" +
            "PRINT \"Case 2\"\n" +
            "CASE 3\n" +
            "PRINT \"Case 3\"\n" +
            "END SELECT\n" +
            "PRINT \"After SELECT\"\n";

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code);
            System.out.println("Expected output:");
            System.out.println("Before SELECT");
            System.out.println("Case 2");
            System.out.println("After SELECT");
            System.out.println("\nActual output:");
            interpreter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
