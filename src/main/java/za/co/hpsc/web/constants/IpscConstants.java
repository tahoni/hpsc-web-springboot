package za.co.hpsc.web.constants;

// TODO: Javadoc
public final class IpscConstants {
    public static final String STAGE_NAME_PREFIX = "Stage";
    public static final String RANGE_NAME_PREFIX = "Range";


    public static final String MATCH_NAME_FORMAT = """
              ${clubName} ${divisionName} ${categoryName}
            """;
    public static final String STAGE_RANGE_NAME_FORMAT = """
                ${stageName} ${rangeName}
            """;

    public static final String SCHEDULED_MATCH_NAME_FORMAT = """
                ${matchName} ${longDate}
            """;
    public static final String SCHEDULED_MATCH_STAGE_NAME_FORMAT = """
                ${scheduledMatchName} - ${stageRangeName} - ${isoDate}
            """;
    public static final String SCHEDULED_MATCH_OVERALL_NAME_FORMAT = """
                ${scheduledMatchName} - ${isoDate}
            """;

    public static final String MATCH_ISO_DATE_FORMAT = DateConstants.ISO_DATE_FORMAT;
    public static final String MATCH_LONG_DATE_FORMAT = DateConstants.LONG_DATE_FORMAT;
}
