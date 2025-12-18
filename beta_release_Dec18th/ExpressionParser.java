import java.util.*;

public class ExpressionParser {
    private Map<String, Double> variables;
    private Map<String, double[]> arrays;
    private Map<String, String> stringVars;
    private Map<String, ?> userFunctions;
    private Interpreter interpreter;
    
    public ExpressionParser(Map<String, Double> variables, Map<String, double[]> arrays) {
        this.variables = variables;
        this.arrays = arrays;
        this.stringVars = new HashMap<>();
    }
    
    public void setStringVars(Map<String, String> stringVars) {
        this.stringVars = stringVars;
    }
    
    public void setUserFunctions(Map<String, ?> userFunctions) {
        this.userFunctions = userFunctions;
    }
    
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    public String evaluateString(String expr) {
        expr = expr.trim();
        
        // String literal
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return expr.substring(1, expr.length() - 1);
        }
        
        // String concatenation with +
        if (expr.contains("+") && !expr.startsWith("(")) {
            int depth = 0;
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') depth--;
                else if (c == '+' && depth == 0) {
                    String left = evaluateString(expr.substring(0, i).trim());
                    String right = evaluateString(expr.substring(i + 1).trim());
                    return left + right;
                }
            }
        }
        
        // String functions
        if (expr.toUpperCase().startsWith("LEFT$") || expr.toUpperCase().startsWith("LEFT(")) {
            String args = getFunctionArgs(expr);
            String[] parts = splitArgs(args);
            if (parts.length >= 2) {
                String str = evaluateString(parts[0]);
                int count = (int)evaluate(parts[1]);
                return StringFunctions.left(str, count);
            }
        }
        
        if (expr.toUpperCase().startsWith("RIGHT$") || expr.toUpperCase().startsWith("RIGHT(")) {
            String args = getFunctionArgs(expr);
            String[] parts = splitArgs(args);
            if (parts.length >= 2) {
                String str = evaluateString(parts[0]);
                int count = (int)evaluate(parts[1]);
                return StringFunctions.right(str, count);
            }
        }
        
        if (expr.toUpperCase().startsWith("MID$") || expr.toUpperCase().startsWith("MID(")) {
            String args = getFunctionArgs(expr);
            String[] parts = splitArgs(args);
            if (parts.length >= 3) {
                String str = evaluateString(parts[0]);
                int start = (int)evaluate(parts[1]);
                int count = (int)evaluate(parts[2]);
                return StringFunctions.mid(str, start, count);
            }
        }
        
        if (expr.toUpperCase().startsWith("UPPER$") || expr.toUpperCase().startsWith("UPPER(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.upper(evaluateString(arg));
        }
        
        if (expr.toUpperCase().startsWith("LOWER$") || expr.toUpperCase().startsWith("LOWER(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.lower(evaluateString(arg));
        }
        
        if (expr.toUpperCase().startsWith("TRIM$") || expr.toUpperCase().startsWith("TRIM(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.trim(evaluateString(arg));
        }
        
        if (expr.toUpperCase().startsWith("CHR$") || expr.toUpperCase().startsWith("CHR(")) {
            String arg = getFunctionArgs(expr);
            int code = (int)evaluate(arg);
            return StringFunctions.chr(code);
        }
        
        if (expr.toUpperCase().startsWith("STR$") || expr.toUpperCase().startsWith("STR(")) {
            String arg = getFunctionArgs(expr);
            double num = evaluate(arg);
            return StringFunctions.str(num);
        }
        
        if (expr.toUpperCase().startsWith("REPLACE$") || expr.toUpperCase().startsWith("REPLACE(")) {
            String args = getFunctionArgs(expr);
            String[] parts = splitArgs(args);
            if (parts.length >= 3) {
                String str = evaluateString(parts[0]);
                String oldStr = evaluateString(parts[1]);
                String newStr = evaluateString(parts[2]);
                return StringFunctions.replace(str, oldStr, newStr);
            }
        }
        
        if (expr.toUpperCase().startsWith("REVERSE$") || expr.toUpperCase().startsWith("REVERSE(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.reverse(evaluateString(arg));
        }
        
        // String variable
        String varName = expr.toUpperCase();
        if (stringVars.containsKey(varName)) {
            return stringVars.get(varName);
        }
        
        return "";
    }
    
    private String[] splitArgs(String args) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        
        for (char c : args.toCharArray()) {
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
        
        return result.toArray(new String[0]);
    }
    
    public double evaluate(String expr) {
        expr = expr.trim();
        
        if (expr.isEmpty()) return 0.0;
        
        // Handle string literals
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return 0.0;
        }
        
        try {
            return parseExpression(expr);
        } catch (Exception e) {
            System.err.println("Expression error: " + expr + " - " + e.getMessage());
            return 0.0;
        }
    }
    
    private double parseExpression(String expr) {
        return parseOr(expr);
    }
    
    private double parseOr(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;
            
            if (level == 0 && i >= 2) {
                String sub = expr.substring(i - 2, i + 1).toUpperCase();
                if (sub.equals(" OR")) {
                    double left = parseOr(expr.substring(0, i - 2).trim());
                    double right = parseAnd(expr.substring(i + 1).trim());
                    return (left != 0 || right != 0) ? 1.0 : 0.0;
                }
            }
        }
        return parseAnd(expr);
    }
    
    private double parseAnd(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;
            
            if (level == 0 && i >= 3) {
                String sub = expr.substring(i - 3, i + 1).toUpperCase();
                if (sub.equals(" AND")) {
                    double left = parseAnd(expr.substring(0, i - 3).trim());
                    double right = parseComparison(expr.substring(i + 1).trim());
                    return (left != 0 && right != 0) ? 1.0 : 0.0;
                }
            }
        }
        return parseComparison(expr);
    }
    
    private double parseComparison(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;

            if (level == 0) {
                // Check two-character operators BEFORE single-character ones
                if (i > 0 && expr.charAt(i - 1) == '>' && expr.charAt(i) == '=') {
                    String leftStr = expr.substring(0, i - 1).trim();
                    String rightStr = expr.substring(i + 1).trim();
                    double left = parseAddSub(leftStr);
                    double right = parseAddSub(rightStr);
                    return left >= right ? 1.0 : 0.0;
                }
                if (i > 0 && expr.charAt(i - 1) == '<' && expr.charAt(i) == '=') {
                    String leftStr = expr.substring(0, i - 1).trim();
                    String rightStr = expr.substring(i + 1).trim();
                    double left = parseAddSub(leftStr);
                    double right = parseAddSub(rightStr);
                    return left <= right ? 1.0 : 0.0;
                }
                if (i > 0 && expr.charAt(i - 1) == '<' && expr.charAt(i) == '>') {
                    String leftStr = expr.substring(0, i - 1).trim();
                    String rightStr = expr.substring(i + 1).trim();
                    double left = parseAddSub(leftStr);
                    double right = parseAddSub(rightStr);
                    return left != right ? 1.0 : 0.0;
                }
                // Now check single-character operators
                // Ensure we're not part of a two-character operator
                boolean notPartOfTwoChar = (i == 0 || (expr.charAt(i-1) != '<' && expr.charAt(i-1) != '>')) &&
                                          (i >= expr.length() - 1 || (c != '<' && c != '>') || expr.charAt(i+1) != '=');

                if (c == '>' && notPartOfTwoChar) {
                    double left = parseAddSub(expr.substring(0, i).trim());
                    double right = parseAddSub(expr.substring(i + 1).trim());
                    return left > right ? 1.0 : 0.0;
                }
                if (c == '<' && notPartOfTwoChar) {
                    double left = parseAddSub(expr.substring(0, i).trim());
                    double right = parseAddSub(expr.substring(i + 1).trim());
                    return left < right ? 1.0 : 0.0;
                }
                if (c == '=' && (i == 0 || expr.charAt(i-1) != '<' && expr.charAt(i-1) != '>')) {
                    double left = parseAddSub(expr.substring(0, i).trim());
                    double right = parseAddSub(expr.substring(i + 1).trim());
                    return Math.abs(left - right) < 0.0001 ? 1.0 : 0.0;
                }
            }
        }
        return parseAddSub(expr);
    }
    
    private double parseAddSub(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;
            
            if (level == 0 && i > 0) {
                if (c == '+') {
                    return parseAddSub(expr.substring(0, i).trim()) +
                           parseMulDiv(expr.substring(i + 1).trim());
                }
                if (c == '-') {
                    // Check if this is subtraction (previous char is digit/space/)) or negative number (previous char is operator)
                    char prev = expr.charAt(i - 1);
                    if (Character.isDigit(prev) || prev == ')' || Character.isWhitespace(prev)) {
                        return parseAddSub(expr.substring(0, i).trim()) -
                               parseMulDiv(expr.substring(i + 1).trim());
                    }
                    // Otherwise it's a negative number, continue scanning
                }
            }
        }
        return parseMulDiv(expr);
    }
    
    private double parseMulDiv(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;
            
            if (level == 0 && i > 0) {
                if (c == '*') {
                    return parseMulDiv(expr.substring(0, i).trim()) * 
                           parsePower(expr.substring(i + 1).trim());
                }
                if (c == '/') {
                    double divisor = parsePower(expr.substring(i + 1).trim());
                    if (Math.abs(divisor) < 0.0000001) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return parseMulDiv(expr.substring(0, i).trim()) / divisor;
                }
                if (i >= 3 && expr.substring(i - 2, i + 1).equalsIgnoreCase("MOD")) {
                    double divisor = parsePower(expr.substring(i + 1).trim());
                    if (Math.abs(divisor) < 0.0000001) {
                        throw new ArithmeticException("Modulo by zero");
                    }
                    return parseMulDiv(expr.substring(0, i - 2).trim()) % divisor;
                }
            }
        }
        return parsePower(expr);
    }
    
    private double parsePower(String expr) {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            if (c == '(') level--;
            
            if (level == 0 && c == '^') {
                return Math.pow(parseUnary(expr.substring(0, i).trim()), 
                               parsePower(expr.substring(i + 1).trim()));
            }
        }
        return parseUnary(expr);
    }
    
    private double parseUnary(String expr) {
        if (expr.startsWith("-")) {
            return -parsePrimary(expr.substring(1).trim());
        }
        if (expr.toUpperCase().startsWith("NOT ")) {
            return parsePrimary(expr.substring(4).trim()) == 0 ? 1.0 : 0.0;
        }
        return parsePrimary(expr);
    }
    
    private double parsePrimary(String expr) {
        // Parentheses
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return parseExpression(expr.substring(1, expr.length() - 1));
        }
        
        // String functions that return numbers
        if (expr.toUpperCase().startsWith("LEN(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.len(evaluateString(arg));
        }
        if (expr.toUpperCase().startsWith("ASC(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.asc(evaluateString(arg));
        }
        if (expr.toUpperCase().startsWith("VAL(")) {
            String arg = getFunctionArgs(expr);
            return StringFunctions.val(evaluateString(arg));
        }
        if (expr.toUpperCase().startsWith("INSTR(")) {
            String args = getFunctionArgs(expr);
            String[] parts = splitArgs(args);
            if (parts.length >= 2) {
                return StringFunctions.instr(evaluateString(parts[0]), evaluateString(parts[1]));
            }
        }
        
        // Math Functions
        if (expr.toUpperCase().startsWith("ABS(")) {
            return Math.abs(parseExpression(getFunctionArgs(expr)));
        }
        if (expr.toUpperCase().startsWith("SIN(")) {
            return Math.sin(Math.toRadians(parseExpression(getFunctionArgs(expr))));
        }
        if (expr.toUpperCase().startsWith("COS(")) {
            return Math.cos(Math.toRadians(parseExpression(getFunctionArgs(expr))));
        }
        if (expr.toUpperCase().startsWith("TAN(")) {
            return Math.tan(Math.toRadians(parseExpression(getFunctionArgs(expr))));
        }
        if (expr.toUpperCase().startsWith("ATN(")) {
            return Math.toDegrees(Math.atan(parseExpression(getFunctionArgs(expr))));
        }
        if (expr.toUpperCase().startsWith("SQR(")) {
            return Math.sqrt(parseExpression(getFunctionArgs(expr)));
        }
        if (expr.toUpperCase().startsWith("INT(")) {
            return Math.floor(parseExpression(getFunctionArgs(expr)));
        }
        if (expr.toUpperCase().equals("RND")) {
            // RND without arguments returns 0.0 to 1.0
            return Math.random();
        }
        if (expr.toUpperCase().startsWith("RND(")) {
            // RND(max) returns 0.0 to max
            return Math.random() * parseExpression(getFunctionArgs(expr));
        }
        if (expr.toUpperCase().startsWith("SGN(")) {
            double val = parseExpression(getFunctionArgs(expr));
            return val > 0 ? 1.0 : (val < 0 ? -1.0 : 0.0);
        }
        
        // Array access
        if (expr.contains("(") && expr.endsWith(")")) {
            int parenPos = expr.indexOf('(');
            String arrayName = expr.substring(0, parenPos).trim().toUpperCase();
            String indexExpr = expr.substring(parenPos + 1, expr.length() - 1);
            
            if (arrays.containsKey(arrayName)) {
                int index = (int)parseExpression(indexExpr);
                double[] array = arrays.get(arrayName);
                if (index >= 0 && index < array.length) {
                    return array[index];
                } else {
                    throw new RuntimeException("Array index out of bounds: " + index + 
                        " (array size: " + array.length + ")");
                }
            }
        }
        
        // Number literal
        try {
            return Double.parseDouble(expr);
        } catch (NumberFormatException e) {
            // Variable
            String varName = expr.toUpperCase();
            return variables.getOrDefault(varName, 0.0);
        }
    }
    
    private String getFunctionArgs(String expr) {
        int openParen = expr.indexOf('(');
        if (openParen == -1) return "";
        return expr.substring(openParen + 1, expr.length() - 1);
    }
}
