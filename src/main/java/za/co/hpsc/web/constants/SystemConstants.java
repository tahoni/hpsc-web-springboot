package za.co.hpsc.web.constants;

/**
 * Provides system-wide constants for various commonly used configurations,
 * including default scale values and date formatting patterns.
 *
 * <p>
 * This class serves as a centralised repository for constants that are shared
 * across the application. It ensures consistency in settings and reduces duplication of
 * constant definitions throughout the codebase.
 * </p>
 *
 * @since 1.1.3
 */
public class SystemConstants {
    private SystemConstants() {
        // Prevent instantiation of this utility class
    }

    /** Decimal scale {@link za.co.hpsc.web.utils.NumberUtil}'s calculations round/format to by default. */
    public static final int DEFAULT_SCALE = 5;

    /** Bare time-of-day pattern (e.g. {@code "14:30"}), composed into {@link #ISO_DATE_TIME_FORMAT}. */
    public static final String TIME_FORMAT = "HH:mm";
    /** ISO-8601 date pattern (e.g. {@code "2026-09-03"}) used for every date field's JSON/CSV representation. */
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    /** ISO-8601 date/time pattern (e.g. {@code "2026-09-03 14:30"}), {@link #ISO_DATE_FORMAT} + {@link #TIME_FORMAT}. */
    public static final String ISO_DATE_TIME_FORMAT = ISO_DATE_FORMAT + " " + TIME_FORMAT;
    /** Alias for {@link #ISO_DATE_FORMAT}, used where a request model's date field names its pattern generically. */
    public static final String DEFAULT_DATE_FORMAT = ISO_DATE_FORMAT;
    public static final String DEFAULT_DATE_TIME_FORMAT = ISO_DATE_TIME_FORMAT;

    /** Delimiter for CSV array-typed columns and semicolon-joined fields (e.g. email addresses, match stages). */
    public static final String ARRAY_SEPARATOR = ";";
}
