public class DebugCaseExecution {
    public static void main(String[] args) {
        String code = "LET X = 2\nSELECT CASE X\nCASE 2\nPRINT \"Case 2\"\nCASE 3\nPRINT \"Case 3\"\nEND SELECT\n";

        // Add instrumentation to Interpreter
        String[] lines = code.split("\n");
        System.out.println("Program lines:");
        for (int i = 0; i < lines.length; i++) {
            System.out.println("  " + i + ": [" + lines[i] + "]");
        }

        System.out.println("\n### My hypothesis:");
        System.out.println("When CASE 2 executes (line 2), executeCase iterates:");
        System.out.println("  i=3: 'PRINT \"Case 2\"' -> NOT a CASE line, so execute it");
        System.out.println("  i=4: 'CASE 3' -> IS a CASE line, so call skipToEndSelect() and return");
        System.out.println("But somehow 'PRINT \"Case 3\"' still executes...");
        System.out.println("\n### Actual output:\n");

        try {
            Interpreter interp = new Interpreter();
            interp.loadProgram(code);
            interp.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
