package za.co.hpsc.web.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class providing methods to handle date and time formatting.
 *
 * <p>
 * The {@code DateUtil} class contains static utility methods to format
 * {@link LocalDate} and {@link LocalDateTime} objects into strings using
 * specified patterns. This class is designed to simplify date and time
 * formatting operations and handles null values gracefully by returning
 * an empty string.
 * </p>
 */
public final class DateUtil {
    private DateUtil() {
        // Utility class, not to be instantiated
    }

    /**
     * Formats a given {@link LocalDate} object as a string using the specified date pattern.
     * If the input date or date format is null, an empty string is returned.
     *
     * @param date       the {@link LocalDate} object to be formatted;
     *                   may be null.
     * @param dateFormat the date format pattern to be applied;
     *                   must be a valid {@link DateTimeFormatter} pattern;
     *                   may be null.
     * @return the formatted date as a string, or an empty string if the input date or date format is null.
     */
    public static String formatDate(LocalDate date, String dateFormat) {
        if ((date == null) || (dateFormat == null)) {
            return "";
        }

        return DateTimeFormatter.ofPattern(dateFormat).format(date);
    }

    /**
     * Formats a given {@link LocalDateTime} object as a string using the specified date-time pattern.
     * If the input date or date format is null, an empty string is returned.
     *
     * @param date       the {@link LocalDateTime} object to be formatted;
     *                   may be null
     * @param dateFormat the date-time format pattern to be applied;
     *                   must be a valid {@link DateTimeFormatter} pattern;
     *                   may be null
     * @return the formatted date-time as a string, or an empty string if the input date or date format is null
     */
    public static String formatDateTime(LocalDateTime date, String dateFormat) {
        if ((date == null) || (dateFormat == null)) {
            return "";
        }

        return DateTimeFormatter.ofPattern(dateFormat).format(date);
    }
}
