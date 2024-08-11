public class Design {
    // Color codes
    public static final String RESET_COLOR = "\u001B[0m";
    public static final String GREEN_COLOR = "\u001B[32m";
    public static final String BLUE_COLOR = "\u001B[34m";
    public static final String RED_COLOR = "\u001B[31m"; // Error color
    public static final String YELLOW_COLOR = "\u001B[33m"; // Warning color

    // Method to format a message with color
    public static String formatMessage(String message, String color) {
        return String.format("%s %s %s", color, message, RESET_COLOR);
    }

    // method to format a message to display to the user asking for input
    public static String formatInputMessage(String message) {
        return String.format("%s%s%s ", BLUE_COLOR, message, RESET_COLOR);
    }

    // Method to create a border
    public static String createBorder(int length) {
        return "=".repeat(length);
    }

    // Method to pad a message to a specific length
    public static String padMessage(String message, int length) {
        int padding = (length - message.length()) / 2;
        return " ".repeat(padding) + message + " ".repeat(padding);
    }

    public static void printTableHeader() {
        System.out.println("+-------------------------+-------------------------+");
        System.out.println("| System Message          | User Input              |");
        System.out.println("+-------------------------+-------------------------+");
    }

    // Method to print a row with two columns
    public static void printTableRow(String systemMessage, String userInput) {
        System.out.printf("| %-23s | %-23s |%n", systemMessage, userInput);
        System.out.println("+-------------------------+-------------------------+");
    }
}
