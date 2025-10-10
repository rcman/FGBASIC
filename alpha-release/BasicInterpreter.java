import java.util.*;
import java.util.List;
import java.util.regex.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.ImageIO;

/**
 * FGBasic - GPU-Accelerated BASIC Interpreter (Java 24)
 * Version 3.0.3 - WITH IMPROVED TOKENIZATION
 */
public class BasicInterpreter {
    
    private final Map<Integer, String> program = new TreeMap<>();
    private final Map<String, Integer> labels = new HashMap<>();
    private final Map<String, TypedValue> variables = new HashMap<>();
    private final Map<String, TypedArray> arrays = new HashMap<>();
    private final Stack<Integer> gosubStack = new Stack<>();
    private final Stack<ForLoopContext> forStack = new Stack<>();
    private final Stack<WhileLoopContext> whileStack = new Stack<>();
    private final Queue<String> dataQueue = new LinkedList<>();
    private int currentLine = 0;
    private int autoLineNumber = 10;
    volatile boolean running = false;
    private final Random random = new Random();
    
    private final GraphicsEngine graphics;
    private final SoundEngine sound;
    private final SpriteEngine sprites;
    private BasicIDE ide;
    
    public BasicInterpreter() {
        graphics = new GraphicsEngine();
        sound = new SoundEngine();
        sprites = new SpriteEngine(graphics);
    }
    
    public void startIDE() {
        SwingUtilities.invokeLater(() -> {
            ide = new BasicIDE(this);
            ide.setVisible(true);
        });
    }
    
    enum VarType {
        BYTE('b', -128, 127, 1),
        WORD('w', -32768, 32767, 2),
        LONG('l', -2147483648L, 2147483647L, 4),
        QUICK('q', -32768.0, 32767.9999, 2),
        FLOAT('f', -9e18, 9e18, 4),
        STRING('$', 0, 0, 0);
        
        final char suffix;
        final double min, max;
        final int bytes;
        
        VarType(char suffix, double min, double max, int bytes) {
            this.suffix = suffix;
            this.min = min;
            this.max = max;
            this.bytes = bytes;
        }
    }
    
    static final class TypedValue {
        final VarType type;
        private Object value;
        
        TypedValue(VarType type, Object value) {
            this.type = type;
            setValue(value);
        }
        
        void setValue(Object val) {
            if (type == VarType.STRING) {
                this.value = val.toString();
            } else {
                double d = ((Number) val).doubleValue();
                this.value = switch (type) {
                    case BYTE -> {
                        d = Math.clamp(d, type.min, type.max);
                        yield (byte) d;
                    }
                    case WORD -> {
                        d = Math.clamp(d, type.min, type.max);
                        yield (short) d;
                    }
                    case LONG -> {
                        d = Math.clamp(d, type.min, type.max);
                        yield (int) d;
                    }
                    case QUICK -> {
                        d = Math.clamp(d, type.min, type.max);
                        yield (int) (d * 65536);
                    }
                    case FLOAT -> d;
                    default -> d;
                };
            }
        }
        
        Object getValue() {
            return type == VarType.QUICK ? ((int) value) / 65536.0 : value;
        }
        
        double getNumeric() {
            if (type == VarType.STRING) return 0.0;
            if (type == VarType.QUICK) return ((int) value) / 65536.0;
            return ((Number) value).doubleValue();
        }
    }
    
    static final class TypedArray {
        final VarType type;
        final Object[] values;
        
        TypedArray(VarType type, int size) {
            this.type = type;
            this.values = new Object[size];
        }
    }
    
    private VarType getVarType(String varName) {
        if (varName.endsWith("$")) return VarType.STRING;
        if (varName.endsWith(".b")) return VarType.BYTE;
        if (varName.endsWith(".w")) return VarType.WORD;
        if (varName.endsWith(".l")) return VarType.LONG;
        if (varName.endsWith(".q")) return VarType.QUICK;
        if (varName.endsWith(".f")) return VarType.FLOAT;
        return VarType.FLOAT;
    }
    
    private String normalizeVarName(String varName) {
        return varName.toUpperCase();
    }
    
    public void executeProgram(String code) {
        program.clear();
        labels.clear();
        variables.clear();
        arrays.clear();
        autoLineNumber = 10;
        
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (line.startsWith("@")) {
                String labelName = line.substring(1).trim().toUpperCase();
                if (labelName.contains(" ")) {
                    labelName = labelName.substring(0, labelName.indexOf(" "));
                }
                labels.put(labelName, autoLineNumber);
                line = line.substring(line.indexOf(" ") + 1).trim();
                if (!line.isEmpty()) {
                    program.put(autoLineNumber, line);
                    autoLineNumber += 10;
                }
            } else if (line.contains(":") && !line.contains("\"")) {
                int colonPos = line.indexOf(":");
                String labelName = line.substring(0, colonPos).trim().toUpperCase();
                if (!labelName.matches("\\d+")) {
                    labels.put(labelName, autoLineNumber);
                    String rest = line.substring(colonPos + 1).trim();
                    if (!rest.isEmpty()) {
                        program.put(autoLineNumber, rest);
                        autoLineNumber += 10;
                    }
                } else {
                    processLine(line);
                }
            } else {
                processLine(line);
            }
        }
        
        runProgram();
    }
    
    private void processLine(String line) {
        Pattern pattern = Pattern.compile("^(\\d+)\\s+(.*)$");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.matches()) {
            int lineNum = Integer.parseInt(matcher.group(1));
            String code = matcher.group(2).trim();
            
            if (code.isEmpty()) {
                program.remove(lineNum);
            } else {
                program.put(lineNum, code);
            }
        } else if (!line.trim().isEmpty()) {
            program.put(autoLineNumber, line);
            autoLineNumber += 10;
        }
    }
    
    private void runProgram() {
        if (program.isEmpty()) {
            logOutput("No program to run\n");
            return;
        }
        
        running = true;
        gosubStack.clear();
        forStack.clear();
        whileStack.clear();
        dataQueue.clear();
        
        for (String line : program.values()) {
            if (line.toUpperCase().startsWith("DATA ")) {
                String[] items = line.substring(5).split(",");
                for (String item : items) {
                    dataQueue.offer(item.trim());
                }
            }
        }
        
        List<Integer> lineNumbers = new ArrayList<>(program.keySet());
        int index = 0;
        
        try {
            while (running && index < lineNumbers.size()) {
                currentLine = lineNumbers.get(index);
                String line = program.get(currentLine);
                
                Integer nextLine = executeLine(line);
                
                if (nextLine != null) {
                    index = lineNumbers.indexOf(nextLine);
                    if (index < 0) {
                        throw new RuntimeException("Line " + nextLine + " not found");
                    }
                } else {
                    index++;
                }
            }
        } catch (Exception e) {
            logError("Error at line " + currentLine + ": " + e.getMessage() + "\n");
            e.printStackTrace();
        }
        
        running = false;
    }
    
    private Integer executeLine(String line) throws Exception {
        String[] tokens = tokenize(line);
        if (tokens.length == 0) return null;
        
        String cmd = tokens[0].toUpperCase();
        
        return switch (cmd) {
            case "PRINT" -> { executePrint(tokens); yield null; }
            case "LET" -> { executeLet(tokens); yield null; }
            case "INPUT" -> { executeInput(tokens); yield null; }
            case "GOTO" -> resolveLabel(tokens[1]);
            case "GOSUB" -> {
                gosubStack.push(currentLine);
                yield resolveLabel(tokens[1]);
            }
            case "RETURN" -> {
                if (gosubStack.isEmpty()) {
                    throw new RuntimeException("RETURN without GOSUB");
                }
                int retLine = gosubStack.pop();
                List<Integer> lines = new ArrayList<>(program.keySet());
                int idx = lines.indexOf(retLine);
                if (idx >= 0 && idx < lines.size() - 1) {
                    yield lines.get(idx + 1);
                }
                running = false;
                yield null;
            }
            case "IF" -> executeIf(line);
            case "FOR" -> { executeFor(tokens); yield null; }
            case "NEXT" -> executeNext(tokens);
            case "WHILE" -> { executeWhile(line); yield null; }
            case "WEND" -> executeWend();
            case "WAIT", "SLEEP" -> { 
                if (tokens.length >= 2) {
                    int ms = ((Number) evaluateExpression(tokens[1])).intValue();
                    Thread.sleep(ms);
                }
                yield null;
            }
            case "END" -> { running = false; yield null; }
            case "REM" -> null;
            case "CLS" -> { graphics.cls(); yield null; }
            case "READ" -> { executeRead(tokens); yield null; }
            case "DATA" -> null;
            case "DIM" -> { executeDim(tokens); yield null; }
            case "TEXT" -> { executeText(tokens); yield null; }
            case "LINE", "CIRCLE", "BOX", "PIXEL", "COLOUR", "COLOR", "MODE", "SCREEN" -> {
                executeGraphicsCommand(tokens);
                yield null;
            }
            case "PLAY" -> { executeSoundCommand(tokens); yield null; }
            case "SPRITE" -> { executeSpriteCommand(line, tokens); yield null; }
            default -> {
                if (line.contains("=")) {
                    executeLet(tokens);
                    yield null;
                } else {
                    throw new RuntimeException("Unknown command: " + cmd);
                }
            }
        };
    }
    
    private void executeWhile(String line) {
        String condition = line.substring(5).trim();
        whileStack.push(new WhileLoopContext(currentLine, condition));
        
        if (!evaluateCondition(condition)) {
            int depth = 1;
            List<Integer> lineNumbers = new ArrayList<>(program.keySet());
            int index = lineNumbers.indexOf(currentLine) + 1;
            
            while (index < lineNumbers.size()) {
                String checkLine = program.get(lineNumbers.get(index)).toUpperCase().trim();
                if (checkLine.startsWith("WHILE")) depth++;
                if (checkLine.equals("WEND")) {
                    depth--;
                    if (depth == 0) {
                        whileStack.pop();
                        break;
                    }
                }
                index++;
            }
        }
    }
    
    private Integer executeWend() {
        if (whileStack.isEmpty()) {
            throw new RuntimeException("WEND without WHILE");
        }
        
        WhileLoopContext ctx = whileStack.peek();
        
        if (evaluateCondition(ctx.condition)) {
            return ctx.startLine;
        } else {
            whileStack.pop();
            return null;
        }
    }
    
    private void executeText(String[] tokens) {
        graphics.executeTextCommand(tokens, this);
    }
    
    private Integer resolveLabel(String target) {
        target = target.trim().toUpperCase();
        
        if (labels.containsKey(target)) {
            return labels.get(target);
        }
        
        try {
            return Integer.parseInt(target);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unknown label or line: " + target);
        }
    }
    
    private String[] tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inString = !inString;
                current.append(c);
            } else if (inString) {
                current.append(c);
            } else {
                if (isDelimiter(c)) {
                    if (!current.isEmpty()) {
                        tokens.add(current.toString());
                        current = new StringBuilder();
                    }
                    
                    if (i < line.length() - 1) {
                        char next = line.charAt(i + 1);
                        String twoChar = "" + c + next;
                        
                        if (twoChar.equals("<=") || twoChar.equals(">=") || twoChar.equals("<>")) {
                            tokens.add(twoChar);
                            i++;
                            continue;
                        }
                    }
                    
                    if (c == ',' || c == ';') {
                        tokens.add(String.valueOf(c));
                    } else if (c == '=' || c == '<' || c == '>' || 
                              c == '+' || c == '-' || c == '*' || c == '/' || c == '^' ||
                              c == '(' || c == ')') {
                        tokens.add(String.valueOf(c));
                    }
                } else {
                    current.append(c);
                }
            }
        }
        
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        
        return tokens.toArray(String[]::new);
    }
    
    private boolean isDelimiter(char c) {
        return c == ' ' || c == ',' || c == ';' || c == '=' || 
               c == '<' || c == '>' || c == '+' || c == '-' || 
               c == '*' || c == '/' || c == '^' || c == '(' || c == ')';
    }
    
    private List<String> filterCommas(String[] tokens, int startIdx) {
        List<String> params = new ArrayList<>();
        for (int i = startIdx; i < tokens.length; i++) {
            if (!tokens[i].equals(",") && !tokens[i].equals(";")) {
                params.add(tokens[i]);
            }
        }
        return params;
    }
    
    private void executeSpriteCommand(String line, String[] tokens) {
        if (tokens.length < 2) return;
        
        String subCmd = tokens[1].toUpperCase();
        
        try {
            List<String> params = filterCommas(tokens, 2);
            
            switch (subCmd) {
                case "LOAD" -> {
                    if (params.size() >= 2) {
                        int loadNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        String filename = evaluateExpression(params.get(1)).toString();
                        sprites.loadSprite(loadNum, filename);
                    }
                }
                case "CREATE" -> {
                    if (params.size() >= 3) {
                        int createNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        int width = ((Number) evaluateExpression(params.get(1))).intValue();
                        int height = ((Number) evaluateExpression(params.get(2))).intValue();
                        long color = params.size() > 3 ? 
                            ((Number) evaluateExpression(params.get(3))).longValue() : 0xFFFFFFFFL;
                        sprites.createSprite(createNum, width, height, (int)(color & 0xFFFFFFFFL));
                    }
                }
                case "SHOW" -> {
                    if (!params.isEmpty()) {
                        sprites.showSprite(((Number) evaluateExpression(params.get(0))).intValue());
                    }
                }
                case "HIDE" -> {
                    if (!params.isEmpty()) {
                        sprites.hideSprite(((Number) evaluateExpression(params.get(0))).intValue());
                    }
                }
                case "MOVE" -> {
                    if (params.size() >= 3) {
                        int moveNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        double x = ((Number) evaluateExpression(params.get(1))).doubleValue();
                        double y = ((Number) evaluateExpression(params.get(2))).doubleValue();
                        sprites.moveSprite(moveNum, x, y);
                    }
                }
                case "MOVEBY" -> {
                    if (params.size() >= 3) {
                        int moveByNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        double dx = ((Number) evaluateExpression(params.get(1))).doubleValue();
                        double dy = ((Number) evaluateExpression(params.get(2))).doubleValue();
                        sprites.moveSpriteBy(moveByNum, dx, dy);
                    }
                }
                case "SCALE" -> {
                    if (params.size() >= 2) {
                        int scaleNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        double scaleX = ((Number) evaluateExpression(params.get(1))).doubleValue();
                        double scaleY = params.size() > 2 ? 
                            ((Number) evaluateExpression(params.get(2))).doubleValue() : scaleX;
                        sprites.scaleSprite(scaleNum, scaleX, scaleY);
                    }
                }
                case "ROTATE" -> {
                    if (params.size() >= 2) {
                        int rotNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        double angle = ((Number) evaluateExpression(params.get(1))).doubleValue();
                        sprites.rotateSprite(rotNum, angle);
                    }
                }
                case "FLIPH" -> {
                    if (!params.isEmpty()) {
                        sprites.flipSpriteH(((Number) evaluateExpression(params.get(0))).intValue());
                    }
                }
                case "FLIPV" -> {
                    if (!params.isEmpty()) {
                        sprites.flipSpriteV(((Number) evaluateExpression(params.get(0))).intValue());
                    }
                }
                case "COLLISION" -> {
                    if (params.size() >= 2) {
                        int spr1 = ((Number) evaluateExpression(params.get(0))).intValue();
                        int spr2 = ((Number) evaluateExpression(params.get(1))).intValue();
                        boolean collision = sprites.checkCollision(spr1, spr2);
                        setVariable("COLLISION", collision ? 1.0 : 0.0);
                    }
                }
                case "SETPIXEL" -> {
                    if (params.size() >= 4) {
                        int pixNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        int px = ((Number) evaluateExpression(params.get(1))).intValue();
                        int py = ((Number) evaluateExpression(params.get(2))).intValue();
                        long pcolor = ((Number) evaluateExpression(params.get(3))).longValue();
                        sprites.setPixel(pixNum, px, py, (int)(pcolor & 0xFFFFFFFFL));
                    }
                }
                case "GETX" -> {
                    if (!params.isEmpty()) {
                        int getXNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        setVariable("SPRITEX", sprites.getSpriteX(getXNum));
                    }
                }
                case "GETY" -> {
                    if (!params.isEmpty()) {
                        int getYNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        setVariable("SPRITEY", sprites.getSpriteY(getYNum));
                    }
                }
                case "CLEAR" -> sprites.clearAllSprites();
                case "PRIORITY" -> {
                    if (params.size() >= 2) {
                        int priNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        int priority = ((Number) evaluateExpression(params.get(1))).intValue();
                        sprites.setSpritePriority(priNum, priority);
                    }
                }
                case "ALPHA" -> {
                    if (params.size() >= 2) {
                        int alphaNum = ((Number) evaluateExpression(params.get(0))).intValue();
                        int alpha = ((Number) evaluateExpression(params.get(1))).intValue();
                        sprites.setSpriteAlpha(alphaNum, alpha);
                    }
                }
                default -> throw new RuntimeException("Unknown sprite command: " + subCmd);
            }
        } catch (Exception e) {
            throw new RuntimeException("Sprite error: " + e.getMessage());
        }
    }
    
    private void executePrint(String[] tokens) {
        StringBuilder output = new StringBuilder();
        boolean newline = true;
        
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals(";")) {
                newline = false;
                continue;
            } else if (tokens[i].equals(",")) {
                output.append("\t");
                newline = false;
                continue;
            }
            
            Object value = evaluateExpression(tokens[i]);
            output.append(value);
            newline = true;
        }
        
        if (newline) {
            logOutput(output + "\n");
        } else {
            logOutput(output.toString());
        }
    }
    
    private void executeLet(String[] tokens) {
        StringBuilder lineBuilder = new StringBuilder();
        for (String token : tokens) {
            lineBuilder.append(token);
        }
        String line = lineBuilder.toString();
        
        String upperLine = line.toUpperCase();
        if (upperLine.startsWith("LET")) {
            line = line.substring(3).trim();
        }
        
        int eqPos = findEqualsSign(line);
        if (eqPos < 0) return;
        
        String varName = line.substring(0, eqPos).trim();
        String expr = line.substring(eqPos + 1).trim();
        
        if (expr.trim().equalsIgnoreCase("INKEY$")) {
            String key = graphics.getLastKey();
            graphics.clearLastKey();
            setVariable(varName, key);
            return;
        }
        
        Object value = evaluateExpression(expr);
        
        if (varName.contains("(")) {
            int paren = varName.indexOf('(');
            String arrayName = normalizeVarName(varName.substring(0, paren));
            String indexStr = varName.substring(paren + 1, varName.indexOf(')'));
            int index = ((Number) evaluateExpression(indexStr)).intValue();
            
            TypedArray array = arrays.get(arrayName);
            if (array != null && index >= 0 && index < array.values.length) {
                TypedValue tv = new TypedValue(array.type, value);
                array.values[index] = tv.getValue();
            }
        } else {
            setVariable(varName, value);
        }
    }
    
    private int findEqualsSign(String line) {
        boolean inString = false;
        boolean afterComparison = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inString = !inString;
            } else if (!inString) {
                if (c == '<' || c == '>') {
                    afterComparison = true;
                } else if (c == '=' && !afterComparison) {
                    if (i > 0 && (line.charAt(i - 1) == '<' || line.charAt(i - 1) == '>')) {
                        continue;
                    }
                    return i;
                } else if (c != ' ') {
                    afterComparison = false;
                }
            }
        }
        
        return -1;
    }
    
    private void setVariable(String varName, Object value) {
        varName = normalizeVarName(varName);
        VarType type = getVarType(varName);
        TypedValue tv = new TypedValue(type, value);
        variables.put(varName, tv);
    }
    
    private void executeInput(String[] tokens) {
        logOutput("? ");
        String varName = tokens[1].replace(",", "").replace(";", "");
        if (varName.startsWith("\"")) {
            for (int i = 1; i < tokens.length; i++) {
                if (!tokens[i].startsWith("\"")) {
                    varName = tokens[i].replace(",", "").replace(";", "");
                    break;
                }
            }
        }
        setVariable(varName, 0.0);
    }
    
    private Integer executeIf(String line) {
        String upper = line.toUpperCase();
        int thenPos = upper.indexOf("THEN");
        
        if (thenPos < 0) return null;
        
        String condition = line.substring(2, thenPos).trim();
        boolean result = evaluateCondition(condition);
        
        if (result) {
            String thenPart = line.substring(thenPos + 4).trim();
            
            String[] thenTokens = thenPart.split("\\s+");
            if (thenTokens.length > 0) {
                String cmd = thenTokens[0].toUpperCase();
                if (cmd.equals("GOTO") || cmd.equals("GOSUB")) {
                    if (thenTokens.length > 1) {
                        if (cmd.equals("GOSUB")) {
                            gosubStack.push(currentLine);
                        }
                        return resolveLabel(thenTokens[1]);
                    }
                } else if (!thenPart.contains("=") && !thenPart.contains(":")) {
                    try {
                        return resolveLabel(thenTokens[0]);
                    } catch (Exception e) {
                    }
                }
            }
            
            if (thenPart.contains(":")) {
                String[] statements = thenPart.split(":");
                for (String stmt : statements) {
                    try {
                        executeLine(stmt.trim());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                }
            } else {
                try {
                    executeLine(thenPart);
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        
        return null;
    }
    
    private void executeFor(String[] tokens) {
        String varName = normalizeVarName(tokens[1]);
        double start = ((Number) evaluateExpression(tokens[3])).doubleValue();
        double end = ((Number) evaluateExpression(tokens[5])).doubleValue();
        double step = 1.0;
        
        if (tokens.length > 6 && tokens[6].equalsIgnoreCase("STEP")) {
            step = ((Number) evaluateExpression(tokens[7])).doubleValue();
        }
        
        setVariable(varName, start);
        forStack.push(new ForLoopContext(varName, start, end, step, currentLine));
    }
    
    private Integer executeNext(String[] tokens) {
        if (forStack.isEmpty()) {
            throw new RuntimeException("NEXT without FOR");
        }
        
        ForLoopContext ctx = forStack.peek();
        TypedValue tv = variables.get(ctx.varName);
        double current = tv != null ? tv.getNumeric() : 0.0;
        current += ctx.step;
        setVariable(ctx.varName, current);
        
        boolean done = (ctx.step > 0 && current > ctx.end) || 
                       (ctx.step < 0 && current < ctx.end);
        
        if (done) {
            forStack.pop();
            return null;
        } else {
            List<Integer> lineNumbers = new ArrayList<>(program.keySet());
            int forLineIndex = lineNumbers.indexOf(ctx.startLine);
            if (forLineIndex >= 0 && forLineIndex < lineNumbers.size() - 1) {
                return lineNumbers.get(forLineIndex + 1);
            }
            return null;
        }
    }
    
    private void executeRead(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals(",")) continue;
            
            if (dataQueue.isEmpty()) {
                throw new RuntimeException("Out of DATA");
            }
            
            String value = dataQueue.poll();
            String varName = normalizeVarName(tokens[i]);
            
            if (varName.endsWith("$")) {
                setVariable(varName, value);
            } else {
                setVariable(varName, Double.parseDouble(value));
            }
        }
    }
    
    private void executeDim(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals(",")) continue;
            
            String varName = tokens[i].substring(0, tokens[i].indexOf('('));
            String sizeStr = tokens[i].substring(tokens[i].indexOf('(') + 1, tokens[i].indexOf(')'));
            int size = ((Number) evaluateExpression(sizeStr)).intValue() + 1;
            
            varName = normalizeVarName(varName);
            VarType type = getVarType(varName);
            TypedArray array = new TypedArray(type, size);
            arrays.put(varName, array);
        }
    }
    
    private void executeGraphicsCommand(String[] tokens) {
        graphics.executeCommand(tokens, this);
    }
    
    private void executeSoundCommand(String[] tokens) {
        sound.executeCommand(tokens, this);
    }
    
    public Object evaluateExpression(String expr) {
        expr = expr.trim();
        
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return expr.substring(1, expr.length() - 1);
        }
        
        if (expr.equalsIgnoreCase("INKEY$")) {
            String key = graphics.getLastKey();
            return key;
        }
        
        if (expr.matches("[A-Za-z][A-Za-z0-9]*(\\.[bwlqf]|\\$)?")) {
            String varName = normalizeVarName(expr);
            TypedValue tv = variables.get(varName);
            if (tv != null) {
                return tv.getValue();
            }
            return varName.endsWith("$") ? "" : 0.0;
        }
        
        if (expr.contains("(") && expr.contains(")")) {
            int paren = expr.indexOf('(');
            String arrayName = normalizeVarName(expr.substring(0, paren));
            
            if (arrays.containsKey(arrayName)) {
                String indexStr = expr.substring(paren + 1, expr.lastIndexOf(')'));
                int index = ((Number) evaluateExpression(indexStr)).intValue();
                
                TypedArray array = arrays.get(arrayName);
                if (array != null && index >= 0 && index < array.values.length) {
                    Object val = array.values[index];
                    return val != null ? val : (arrayName.endsWith("$") ? "" : 0.0);
                }
            } else {
                return evaluateFunction(expr);
            }
        }
        
        try {
            return evaluateArithmetic(expr);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private Object evaluateFunction(String expr) {
        int paren = expr.indexOf('(');
        String funcName = expr.substring(0, paren).toUpperCase();
        String args = expr.substring(paren + 1, expr.lastIndexOf(')'));
        
        return switch (funcName) {
            case "SIN" -> Math.sin(((Number) evaluateExpression(args)).doubleValue());
            case "COS" -> Math.cos(((Number) evaluateExpression(args)).doubleValue());
            case "TAN" -> Math.tan(((Number) evaluateExpression(args)).doubleValue());
            case "SQR", "SQRT" -> Math.sqrt(((Number) evaluateExpression(args)).doubleValue());
            case "ABS" -> Math.abs(((Number) evaluateExpression(args)).doubleValue());
            case "INT" -> (double) ((Number) evaluateExpression(args)).intValue();
            case "RND" -> random.nextInt(100)+1; 
            case "LEN" -> (double) evaluateExpression(args).toString().length();
            case "CHR$" -> {
                int code = ((Number) evaluateExpression(args)).intValue();
                yield String.valueOf((char) code);
            }
            case "ASC" -> {
                String str = evaluateExpression(args).toString();
                yield str.isEmpty() ? 0.0 : (double) str.charAt(0);
            }
            case "INKEY$" -> graphics.getLastKey();
            default -> 0.0;
        };
    }
    
    private double evaluateArithmetic(String expr) {
        expr = expr.trim();
        
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            
            if (c == ' ') {
                continue;
            } else if (isOperator(c)) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                
                if (c == '-' && (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1).charAt(0)) || tokens.get(tokens.size() - 1).equals("("))) {
                    current.append(c);
                } else {
                    tokens.add(String.valueOf(c));
                }
            } else if (c == '(' || c == ')') {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else {
                current.append(c);
            }
        }
        
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        
        return evaluateTokens(tokens);
    }
    
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }
    
    private double evaluateTokens(List<String> tokens) {
        if (tokens.isEmpty()) return 0.0;
        if (tokens.size() == 1) {
            return parseValue(tokens.get(0));
        }
        
        return parseAddSubTokens(tokens, 0, tokens.size());
    }
    
    private double parseAddSubTokens(List<String> tokens, int start, int end) {
        int parenDepth = 0;
        
        for (int i = end - 1; i >= start; i--) {
            String token = tokens.get(i);
            
            if (token.equals(")")) parenDepth++;
            else if (token.equals("(")) parenDepth--;
            else if (parenDepth == 0 && (token.equals("+") || token.equals("-"))) {
                double left = parseAddSubTokens(tokens, start, i);
                double right = parseMulDivTokens(tokens, i + 1, end);
                return token.equals("+") ? left + right : left - right;
            }
        }
        
        return parseMulDivTokens(tokens, start, end);
    }
    
    private double parseMulDivTokens(List<String> tokens, int start, int end) {
        int parenDepth = 0;
        
        for (int i = end - 1; i >= start; i--) {
            String token = tokens.get(i);
            
            if (token.equals(")")) parenDepth++;
            else if (token.equals("(")) parenDepth--;
            else if (parenDepth == 0 && (token.equals("*") || token.equals("/"))) {
                double left = parseMulDivTokens(tokens, start, i);
                double right = parsePowerTokens(tokens, i + 1, end);
                return token.equals("*") ? left * right : left / right;
            }
        }
        
        return parsePowerTokens(tokens, start, end);
    }
    
    private double parsePowerTokens(List<String> tokens, int start, int end) {
        int parenDepth = 0;
        
        for (int i = end - 1; i >= start; i--) {
            String token = tokens.get(i);
            
            if (token.equals(")")) parenDepth++;
            else if (token.equals("(")) parenDepth--;
            else if (parenDepth == 0 && token.equals("^")) {
                double left = parsePowerTokens(tokens, start, i);
                double right = parsePrimaryTokens(tokens, i + 1, end);
                return Math.pow(left, right);
            }
        }
        
        return parsePrimaryTokens(tokens, start, end);
    }
    
    private double parsePrimaryTokens(List<String> tokens, int start, int end) {
        if (start >= end) return 0.0;
        
        if (tokens.get(start).equals("(") && tokens.get(end - 1).equals(")")) {
            return parseAddSubTokens(tokens, start + 1, end - 1);
        }
        
        if (tokens.get(start).equals("-")) {
            return -parsePrimaryTokens(tokens, start + 1, end);
        }
        
        if (end - start == 1) {
            return parseValue(tokens.get(start));
        }
        
        if (end - start >= 3 && tokens.get(start + 1).equals("(")) {
            String funcName = tokens.get(start);
            StringBuilder expr = new StringBuilder(funcName).append("(");
            for (int i = start + 2; i < end - 1; i++) {
                expr.append(tokens.get(i));
            }
            expr.append(")");
            
            Object result = evaluateFunction(expr.toString());
            return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
        }
        
        return 0.0;
    }
    
    private double parseValue(String token) {
        token = token.trim();
        
        try {
            if (token.toLowerCase().startsWith("0x")) {
                return (double) Long.parseLong(token.substring(2), 16);
            }
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            if (token.contains("(")) {
                int paren = token.indexOf('(');
                String arrayName = normalizeVarName(token.substring(0, paren));
                String indexStr = token.substring(paren + 1, token.lastIndexOf(')'));
                int index = (int) evaluateArithmetic(indexStr);
                
                TypedArray array = arrays.get(arrayName);
                if (array != null && index >= 0 && index < array.values.length) {
                    Object val = array.values[index];
                    return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                }
                return 0.0;
            } else {
                String varName = normalizeVarName(token);
                TypedValue tv = variables.get(varName);
                return tv != null ? tv.getNumeric() : 0.0;
            }
        }
    }
    
    private boolean evaluateCondition(String condition) {
        String[] ops = {"<=", ">=", "<>", "=", "<", ">"};
        
        for (String op : ops) {
            int pos = condition.indexOf(op);
            if (pos > 0) {
                String leftStr = condition.substring(0, pos).trim();
                String rightStr = condition.substring(pos + op.length()).trim();
                
                boolean isStringComparison = leftStr.contains("$") || leftStr.contains("\"") || 
                                            rightStr.contains("$") || rightStr.contains("\"");
                
                if (isStringComparison) {
                    Object left = evaluateExpression(leftStr);
                    Object right = evaluateExpression(rightStr);
                    
                    String l = left.toString();
                    String r = right.toString();
                    
                    return switch (op) {
                        case "=" -> l.equals(r);
                        case "<>" -> !l.equals(r);
                        case "<" -> l.compareTo(r) < 0;
                        case ">" -> l.compareTo(r) > 0;
                        case "<=" -> l.compareTo(r) <= 0;
                        case ">=" -> l.compareTo(r) >= 0;
                        default -> false;
                    };
                }
                
                try {
                    double l = evaluateArithmetic(leftStr);
                    double r = evaluateArithmetic(rightStr);
                    
                    return switch (op) {
                        case "=" -> Math.abs(l - r) < 0.000001;
                        case "<>" -> Math.abs(l - r) >= 0.000001;
                        case "<" -> l < r;
                        case ">" -> l > r;
                        case "<=" -> l <= r;
                        case ">=" -> l >= r;
                        default -> false;
                    };
                } catch (Exception e) {
                    Object left = evaluateExpression(leftStr);
                    Object right = evaluateExpression(rightStr);
                    
                    String l = left.toString();
                    String r = right.toString();
                    
                    return switch (op) {
                        case "=" -> l.equals(r);
                        case "<>" -> !l.equals(r);
                        case "<" -> l.compareTo(r) < 0;
                        case ">" -> l.compareTo(r) > 0;
                        case "<=" -> l.compareTo(r) <= 0;
                        case ">=" -> l.compareTo(r) >= 0;
                        default -> false;
                    };
                }
            }
        }
        
        return false;
    }
    
    public String listProgram() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> entry : program.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
    
    public void newProgram() {
        program.clear();
        labels.clear();
        variables.clear();
        arrays.clear();
        sprites.clearAllSprites();
        autoLineNumber = 10;
        logOutput("Ready.\n");
    }
    
    public void loadProgram(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            newProgram();
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
            logOutput("Loaded: " + filename + "\n");
        } catch (IOException e) {
            logError("Error loading file: " + e.getMessage() + "\n");
        }
    }
    
    public void saveProgram(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, String> entry : program.entrySet()) {
                writer.println(entry.getKey() + " " + entry.getValue());
            }
            logOutput("Saved: " + filename + "\n");
        } catch (IOException e) {
            logError("Error saving file: " + e.getMessage() + "\n");
        }
    }
    
    private void logOutput(String text) {
        if (ide != null) {
            ide.appendOutput(text);
        } else {
            System.out.print(text);
        }
    }
    
    private void logError(String text) {
        if (ide != null) {
            ide.appendError(text);
        } else {
            System.err.print(text);
        }
    }
    
    record ForLoopContext(String varName, double start, double end, double step, int startLine) {}
    record WhileLoopContext(int startLine, String condition) {}
    
    static final class Sprite {
        final int id;
        BufferedImage image;
        double x = 0.0;
        double y = 0.0;
        double scaleX = 1.0;
        double scaleY = 1.0;
        double rotation = 0.0;
        boolean visible = false;
        boolean flipH = false;
        boolean flipV = false;
        int priority = 0;
        double alpha = 1.0;
        
        Sprite(int id, BufferedImage image) {
            this.id = id;
            this.image = image;
        }
        
        Rectangle2D getBounds() {
            int w = (int)(image.getWidth() * scaleX);
            int h = (int)(image.getHeight() * scaleY);
            return new Rectangle2D.Double(x, y, w, h);
        }
    }
    
    static final class SpriteEngine {
        private final Sprite[] sprites = new Sprite[256];
        private final GraphicsEngine graphics;
        
        SpriteEngine(GraphicsEngine graphics) {
            this.graphics = graphics;
        }
        
        void loadSprite(int id, String filename) {
            if (id < 0 || id >= 256) {
                throw new RuntimeException("Sprite ID must be 0-255");
            }
            
            try {
                BufferedImage img = ImageIO.read(new File(filename));
                sprites[id] = new Sprite(id, img);
            } catch (IOException e) {
                throw new RuntimeException("Cannot load sprite: " + e.getMessage());
            }
        }
        
        void createSprite(int id, int width, int height, int argbColor) {
            if (id < 0 || id >= 256) {
                throw new RuntimeException("Sprite ID must be 0-255");
            }
            
            if (width <= 0 || height <= 0) {
                throw new RuntimeException("Sprite width and height must be positive");
            }
            
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            
            Color color = new Color(argbColor, true);
            g2d.setColor(color);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
            
            sprites[id] = new Sprite(id, img);
        }
        
        void showSprite(int id) {
            if (sprites[id] != null) {
                sprites[id].visible = true;
                graphics.requestRender();
            }
        }
        
        void hideSprite(int id) {
            if (sprites[id] != null) {
                sprites[id].visible = false;
                graphics.requestRender();
            }
        }
        
        void moveSprite(int id, double x, double y) {
            if (sprites[id] != null) {
                sprites[id].x = x;
                sprites[id].y = y;
                graphics.requestRender();
            }
        }
        
        void moveSpriteBy(int id, double dx, double dy) {
            if (sprites[id] != null) {
                sprites[id].x += dx;
                sprites[id].y += dy;
                graphics.requestRender();
            }
        }
        
        void scaleSprite(int id, double scaleX, double scaleY) {
            if (sprites[id] != null) {
                sprites[id].scaleX = scaleX;
                sprites[id].scaleY = scaleY;
                graphics.requestRender();
            }
        }
        
        void rotateSprite(int id, double angle) {
            if (sprites[id] != null) {
                sprites[id].rotation = angle;
                graphics.requestRender();
            }
        }
        
        void flipSpriteH(int id) {
            if (sprites[id] != null) {
                sprites[id].flipH = !sprites[id].flipH;
                graphics.requestRender();
            }
        }
        
        void flipSpriteV(int id) {
            if (sprites[id] != null) {
                sprites[id].flipV = !sprites[id].flipV;
                graphics.requestRender();
            }
        }
        
        boolean checkCollision(int id1, int id2) {
            if (sprites[id1] == null || sprites[id2] == null) return false;
            if (!sprites[id1].visible || !sprites[id2].visible) return false;
            
            Rectangle2D r1 = sprites[id1].getBounds();
            Rectangle2D r2 = sprites[id2].getBounds();
            return r1.intersects(r2);
        }
        
        void setPixel(int id, int x, int y, int argbColor) {
            if (sprites[id] != null) {
                BufferedImage img = sprites[id].image;
                if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
                    img.setRGB(x, y, argbColor);
                    graphics.requestRender();
                }
            }
        }
        
        double getSpriteX(int id) {
            return sprites[id] != null ? sprites[id].x : 0.0;
        }
        
        double getSpriteY(int id) {
            return sprites[id] != null ? sprites[id].y : 0.0;
        }
        
        void setSpritePriority(int id, int priority) {
            if (sprites[id] != null) {
                sprites[id].priority = priority;
                graphics.requestRender();
            }
        }
        
        void setSpriteAlpha(int id, int alpha) {
            if (sprites[id] != null) {
                sprites[id].alpha = Math.clamp(alpha / 255.0, 0.0, 1.0);
                graphics.requestRender();
            }
        }
        
        void clearAllSprites() {
            Arrays.fill(sprites, null);
            graphics.requestRender();
        }
        
        void renderSprites(Graphics2D g2d) {
            List<Sprite> visible = Arrays.stream(sprites)
                .filter(s -> s != null && s.visible)
                .sorted(Comparator.comparingInt(s -> s.priority))
                .toList();
            
            for (Sprite s : visible) {
                renderSprite(g2d, s);
            }
        }
        
        private void renderSprite(Graphics2D g2d, Sprite s) {
            Graphics2D g = (Graphics2D) g2d.create();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) s.alpha));
            
            int w = s.image.getWidth();
            int h = s.image.getHeight();
            double centerX = s.x + w * s.scaleX / 2;
            double centerY = s.y + h * s.scaleY / 2;
            
            g.translate(centerX, centerY);
            g.rotate(Math.toRadians(s.rotation));
            g.scale(s.scaleX * (s.flipH ? -1 : 1), s.scaleY * (s.flipV ? -1 : 1));
            g.drawImage(s.image, -w / 2, -h / 2, null);
            g.dispose();
        }
    }
    
    static final class GraphicsEngine {
        private JFrame frame;
        private Canvas canvas;
        private BufferedImage backBuffer;
        private Graphics2D g2d;
        private Color currentColor = Color.WHITE;
        private int currentMode = 1;
        private SpriteEngine spriteEngine;
        private volatile String lastKey = "";
        private Font textFont = new Font("Monospaced", Font.PLAIN, 16);
        private boolean needsFullRedraw = true;
        
        record ScreenMode(int width, int height, String name) {}
        
        private final ScreenMode[] modes = {
            new ScreenMode(320, 240, "Mode 0: 320x240"),
            new ScreenMode(640, 480, "Mode 1: 640x480"),
            new ScreenMode(800, 600, "Mode 2: 800x600"),
            new ScreenMode(1024, 768, "Mode 3: 1024x768"),
            new ScreenMode(1280, 720, "Mode 4: 1280x720 HD"),
            new ScreenMode(1920, 1080, "Mode 5: 1920x1080 Full HD"),
            new ScreenMode(640, 360, "Mode 6: 640x360"),
            new ScreenMode(400, 300, "Mode 7: 400x300"),
            new ScreenMode(160, 120, "Mode 8: 160x120 Tiny"),
            new ScreenMode(1280, 1024, "Mode 9: 1280x1024")
        };
        
        GraphicsEngine() {
            initWindow(640, 480);
        }
        
        void setSpriteEngine(SpriteEngine engine) {
            this.spriteEngine = engine;
        }
        
        String getLastKey() {
            synchronized(this) {
                return lastKey;
            }
        }
        
        void clearLastKey() {
            synchronized(this) {
                lastKey = "";
            }
        }
        
        void setMode(int mode) {
            if (mode < 0 || mode >= modes.length) {
                throw new RuntimeException("Invalid screen mode: " + mode);
            }
            
            currentMode = mode;
            ScreenMode m = modes[mode];
            
            if (frame != null) frame.dispose();
            initWindow(m.width, m.height);
        }
        
        void initWindow(int width, int height) {
            SwingUtilities.invokeLater(() -> {
                if (frame == null) {
                    frame = new JFrame("FGBasic Graphics");
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowActivated(WindowEvent e) {
                            if (canvas != null) {
                                canvas.requestFocusInWindow();
                            }
                        }
                    });
                }
                
                backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                g2d = backBuffer.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                canvas = new Canvas() {
                    @Override
                    public void paint(Graphics g) {
                        g.drawImage(backBuffer, 0, 0, null);
                    }
                    
                    @Override
                    public void update(Graphics g) {
                        paint(g);
                    }
                };
                canvas.setPreferredSize(new Dimension(width, height));
                canvas.setBackground(Color.BLACK);
                canvas.setIgnoreRepaint(false);
                
                canvas.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (c != KeyEvent.CHAR_UNDEFINED && c >= 32 && c < 127) {
                            lastKey = String.valueOf(c);
                        } else {
                            switch (e.getKeyCode()) {
                                case KeyEvent.VK_UP -> lastKey = "UP";
                                case KeyEvent.VK_DOWN -> lastKey = "DOWN";
                                case KeyEvent.VK_LEFT -> lastKey = "LEFT";
                                case KeyEvent.VK_RIGHT -> lastKey = "RIGHT";
                                case KeyEvent.VK_ESCAPE -> lastKey = "ESC";
                                case KeyEvent.VK_ENTER -> lastKey = "\n";
                                case KeyEvent.VK_SPACE -> lastKey = " ";
                                case KeyEvent.VK_BACK_SPACE -> lastKey = "BACK";
                            }
                        }
                    }
                });
                
                canvas.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        canvas.requestFocusInWindow();
                    }
                });
                
                canvas.setFocusable(true);
                
                frame.getContentPane().removeAll();
                frame.add(canvas);
                frame.pack();
                
                frame.invalidate();
                frame.revalidate();
                
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                SwingUtilities.invokeLater(() -> {
                    canvas.requestFocusInWindow();
                    frame.toFront();
                    canvas.invalidate();
                    canvas.repaint();
                });
                
                cls();
            });
        }
        
        void cls() {
            if (g2d != null) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
                needsFullRedraw = true;
                
                if (canvas != null) {
                    canvas.invalidate();
                    canvas.revalidate();
                }
                requestRender();
            }
        }
        
        void requestRender() {
            if (canvas != null && g2d != null) {
                if (needsFullRedraw) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
                    needsFullRedraw = false;
                }
                
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
                
                if (spriteEngine != null) {
                    spriteEngine.renderSprites(g2d);
                }
                
                canvas.invalidate();
                canvas.repaint();
                canvas.revalidate();
            }
        }
        
        void forceRedraw() {
            if (canvas != null) {
                canvas.invalidate();
                canvas.repaint();
                canvas.revalidate();
            }
        }
        
        void executeTextCommand(String[] tokens, BasicInterpreter interp) {
            SwingUtilities.invokeLater(() -> {
                try {
                    if (tokens.length >= 4 && g2d != null) {
                        int x = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).intValue();
                        int y = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).intValue();
                        
                        StringBuilder textBuilder = new StringBuilder();
                        for (int i = 3; i < tokens.length; i++) {
                            if (!tokens[i].equals(",") && !tokens[i].equals(";")) {
                                Object val = interp.evaluateExpression(tokens[i]);
                                textBuilder.append(val);
                            }
                        }
                        String text = textBuilder.toString();
                        
                        g2d.setColor(currentColor);
                        g2d.setFont(textFont);
                        g2d.drawString(text, x, y);
                        requestRender();
                    }
                } catch (Exception e) {
                    System.err.println("Text error: " + e.getMessage());
                }
            });
        }
        
        void executeCommand(String[] tokens, BasicInterpreter interp) {
            String cmd = tokens[0].toUpperCase();
            
            SwingUtilities.invokeLater(() -> {
                try {
                    switch (cmd) {
                        case "MODE", "SCREEN" -> {
                            if (tokens.length >= 2) {
                                int mode = ((Number) interp.evaluateExpression(tokens[1])).intValue();
                                setMode(mode);
                            }
                        }
                        case "LINE" -> {
                            if (tokens.length >= 5 && g2d != null) {
                                double x1 = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).doubleValue();
                                double y1 = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).doubleValue();
                                double x2 = ((Number) interp.evaluateExpression(tokens[3].replace(",", ""))).doubleValue();
                                double y2 = ((Number) interp.evaluateExpression(tokens[4].replace(",", ""))).doubleValue();
                                
                                if (tokens.length > 5) {
                                    long argb = ((Number) interp.evaluateExpression(tokens[5])).longValue();
                                    g2d.setColor(new Color((int)(argb & 0xFFFFFFFFL), true));
                                } else {
                                    g2d.setColor(currentColor);
                                }
                                
                                g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
                                requestRender();
                            }
                        }
                        case "CIRCLE" -> {
                            if (tokens.length >= 4 && g2d != null) {
                                double x = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).doubleValue();
                                double y = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).doubleValue();
                                double radius = ((Number) interp.evaluateExpression(tokens[3].replace(",", ""))).doubleValue();
                                
                                if (tokens.length > 4) {
                                    long argb = ((Number) interp.evaluateExpression(tokens[4])).longValue();
                                    g2d.setColor(new Color((int)(argb & 0xFFFFFFFFL), true));
                                } else {
                                    g2d.setColor(currentColor);
                                }
                                
                                g2d.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
                                requestRender();
                            }
                        }
                        case "BOX" -> {
                            if (tokens.length >= 5 && g2d != null) {
                                double x = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).doubleValue();
                                double y = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).doubleValue();
                                double w = ((Number) interp.evaluateExpression(tokens[3].replace(",", ""))).doubleValue();
                                double h = ((Number) interp.evaluateExpression(tokens[4].replace(",", ""))).doubleValue();
                                
                                if (tokens.length > 5) {
                                    long argb = ((Number) interp.evaluateExpression(tokens[5])).longValue();
                                    g2d.setColor(new Color((int)(argb & 0xFFFFFFFFL), true));
                                } else {
                                    g2d.setColor(currentColor);
                                }
                                
                                g2d.fillRect((int)x, (int)y, (int)w, (int)h);
                                requestRender();
                            }
                        }
                        case "PIXEL" -> {
                            if (tokens.length >= 3 && backBuffer != null) {
                                int x = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).intValue();
                                int y = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).intValue();
                                
                                int color = tokens.length > 3 ? 
                                    (int)(((Number) interp.evaluateExpression(tokens[3])).longValue() & 0xFFFFFFFFL) :
                                    currentColor.getRGB();
                                
                                if (x >= 0 && x < backBuffer.getWidth() && y >= 0 && y < backBuffer.getHeight()) {
                                    backBuffer.setRGB(x, y, color);
                                    requestRender();
                                }
                            }
                        }
                        case "COLOUR", "COLOR" -> {
                            if (tokens.length >= 4) {
                                int r = ((Number) interp.evaluateExpression(tokens[1].replace(",", ""))).intValue();
                                int g = ((Number) interp.evaluateExpression(tokens[2].replace(",", ""))).intValue();
                                int b = ((Number) interp.evaluateExpression(tokens[3].replace(",", ""))).intValue();
                                int a = tokens.length > 4 ? 
                                    ((Number) interp.evaluateExpression(tokens[4])).intValue() : 255;
                                currentColor = new Color(r, g, b, a);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Graphics error: " + e.getMessage());
                }
            });
        }
    }
    
    static final class SoundEngine {
        void executeCommand(String[] tokens, BasicInterpreter interp) {
            System.out.println("Sound: " + String.join(" ", tokens));
        }
    }
    
    static final class BasicIDE extends JFrame {
        private final JTextPane codeEditor;
        private final javax.swing.JTextArea outputArea;
        private final BasicInterpreter interpreter;
        private String currentTheme = "Light";
        private int fontSize = 14;
        
        private static final Color LIGHT_BG = new Color(255, 255, 255);
        private static final Color LIGHT_FG = new Color(0, 0, 0);
        private static final Color DARK_BG = new Color(43, 43, 43);
        private static final Color DARK_FG = new Color(220, 220, 220);
        
        BasicIDE(BasicInterpreter interp) {
            this.interpreter = interp;
            
            setTitle("FGBasic IDE v3.0.3");
            setSize(1000, 700);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            
            codeEditor = createCodeEditor();
            outputArea = createOutputArea();
            
            createMenuBar();
            createToolBar();
            createEditorPanel();
            createOutputPanel();
            
            applyTheme();
        }
        
        private JTextPane createCodeEditor() {
            JTextPane editor = new JTextPane();
            editor.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
            return editor;
        }
        
        private javax.swing.JTextArea createOutputArea() {
            javax.swing.JTextArea area = new javax.swing.JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
            return area;
        }
        
        private void createMenuBar() {
            JMenuBar menuBar = new JMenuBar();
            
            JMenu fileMenu = new JMenu("File");
            addMenuItem(fileMenu, "New", KeyEvent.VK_N, e -> newFile());
            addMenuItem(fileMenu, "Open...", KeyEvent.VK_O, e -> openFile());
            addMenuItem(fileMenu, "Save...", KeyEvent.VK_S, e -> saveFile());
            fileMenu.addSeparator();
            addMenuItem(fileMenu, "Exit", -1, e -> System.exit(0));
            
            JMenu editMenu = new JMenu("Edit");
            addMenuItem(editMenu, "Change Theme", -1, e -> changeTheme());
            addMenuItem(editMenu, "Change Font Size", -1, e -> changeFontSize());
            
            JMenu runMenu = new JMenu("Run");
            addMenuItem(runMenu, "Run", KeyEvent.VK_F5, e -> runProgram());
            addMenuItem(runMenu, "Stop", -1, e -> stopProgram());
            addMenuItem(runMenu, "List", -1, e -> listProgram());
            
            JMenu helpMenu = new JMenu("Help");
            addMenuItem(helpMenu, "Variable Types", -1, e -> showVariableTypes());
            addMenuItem(helpMenu, "Commands", -1, e -> showCommands());
            addMenuItem(helpMenu, "About", -1, e -> showAbout());
            
            menuBar.add(fileMenu);
            menuBar.add(editMenu);
            menuBar.add(runMenu);
            menuBar.add(helpMenu);
            
            setJMenuBar(menuBar);
        }
        
        private void addMenuItem(JMenu menu, String text, int keyCode, java.awt.event.ActionListener listener) {
            JMenuItem item = new JMenuItem(text);
            item.addActionListener(listener);
            if (keyCode > 0) {
                item.setAccelerator(KeyStroke.getKeyStroke(keyCode, 
                    keyCode == KeyEvent.VK_F5 ? 0 : InputEvent.CTRL_DOWN_MASK));
            }
            menu.add(item);
        }
        
        private void createToolBar() {
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            
            addButton(toolBar, "New", this::newFile);
            addButton(toolBar, "Open", this::openFile);
            addButton(toolBar, "Save", this::saveFile);
            toolBar.addSeparator();
            
            JButton runBtn = new JButton("▶ Run");
            runBtn.setBackground(new Color(0, 180, 0));
            runBtn.setForeground(Color.WHITE);
            runBtn.setOpaque(true);
            runBtn.setBorderPainted(false);
            runBtn.addActionListener(e -> runProgram());
            toolBar.add(runBtn);
            
            addButton(toolBar, "■ Stop", this::stopProgram);
            
            add(toolBar, BorderLayout.NORTH);
        }
        
        private void addButton(JToolBar toolBar, String text, Runnable action) {
            JButton btn = new JButton(text);
            btn.addActionListener(e -> action.run());
            toolBar.add(btn);
        }
        
        private void createEditorPanel() {
            JScrollPane editorScroll = new JScrollPane(codeEditor);
            editorScroll.setPreferredSize(new Dimension(600, 400));
            
            JPanel editorPanel = new JPanel(new BorderLayout());
            editorPanel.add(new JLabel(" Code Editor"), BorderLayout.NORTH);
            editorPanel.add(editorScroll, BorderLayout.CENTER);
            
            add(editorPanel, BorderLayout.CENTER);
        }
        
        private void createOutputPanel() {
            JScrollPane outputScroll = new JScrollPane(outputArea);
            outputScroll.setPreferredSize(new Dimension(600, 200));
            
            JPanel outputPanel = new JPanel(new BorderLayout());
            outputPanel.add(new JLabel(" Output"), BorderLayout.NORTH);
            outputPanel.add(outputScroll, BorderLayout.CENTER);
            
            add(outputPanel, BorderLayout.SOUTH);
        }
        
        private void applyTheme() {
            Color bg = currentTheme.equals("Dark") ? DARK_BG : LIGHT_BG;
            Color fg = currentTheme.equals("Dark") ? DARK_FG : LIGHT_FG;
            
            codeEditor.setBackground(bg);
            codeEditor.setForeground(fg);
            codeEditor.setCaretColor(fg);
            
            outputArea.setBackground(bg);
            outputArea.setForeground(fg);
            outputArea.setCaretColor(fg);
        }
        
        private void changeTheme() {
            String[] themes = {"Light", "Dark"};
            String selected = (String) JOptionPane.showInputDialog(
                this, "Select theme:", "Change Theme",
                JOptionPane.PLAIN_MESSAGE, null, themes, currentTheme);
            
            if (selected != null) {
                currentTheme = selected;
                applyTheme();
            }
        }
        
        private void changeFontSize() {
            String input = JOptionPane.showInputDialog(this, "Enter font size (8-32):", fontSize);
            
            if (input != null) {
                try {
                    int size = Integer.parseInt(input);
                    if (size >= 8 && size <= 32) {
                        fontSize = size;
                        codeEditor.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
                        outputArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid font size");
                }
            }
        }
        
        private void newFile() {
            int result = JOptionPane.showConfirmDialog(
                this, "Clear current program?", "New File", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                codeEditor.setText("");
                outputArea.setText("");
                interpreter.newProgram();
            }
        }
        
        private void openFile() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "BASIC files", "bas", "basic"));
            
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    codeEditor.setText(sb.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
                }
            }
        }
        
        private void saveFile() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "BASIC files", "bas", "basic"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    if (!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + ".bas");
                    }
                    
                    PrintWriter writer = new PrintWriter(new FileWriter(file));
                    writer.print(codeEditor.getText());
                    writer.close();
                    
                    appendOutput("Saved: " + file.getName() + "\n");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
                }
            }
        }
        
        private void runProgram() {
            outputArea.setText("");
            String code = codeEditor.getText();
            
            Thread.ofVirtual().start(() -> {
                try {
                    interpreter.executeProgram(code);
                    SwingUtilities.invokeLater(() -> 
                        appendOutput("\n--- Program finished ---\n"));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        appendError("Error: " + e.getMessage() + "\n"));
                }
            });
        }
        
        private void stopProgram() {
            interpreter.running = false;
            appendOutput("\n--- Program stopped ---\n");
        }
        
        private void listProgram() {
            String listing = interpreter.listProgram();
            if (listing.isEmpty()) {
                appendOutput("No program loaded\n");
            } else {
                codeEditor.setText(listing);
            }
        }
        
        private void showAbout() {
            JOptionPane.showMessageDialog(this,
                "FGBasic v3.0.3 - Fixed Tokenization\n\n" +
                "NEW: Spaces optional in expressions!\n" +
                "x=x+1 works\n" +
                "IF x>10 THEN y=20 works\n" +
                "FOR i=1 TO 10 works\n\n" +
                "Press F5 to run",
                "About", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void showCommands() {
            JOptionPane.showMessageDialog(this,
                "Commands:\n" +
                "PRINT, LET, INPUT, IF...THEN\n" +
                "FOR...NEXT, WHILE...WEND\n" +
                "GOTO, GOSUB, RETURN\n" +
                "LINE, CIRCLE, BOX, PIXEL\n" +
                "SPRITE commands\n\n" +
                "NEW: No spaces needed!\n" +
                "x=x+1\n" +
                "FOR i=1TO10",
                "Commands", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void showVariableTypes() {
            JOptionPane.showMessageDialog(this,
                "Variable Types:\n" +
                ".b (Byte)\n" +
                ".w (Word)\n" +
                ".l (Long)\n" +
                ".q (Quick)\n" +
                ".f (Float)\n" +
                "$  (String)",
                "Variable Types", JOptionPane.INFORMATION_MESSAGE);
        }
        
        void appendOutput(String text) {
            SwingUtilities.invokeLater(() -> {
                outputArea.append(text);
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
        
        void appendError(String text) {
            SwingUtilities.invokeLater(() -> {
                outputArea.append("[ERROR] " + text);
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
    }
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        
        BasicInterpreter basic = new BasicInterpreter();
        basic.graphics.setSpriteEngine(basic.sprites);
        basic.startIDE();
    }
}