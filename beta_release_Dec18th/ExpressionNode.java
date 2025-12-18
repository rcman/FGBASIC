/**
 * Base interface for all expression nodes in the Abstract Syntax Tree.
 * Each node can evaluate itself given the current interpreter state.
 *
 * This is the core of the double-pass optimization:
 * - First pass: Build AST from source code strings
 * - Runtime: Evaluate pre-built AST without re-parsing
 */
public interface ExpressionNode {
    /**
     * Evaluate this expression node and return the numeric result.
     * @param context - The execution context containing variables, arrays, etc.
     * @return The numeric value of this expression
     */
    double evaluate(ExecutionContext context);

    /**
     * Get a string representation for debugging
     */
    String toString();
}
