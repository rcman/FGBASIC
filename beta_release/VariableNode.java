/**
 * Represents a reference to a variable (e.g., X, SCORE, COUNT)
 *
 * Variable name is stored uppercase for case-insensitive lookup.
 * Evaluation is a single hash map lookup - much faster than parsing!
 */
public class VariableNode implements ExpressionNode {
    private final String varName;  // Store uppercase name for lookup

    public VariableNode(String varName) {
        this.varName = varName.toUpperCase();
    }

    @Override
    public double evaluate(ExecutionContext context) {
        Double value = context.getVariable(varName);
        return value != null ? value : 0.0;
    }

    @Override
    public String toString() {
        return varName;
    }

    public String getVarName() {
        return varName;
    }
}
