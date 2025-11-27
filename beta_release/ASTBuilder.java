import java.util.*;

/**
 * Builds Abstract Syntax Trees from BASIC expression strings.
 *
 * This is the "first pass" of the double-pass optimization.
 * Parses expression strings once and creates reusable AST nodes.
 */
public class ASTBuilder {

    /**
     * Build an AST from an expression string
     */
    public static ExpressionNode buildAST(String expr) {
        expr = expr.trim();

        if (expr.isEmpty()) {
            return new ConstantNode(0.0);
        }

        try {
            return parseExpression(expr);
        } catch (Exception e) {
            System.err.println("AST build error: " + expr + " - " + e.getMessage());
            return new ConstantNode(0.0);
        }
    }

    private static ExpressionNode parseExpression(String expr) {
        return parseOr(expr);
    }

    private static ExpressionNode parseOr(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0 && i >= 2) {
                String sub = expr.substring(i - 2, i + 1).toUpperCase();
                if (sub.equals(" OR")) {
                    ExpressionNode left = parseOr(expr.substring(0, i - 2).trim());
                    ExpressionNode right = parseAnd(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.OR, right);
                }
            }
        }
        return parseAnd(expr);
    }

    private static ExpressionNode parseAnd(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0 && i >= 3) {
                String sub = expr.substring(i - 3, i + 1).toUpperCase();
                if (sub.equals(" AND")) {
                    ExpressionNode left = parseAnd(expr.substring(0, i - 3).trim());
                    ExpressionNode right = parseComparison(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.AND, right);
                }
            }
        }
        return parseComparison(expr);
    }

    private static ExpressionNode parseComparison(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0) {
                // Check two-character operators BEFORE single-character ones
                if (i > 0 && expr.charAt(i - 1) == '>' && expr.charAt(i) == '=') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i - 1).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.GREATER_EQUAL, right);
                }
                if (i > 0 && expr.charAt(i - 1) == '<' && expr.charAt(i) == '=') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i - 1).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.LESS_EQUAL, right);
                }
                if (i > 0 && expr.charAt(i - 1) == '<' && expr.charAt(i) == '>') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i - 1).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.NOT_EQUAL, right);
                }

                // Single-character comparison operators
                if (c == '>') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.GREATER_THAN, right);
                }
                if (c == '<') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.LESS_THAN, right);
                }
                if (c == '=') {
                    ExpressionNode left = parseAddSub(expr.substring(0, i).trim());
                    ExpressionNode right = parseAddSub(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.EQUAL, right);
                }
            }
        }
        return parseAddSub(expr);
    }

    private static ExpressionNode parseAddSub(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0) {
                if (c == '+' && i > 0) {
                    ExpressionNode left = parseAddSub(expr.substring(0, i).trim());
                    ExpressionNode right = parseMulDiv(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.ADD, right);
                }
                if (c == '-' && i > 0 && !isOperator(expr.charAt(i - 1))) {
                    ExpressionNode left = parseAddSub(expr.substring(0, i).trim());
                    ExpressionNode right = parseMulDiv(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.SUBTRACT, right);
                }
            }
        }
        return parseMulDiv(expr);
    }

    private static ExpressionNode parseMulDiv(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0) {
                if (c == '*') {
                    ExpressionNode left = parseMulDiv(expr.substring(0, i).trim());
                    ExpressionNode right = parseUnary(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.MULTIPLY, right);
                }
                if (c == '/') {
                    ExpressionNode left = parseMulDiv(expr.substring(0, i).trim());
                    ExpressionNode right = parseUnary(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.DIVIDE, right);
                }
                if (c == '%') {
                    ExpressionNode left = parseMulDiv(expr.substring(0, i).trim());
                    ExpressionNode right = parseUnary(expr.substring(i + 1).trim());
                    return new BinaryOpNode(left, BinaryOpNode.BinaryOperator.MODULO, right);
                }
            }
        }
        return parseUnary(expr);
    }

    private static ExpressionNode parseUnary(String expr) {
        expr = expr.trim();

        // Unary minus
        if (expr.startsWith("-")) {
            ExpressionNode operand = parseUnary(expr.substring(1).trim());
            return new UnaryOpNode(UnaryOpNode.UnaryOperator.NEGATE, operand);
        }

        // NOT operator
        if (expr.toUpperCase().startsWith("NOT ")) {
            ExpressionNode operand = parseUnary(expr.substring(4).trim());
            return new UnaryOpNode(UnaryOpNode.UnaryOperator.NOT, operand);
        }

        return parsePrimary(expr);
    }

    private static ExpressionNode parsePrimary(String expr) {
        expr = expr.trim();

        // Parenthesized expression
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return parseExpression(expr.substring(1, expr.length() - 1));
        }

        // Check for function call or array access
        int parenPos = expr.indexOf('(');
        if (parenPos > 0 && expr.endsWith(")")) {
            String name = expr.substring(0, parenPos).trim().toUpperCase();
            String argsStr = expr.substring(parenPos + 1, expr.length() - 1);

            // Check if it's a known function
            if (isBuiltInFunction(name)) {
                List<ExpressionNode> args = parseArgumentList(argsStr);
                return new FunctionCallNode(name, args);
            } else {
                // It's an array access
                List<ExpressionNode> indices = parseArgumentList(argsStr);
                return new ArrayAccessNode(name, indices);
            }
        }

        // Try to parse as number
        try {
            double value = Double.parseDouble(expr);
            return new ConstantNode(value);
        } catch (NumberFormatException e) {
            // Not a number, must be a variable
        }

        // Variable reference
        return new VariableNode(expr);
    }

    private static List<ExpressionNode> parseArgumentList(String argsStr) {
        List<ExpressionNode> args = new ArrayList<>();
        if (argsStr.trim().isEmpty()) {
            return args;
        }

        // Split by commas, respecting parentheses
        List<String> argStrings = splitArguments(argsStr);
        for (String argStr : argStrings) {
            args.add(parseExpression(argStr.trim()));
        }

        return args;
    }

    private static List<String> splitArguments(String argsStr) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (char c : argsStr.toCharArray()) {
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ',' && depth == 0) {
                result.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }
        result.add(current.toString().trim());

        return result;
    }

    private static boolean isBuiltInFunction(String name) {
        switch (name) {
            case "SIN": case "COS": case "TAN": case "ABS": case "INT":
            case "SQR": case "LOG": case "EXP": case "ATN": case "SGN": case "RND":
            case "LEN": case "ASC": case "VAL":
                return true;
            default:
                return false;
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == '<' || c == '>' || c == '=';
    }
}
