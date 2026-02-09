package za.co.hpsc.web.constants;

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

    public static final List<Integer> EXCLUDE_ICS_ALIAS =
            List.of(15000, 16000);

    public static final MatchCategory DEFAULT_MATCH_CATEGORY = MatchCategory.CLUB_SHOOT;
}
