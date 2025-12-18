public class StringFunctions {
    
    public static int len(String str) {
        return str.length();
    }
    
    public static String left(String str, int count) {
        if (count < 0) count = 0;
        if (count > str.length()) count = str.length();
        return str.substring(0, count);
    }
    
    public static String right(String str, int count) {
        if (count < 0) count = 0;
        if (count > str.length()) count = str.length();
        return str.substring(str.length() - count);
    }
    
    public static String mid(String str, int start, int count) {
        if (start < 1) start = 1;
        start--; // Convert to 0-based
        if (start >= str.length()) return "";
        
        int end = start + count;
        if (end > str.length()) end = str.length();
        
        return str.substring(start, end);
    }
    
    public static String upper(String str) {
        return str.toUpperCase();
    }
    
    public static String lower(String str) {
        return str.toLowerCase();
    }
    
    public static String trim(String str) {
        return str.trim();
    }
    
    public static int instr(String haystack, String needle) {
        int pos = haystack.indexOf(needle);
        return pos == -1 ? 0 : pos + 1; // 1-based index
    }
    
    public static String replace(String str, String oldStr, String newStr) {
        return str.replace(oldStr, newStr);
    }
    
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
    
    public static String chr(int code) {
        return String.valueOf((char)code);
    }
    
    public static int asc(String str) {
        if (str.isEmpty()) return 0;
        return (int)str.charAt(0);
    }
    
    public static String str(double num) {
        if (num == (int)num) {
            return String.valueOf((int)num);
        }
        return String.valueOf(num);
    }
    
    public static double val(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
