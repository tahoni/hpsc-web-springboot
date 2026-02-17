package za.co.hpsc.web.constants;

import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.MatchCategory;

import java.util.List;

/**
 * Defines constants specific to the IPSC module.
 *
 * <p>
 * This class provides a centralised location for settings and configurations
 * used within the IPSC domain.
 * </p>
 */
public final class IpscConstants {
    public static final String IPSC_INPUT_DATE_TIME_FORMAT =
            SystemConstants.T_SEPARATED_DATE_TIME_FORMAT;
    public static final String IPSC_OUTPUT_DATE_FORMAT =
            SystemConstants.ISO_DATE_FORMAT;
    public static final String IPSC_OUTPUT_DATE_TIME_FORMAT =
            SystemConstants.ISO_DATE_TIME_FORMAT;

    public static final List<Integer> EXCLUDE_ICS_ALIAS =
            List.of(15000, 16000);
    public static final List<ClubIdentifier> EXCLUDE_CLUB_IDENTIFIERS =
            List.of(ClubIdentifier.UNKNOWN);

    public static final String REPLACE_IN_NAMES_REGEX = "(\\(RO\\)|RO)$";

    public static final int STAGE_POINTS_SCALE = 4;
    public static final int MATCH_POINTS_SCALE = 4;
    public static final int HIT_FACTOR_SCALE = 4;
    public static final int TIME_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 2;

    public static final MatchCategory DEFAULT_MATCH_CATEGORY = MatchCategory.CLUB_SHOOT;
}
