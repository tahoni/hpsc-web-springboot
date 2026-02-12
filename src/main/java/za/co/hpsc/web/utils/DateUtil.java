package za.co.hpsc.web.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private DateUtil() {
        // Utility class, not to be instantiated
    }

    public static String formatDate(LocalDate date, String format) {
        return DateTimeFormatter.ofPattern(format).format(date);
    }

    public static String formatDateTime(LocalDateTime date, String format) {
        return DateTimeFormatter.ofPattern(format).format(date);
    }
}
