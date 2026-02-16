package za.co.hpsc.web.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// TODO: Javadoc
// TODO: add tests
public final class DateUtil {
    private DateUtil() {
        // Utility class, not to be instantiated
    }

    public static String formatDate(LocalDate date, String dateFormat) {
        if ((date == null) || (dateFormat == null)) {
            return "";
        }

        return DateTimeFormatter.ofPattern(dateFormat).format(date);
    }

    public static String formatDateTime(LocalDateTime date, String dateFormat) {
        if ((date == null) || (dateFormat == null)) {
            return "";
        }

        return DateTimeFormatter.ofPattern(dateFormat).format(date);
    }
}
