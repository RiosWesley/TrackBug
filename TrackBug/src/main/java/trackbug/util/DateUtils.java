package trackbug.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATE_FORMATTER);
    }

    public static LocalDate parseData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(data, DATE_FORMATTER);
    }
}