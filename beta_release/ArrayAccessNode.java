import java.util.*;

/**
 * Represents array element access: A(I), SCORES(X, Y)
 *
 * Array name and index expressions are pre-parsed.
 */
public class ArrayAccessNode implements ExpressionNode {
    private final String arrayName;
    private final List<ExpressionNode> indices;

    public ArrayAccessNode(String arrayName, List<ExpressionNode> indices) {
        this.arrayName = arrayName.toUpperCase();
        this.indices = indices;
    }

    @Override
    public double evaluate(ExecutionContext context) {
        if (indices.isEmpty()) {
            return 0.0;
        }

        // Evaluate index expression
        int index = (int) indices.get(0).evaluate(context);

        double[] array = context.getArray(arrayName);
        if (array != null && index >= 0 && index < array.length) {
            return array[index];
        }
        return 0.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(arrayName).append("(");
        for (int i = 0; i < indices.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(indices.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    public String getArrayName() {
        return arrayName;
    }

    public List<ExpressionNode> getIndices() {
        return indices;
    }
}
