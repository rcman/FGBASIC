import java.util.*;

/**
 * Represents built-in function calls: SIN, COS, ABS, SQR, RND, etc.
 *
 * Function arguments are pre-parsed as AST nodes.
 * Evaluation evaluates all arguments first, then calls the function.
 */
public class FunctionCallNode implements ExpressionNode {
    private final String functionName;
    private final List<ExpressionNode> arguments;

    public FunctionCallNode(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName.toUpperCase();
        this.arguments = arguments;
    }

    @Override
    public double evaluate(ExecutionContext context) {
        // Evaluate all arguments first
        double[] args = new double[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            args[i] = arguments.get(i).evaluate(context);
        }

        // Call built-in function
        switch (functionName) {
            case "SIN": return args.length > 0 ? Math.sin(args[0]) : 0.0;
            case "COS": return args.length > 0 ? Math.cos(args[0]) : 0.0;
            case "TAN": return args.length > 0 ? Math.tan(args[0]) : 0.0;
            case "ABS": return args.length > 0 ? Math.abs(args[0]) : 0.0;
            case "INT": return args.length > 0 ? Math.floor(args[0]) : 0.0;
            case "SQR": return args.length > 0 ? Math.sqrt(args[0]) : 0.0;
            case "LOG": return args.length > 0 ? Math.log(args[0]) : 0.0;
            case "EXP": return args.length > 0 ? Math.exp(args[0]) : 0.0;
            case "ATN": return args.length > 0 ? Math.atan(args[0]) : 0.0;
            case "SGN":
                if (args.length > 0) {
                    return args[0] > 0 ? 1.0 : (args[0] < 0 ? -1.0 : 0.0);
                }
                return 0.0;
            case "RND":
                return context.getInterpreter().getRandom().nextDouble();
            case "LEN":
                // String function - would need string context
                return 0.0;
            default:
                System.err.println("Unknown function: " + functionName);
                return 0.0;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(functionName).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(arguments.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }
}
