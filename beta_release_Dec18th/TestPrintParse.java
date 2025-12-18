public class TestPrintParse {
    public static void main(String[] args) {
        String expr = "\"Left:\", LEFT$(NAME$, 3)";
        String[] parts = splitPrintArgs(expr);
        for (int i = 0; i < parts.length; i++) {
            System.out.println("Part " + i + ": [" + parts[i] + "]");
            System.out.println("  Trimmed: [" + parts[i].trim() + "]");
            System.out.println("  Contains $: " + parts[i].contains("$"));
            System.out.println();
        }
    }

    private static String[] splitPrintArgs(String expr) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                parts.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());

        return parts.toArray(new String[0]);
    }
}
