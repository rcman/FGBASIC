public class TestDefFn {
    public static void main(String[] args) {
        String code = "DEF FN DOUBLE(X) = X * 2\nPRINT FN DOUBLE(5)\n";

        try {
            Interpreter interp = new Interpreter();
            interp.loadProgram(code);
            System.out.println("Expected: 10.0");
            System.out.print("Actual: ");
            interp.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
