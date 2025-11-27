/**
 * Represents unary operations: -, NOT
 *
 * Stores pre-parsed operand as AST node.
 */
public class UnaryOpNode implements ExpressionNode {
    private final ExpressionNode operand;
    private final UnaryOperator operator;

    public enum UnaryOperator {
        NEGATE, NOT
    }

    public UnaryOpNode(UnaryOperator operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public double evaluate(ExecutionContext context) {
        double value = operand.evaluate(context);

        switch (operator) {
            case NEGATE: return -value;
            case NOT: return value == 0 ? 1.0 : 0.0;
            default: return 0.0;
        }
    }

    @Override
    public String toString() {
        return operator + "(" + operand + ")";
    }

    public ExpressionNode getOperand() {
        return operand;
    }

    public UnaryOperator getOperator() {
        return operator;
    }
}
