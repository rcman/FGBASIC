public class TestSelectCases {
    public static void main(String[] args) {
        test("Match first case", "LET X=1\nSELECT CASE X\nCASE 1\nPRINT \"One\"\nCASE 2\nPRINT \"Two\"\nEND SELECT\n", "One");
        test("Match last case", "LET X=3\nSELECT CASE X\nCASE 1\nPRINT \"One\"\nCASE 3\nPRINT \"Three\"\nEND SELECT\n", "Three");
        test("No match", "LET X=5\nSELECT CASE X\nCASE 1\nPRINT \"One\"\nCASE 2\nPRINT \"Two\"\nEND SELECT\nPRINT \"Done\"\n", "Done");
        test("CASE ELSE", "LET X=5\nSELECT CASE X\nCASE 1\nPRINT \"One\"\nCASE ELSE\nPRINT \"Other\"\nEND SELECT\n", "Other");
        test("Multiple statements", "LET X=2\nSELECT CASE X\nCASE 2\nPRINT \"A\"\nPRINT \"B\"\nCASE 3\nPRINT \"C\"\nEND SELECT\n", "A\nB");
    }

    static void test(String name, String code, String expected) {
        System.out.println("Test: " + name);
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            System.setOut(ps);

            Interpreter interp = new Interpreter();
            interp.loadProgram(code);
            interp.run();

            System.setOut(System.err);
            String output = baos.toString().trim();
            String exp = expected.replace("\\n", "\n");

            if (output.equals(exp)) {
                System.out.println("  ✓ PASSED");
            } else {
                System.out.println("  ✗ FAILED");
                System.out.println("    Expected: " + exp.replace("\n", "\\n"));
                System.out.println("    Got: " + output.replace("\n", "\\n"));
            }
        } catch (Exception e) {
            System.setOut(System.err);
            System.out.println("  ✗ EXCEPTION: " + e.getMessage());
        }
        System.out.println();
    }
}
