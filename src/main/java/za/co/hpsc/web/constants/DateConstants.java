package za.co.hpsc.web.constants;

/**
 * Provides constants for commonly used date and time formats.
 * These constants can be utilized throughout the application to standardize
 * the representation and formatting of date and time values.
 */
// TODO: Javadoc
public final class DateConstants {
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME_FORMAT = ISO_DATE_FORMAT + " HH:mm:ss.SSS";
    public static final String T_SEPARATED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LONG_DATE_FORMAT = "dd MMMM yyyy";
    public static final String LONG_DATE_TIME_FORMAT = LONG_DATE_FORMAT + " HH:mm";
}
