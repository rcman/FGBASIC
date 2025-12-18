public class DebugDefFn {
    public static void main(String[] args) {
        String code = "DEF FN DOUBLE(X) = X * 2\nLET Y = FN DOUBLE(5)\nPRINT Y\n";

        try {
            Interpreter interp = new Interpreter();
            interp.loadProgram(code);

            System.out.println("Running DEF FN");
            interp.run();

            System.out.println("\nUser functions defined:");
            if (interp.getUserFunctions() != null) {
                for (String key : interp.getUserFunctions().keySet()) {
                    System.out.println("  - " + key);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
