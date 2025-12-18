import java.io.*;

public class SimpleSelectTest {
    public static void main(String[] args) {
        String code = "LET X = 2\nSELECT CASE X\nCASE 2\nPRINT \"Matched\"\nEND SELECT\nPRINT \"Done\"\n";

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.loadProgram(code);
            interpreter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
