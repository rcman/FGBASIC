public class CommandParser {
    private String line;
    private String command;
    private String remainder;
    
    public CommandParser(String line) {
        this.line = line.trim();
        parse();
    }
    
    private void parse() {
        if (line.isEmpty()) {
            command = "";
            remainder = "";
            return;
        }
        
        // Find first whitespace or special character
        int spacePos = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c) || c == '(' || c == '=') {
                spacePos = i;
                break;
            }
        }
        
        if (spacePos == -1) {
            command = line;
            remainder = "";
        } else {
            command = line.substring(0, spacePos);
            remainder = line.substring(spacePos).trim();
        }
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getRemainder() {
        return remainder;
    }
    
    public String getFullLine() {
        return line;
    }
}
