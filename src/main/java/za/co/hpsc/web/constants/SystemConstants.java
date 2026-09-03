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

    public static final int DEFAULT_SCALE = 5;

    public static final String TIME_FORMAT = "HH:mm";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME_FORMAT = ISO_DATE_FORMAT + " " + TIME_FORMAT;
    public static final String DEFAULT_DATE_FORMAT = ISO_DATE_FORMAT;
    public static final String DEFAULT_DATE_TIME_FORMAT = ISO_DATE_TIME_FORMAT;

    public static final String ARRAY_SEPARATOR = ";";
}
