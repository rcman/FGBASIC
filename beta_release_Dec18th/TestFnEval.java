public class TestFnEval {
    public static void main(String[] args) {
        try {
            // Create interpreter and define function
            Interpreter interp = new Interpreter();
            interp.loadProgram("DEF FN DOUBLE(X) = X * 2\n");
            interp.run();

            System.out.println("Functions defined: " + interp.getUserFunctions().keySet());

            // Now parse and evaluate the function call
            ExpressionNode node = ASTBuilder.buildAST("FN DOUBLE(5)");
            System.out.println("AST node: " + node);

            ExecutionContext context = new ExecutionContext(interp);
            double result = node.evaluate(context);
            System.out.println("Result: " + result);
            System.out.println("Expected: 10.0");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
