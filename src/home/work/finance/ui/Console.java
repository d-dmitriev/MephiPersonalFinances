package home.work.finance.ui;

public class Console {
    public static void printFormatedLine(String text, Object... args) {
        System.out.printf("\u001B[32m" + text + "\u001B[0m", args);
    }

    public static void printLine(String text) {
        System.out.println("\u001B[32m" + text + "\u001B[0m");
    }

    public static void printLineWarning(String text) {
        System.out.println("\u001B[33mВнимание! " + text + "\u001B[0m");
    }

    public static void printLineError(String text) {
        System.out.println("\u001B[31mОшибка: " + text + "\u001B[0m");
    }

    public static void printInput(String text) {
        System.out.print(text);
    }

    public static void printHeader(String text) {
        System.out.println(text);
    }

    public static void printMenu(String text) {
        System.out.println("\u001B[37m" + text + "\u001B[0m");
    }
}
