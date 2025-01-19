package home.work.finance.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataValidator {

    public static boolean isNumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String input) {
        return isNumeric(input) && Double.parseDouble(input) >= 0;
    }

    public static boolean isNonEmptyString(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidDate(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isNumberInRange(double number, double min, double max) {
        return number >= min && number <= max;
    }

    public static boolean isStringLengthValid(String input, int maxLength) {
        return isNonEmptyString(input) && input.length() <= maxLength;
    }

    public static boolean isValidLogin(String login) {
        return isNonEmptyString(login) && login.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    public static boolean isValidPassword(String password) {
        return isNonEmptyString(password) && password.length() >= 6;
    }
}