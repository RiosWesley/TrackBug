package trackbug.util;

public class ValidationHelper {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        return name.matches("^[\\p{L}\\s.'-]+$");
    }

    public static boolean isNumeric(String value) {
        if (isNullOrEmpty(value)) return false;
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidId(String id) {
        if (isNullOrEmpty(id)) return false;
        return id.matches("^[A-Z0-9]+$");
    }
}