package za.co.hpsc.web.constants;

/**
 * Defines constant values used throughout the application for formatting and naming
 * matches, stages, ranges, and associated schedule details.
 *
 * <p>
 * This class provides standardised naming conventions and date formats for matches
 * and their components, ensuring consistency across different parts of the application.
 * These constants include general prefixes, formatting templates, and references to
 * date format patterns from {@link SystemConstants}.
 * The constants defined here support the creation of user-friendly and properly formatted
 * strings for display purposes, including match names, stage and range identifiers, and
 * detailed scheduled match names.
 * </p>
 */
public final class MatchConstants {
    public static final String MATCH_NAME_FORMAT = "${clubName} ${divisionName} ${categoryName}";
    public static final String STAGE_RANGE_NAME_FORMAT = "Stage ${stageNumber} Range ${rangeNumber}";

    public static final String SCHEDULED_MATCH_NAME_FORMAT =
            MATCH_NAME_FORMAT + " - ${longDate}";
    public static final String SCHEDULED_MATCH_STAGE_NAME_FORMAT =
            "${matchName} - " + STAGE_RANGE_NAME_FORMAT + " - ${isoDate}";
    public static final String SCHEDULED_MATCH_OVERALL_NAME_FORMAT =
            "${matchName} - ${isoDate}";

    public static final String MATCH_ISO_DATE_FORMAT = SystemConstants.ISO_DATE_FORMAT;
    public static final String MATCH_LONG_DATE_FORMAT = SystemConstants.LONG_DATE_FORMAT;
}
