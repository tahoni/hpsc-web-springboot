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
 */
public class SystemConstants {
    public static final int DEFAULT_SCALE = 5;

    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME_FORMAT = ISO_DATE_FORMAT + " HH:mm";
    public static final String T_SEPARATED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LONG_DATE_FORMAT = "dd MMMM yyyy";
    public static final String LONG_DATE_TIME_FORMAT = LONG_DATE_FORMAT + " HH:mm";
}
