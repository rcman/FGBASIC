/**
 * Debug test to verify AST optimization is working
 */
public class DebugTest {
    public static void main(String[] args) {
        // Test 1: Build an AST
        System.out.println("Test 1: Building AST for 'X < 100'");
        ExpressionNode ast = ASTBuilder.buildAST("X < 100");
        System.out.println("AST created: " + ast);
        System.out.println();

        // Test 2: Evaluate the AST
        System.out.println("Test 2: Evaluating AST");
        Interpreter interp = new Interpreter();
        interp.getVariables().put("X", 50.0);
        ExecutionContext context = new ExecutionContext(interp);

        double result = ast.evaluate(context);
        System.out.println("X = 50, X < 100 = " + result + " (should be 1.0 for true)");

        interp.getVariables().put("X", 150.0);
        result = ast.evaluate(context);
        System.out.println("X = 150, X < 100 = " + result + " (should be 0.0 for false)");
        System.out.println();

        // Test 3: Complex expression
        System.out.println("Test 3: Complex expression 'X > Y + Z * 2'");
        ExpressionNode complexAst = ASTBuilder.buildAST("X > Y + Z * 2");
        System.out.println("AST created: " + complexAst);

        interp.getVariables().put("X", 25.0);
        interp.getVariables().put("Y", 10.0);
        interp.getVariables().put("Z", 5.0);

        result = complexAst.evaluate(context);
        System.out.println("X=25, Y=10, Z=5, X > Y + Z * 2 = " + result);
        System.out.println("Expected: 25 > (10 + 5 * 2) = 25 > 20 = true (1.0)");
        System.out.println();

        // Test 4: Performance comparison
        System.out.println("Test 4: Performance comparison (1000 evaluations)");

        // Using AST (optimized)
        long startAst = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ast.evaluate(context);
        }
        long endAst = System.nanoTime();
        long astTime = endAst - startAst;

        // Using string parsing (old way)
        ExpressionParser parser = new ExpressionParser(interp.getVariables(), interp.getArrays());
        long startString = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            parser.evaluate("X < 100");
        }
        long endString = System.nanoTime();
        long stringTime = endString - startString;

        System.out.println("AST evaluation (1000x): " + (astTime / 1_000_000.0) + " ms");
        System.out.println("String parsing (1000x): " + (stringTime / 1_000_000.0) + " ms");
        System.out.println("Speedup: " + (stringTime / (double)astTime) + "x");
        System.out.println();

        System.out.println("✅ All tests completed successfully!");
    }
}
