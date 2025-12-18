/**
 * Represents a constant numeric value (e.g., 42, 3.14, -5.5)
 *
 * This is the most efficient node type - O(1) evaluation with no lookups.
 */
public class ConstantNode implements ExpressionNode {
    private final double value;

    public ConstantNode(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(ExecutionContext context) {
        return value;  // O(1) - no parsing, no lookups!
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public double getValue() {
        return value;
    }
}
