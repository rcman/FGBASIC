/**
 * Test nested loop behavior
 */
public class LoopTest {
    public static void main(String[] args) {
        Interpreter interp = new Interpreter();

        System.out.println("Test 1: Simple FOR loop");
        String prog1 = "LET COUNT = 0\n" +
                      "FOR I = 0 TO 5\n" +
                      "  LET COUNT = COUNT + 1\n" +
                      "  PRINT \"I=\", I, \" COUNT=\", COUNT\n" +
                      "NEXT I\n" +
                      "PRINT \"Final: \", COUNT";
        try {
            interp.loadProgram(prog1);
            interp.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nTest 2: Nested FOR loops");
        interp.clear();
        String prog2 = "LET COUNT = 0\n" +
                      "FOR I = 0 TO 2\n" +
                      "  PRINT \"Outer I=\", I\n" +
                      "  FOR J = 0 TO 2\n" +
                      "    LET COUNT = COUNT + 1\n" +
                      "    PRINT \"  Inner J=\", J, \" COUNT=\", COUNT\n" +
                      "  NEXT J\n" +
                      "NEXT I\n" +
                      "PRINT \"Final: \", COUNT";
        try {
            interp.loadProgram(prog2);
            interp.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
