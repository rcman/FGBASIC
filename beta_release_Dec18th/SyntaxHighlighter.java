import javax.swing.text.*;
import java.awt.Color;
import java.util.regex.*;

public class SyntaxHighlighter {
    private static final String[] KEYWORDS = {
        "PRINT", "LET", "INPUT", "IF", "THEN", "ELSE", "GOTO", "GO", "TO",
        "GOSUB", "RETURN", "FOR", "NEXT", "STEP", "DIM", "END", "STOP",
        "CLS", "COLOR", "COLOUR", "LINE", "CIRCLE", "ELLIPSE", "POLYGON",
        "RECT", "BOX", "TEXT", "PIXEL", "PSET", "SPRITE", "SOUND", "PLAY", 
        "BEEP", "TONE", "WAVEFORM", "LOAD", "SAVE", "REM", "AND", "OR", "NOT", "MOD",
        "SELECT", "CASE", "TURTLE", "FORWARD", "FD", "BACKWARD", "BK",
        "RIGHT", "RT", "LEFT", "LT", "PENUP", "PU", "PENDOWN", "PD", "HOME",
        "PAGE", "MOUSEX", "MOUSEY", "WHILE", "WEND", "DO", "LOOP", "UNTIL",
        "DEF", "FN", "DATA", "READ", "RESTORE", "COLLISION", "RECORD", "PLAYBACK",
        "BOX3D", "SPHERE3D", "ROTATE3D", "NETOPEN", "NETSEND", "NETRECV", "NETCLOSE",
        "WAIT", "FONT"
    };
    
    private static final String[] FUNCTIONS = {
        "ABS", "SIN", "COS", "TAN", "ATN", "SQR", "INT", "RND", "SGN",
        "LEN", "VAL", "STR", "CHR", "ASC", "LEFT", "RIGHT", "MID",
        "UPPER", "LOWER", "TRIM", "INSTR", "REPLACE", "REVERSE"
    };
    
    private StyleContext context;
    private Style defaultStyle;
    private Style keywordStyle;
    private Style functionStyle;
    private Style numberStyle;
    private Style stringStyle;
    private Style commentStyle;
    private Style operatorStyle;
    
    public SyntaxHighlighter() {
        context = new StyleContext();
        
        defaultStyle = context.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, new Color(0, 255, 255));
        
        keywordStyle = context.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, new Color(255, 150, 50));
        StyleConstants.setBold(keywordStyle, true);
        
        functionStyle = context.addStyle("function", null);
        StyleConstants.setForeground(functionStyle, new Color(100, 200, 255));
        
        numberStyle = context.addStyle("number", null);
        StyleConstants.setForeground(numberStyle, new Color(255, 255, 100));
        
        stringStyle = context.addStyle("string", null);
        StyleConstants.setForeground(stringStyle, new Color(100, 255, 100));
        
        commentStyle = context.addStyle("comment", null);
        StyleConstants.setForeground(commentStyle, new Color(100, 150, 100));
        StyleConstants.setItalic(commentStyle, true);
        
        operatorStyle = context.addStyle("operator", null);
        StyleConstants.setForeground(operatorStyle, new Color(255, 100, 200));
    }
    
    public void highlight(StyledDocument doc, String text) {
        try {
            // Limit highlighting for performance
            int textLength = Math.min(text.length(), 10000);
            
            // Reset all to default
            doc.setCharacterAttributes(0, textLength, defaultStyle, true);
            
            String[] lines = text.substring(0, textLength).split("\n");
            int offset = 0;
            
            for (String line : lines) {
                if (offset < textLength) {
                    highlightLine(doc, line, offset);
                }
                offset += line.length() + 1;
            }
        } catch (Exception e) {
            // Fail silently to avoid breaking the editor
            System.err.println("Syntax highlighting error: " + e.getMessage());
        }
    }
    
    private void highlightLine(StyledDocument doc, String line, int offset) {
        // Comments
        if (line.trim().startsWith("'") || line.trim().toUpperCase().startsWith("REM")) {
            doc.setCharacterAttributes(offset, line.length(), commentStyle, true);
            return;
        }
        
        // Strings
        Pattern stringPattern = Pattern.compile("\"([^\"]*)\"");
        Matcher stringMatcher = stringPattern.matcher(line);
        while (stringMatcher.find()) {
            doc.setCharacterAttributes(offset + stringMatcher.start(), 
                stringMatcher.end() - stringMatcher.start(), stringStyle, true);
        }
        
        // Numbers
        Pattern numberPattern = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");
        Matcher numberMatcher = numberPattern.matcher(line);
        while (numberMatcher.find()) {
            doc.setCharacterAttributes(offset + numberMatcher.start(),
                numberMatcher.end() - numberMatcher.start(), numberStyle, true);
        }
        
        // Keywords
        for (String keyword : KEYWORDS) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                doc.setCharacterAttributes(offset + matcher.start(),
                    keyword.length(), keywordStyle, true);
            }
        }
        
        // Functions
        for (String func : FUNCTIONS) {
            Pattern pattern = Pattern.compile("\\b" + func + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                doc.setCharacterAttributes(offset + matcher.start(),
                    func.length(), functionStyle, true);
            }
        }
        
        // Operators
        Pattern opPattern = Pattern.compile("[+\\-*/=<>()\\[\\],;:]");
        Matcher opMatcher = opPattern.matcher(line);
        while (opMatcher.find()) {
            doc.setCharacterAttributes(offset + opMatcher.start(), 1, operatorStyle, true);
        }
    }
}
