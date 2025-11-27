/**
 * Represents binary operations: +, -, *, /, ^, AND, OR, comparisons
 *
 * Stores pre-parsed left and right sub-expressions as AST nodes.
 * Evaluation recursively evaluates children then applies operator.
 */
public class BinaryOpNode implements ExpressionNode {
    private final ExpressionNode left;
    private final ExpressionNode right;
    private final BinaryOperator operator;

    public enum BinaryOperator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER, MODULO,
        EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL,
        AND, OR
    }

    public BinaryOpNode(ExpressionNode left, BinaryOperator operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public double evaluate(ExecutionContext context) {
        double leftVal = left.evaluate(context);   // Recursive evaluation
        double rightVal = right.evaluate(context);

        switch (operator) {
            case ADD: return leftVal + rightVal;
            case SUBTRACT: return leftVal - rightVal;
            case MULTIPLY: return leftVal * rightVal;
            case DIVIDE: return rightVal != 0 ? leftVal / rightVal : 0.0;
            case POWER: return Math.pow(leftVal, rightVal);
            case MODULO: return leftVal % rightVal;
            case EQUAL: return leftVal == rightVal ? 1.0 : 0.0;
            case NOT_EQUAL: return leftVal != rightVal ? 1.0 : 0.0;
            case LESS_THAN: return leftVal < rightVal ? 1.0 : 0.0;
            case GREATER_THAN: return leftVal > rightVal ? 1.0 : 0.0;
            case LESS_EQUAL: return leftVal <= rightVal ? 1.0 : 0.0;
            case GREATER_EQUAL: return leftVal >= rightVal ? 1.0 : 0.0;
            case AND: return (leftVal != 0 && rightVal != 0) ? 1.0 : 0.0;
            case OR: return (leftVal != 0 || rightVal != 0) ? 1.0 : 0.0;
            default: return 0.0;
        }
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public BinaryOperator getOperator() {
        return operator;
    }
}
