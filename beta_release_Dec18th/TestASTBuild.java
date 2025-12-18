public class TestASTBuild {
    public static void main(String[] args) {
        String expr = "FN DOUBLE(5)";
        System.out.println("Parsing expression: " + expr);

        try {
            ExpressionNode node = ASTBuilder.buildAST(expr);
            System.out.println("AST node: " + node);
            System.out.println("Node type: " + node.getClass().getName());

            if (node instanceof FunctionCallNode) {
                FunctionCallNode fn = (FunctionCallNode) node;
                System.out.println("Function name: " + fn.getFunctionName());
                System.out.println("Arguments: " + fn.getArguments());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
