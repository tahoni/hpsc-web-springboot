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
 *
 * @since 1.1.3
 */
public final class IpscConstants {
    private IpscConstants() {
        // Prevent instantiation of this utility class
    }

    /** Date pattern for every IPSC request DTO's date field (competitor date of birth, match date). */
    public static final String IPSC_INPUT_DATE_FORMAT = SystemConstants.ISO_DATE_FORMAT;
    public static final String IPSC_INPUT_DATE_TIME_FORMAT = SystemConstants.ISO_DATE_TIME_FORMAT;

    public static final List<String> EXCLUDE_ICS_ALIAS = List.of("15000", "16000");
    public static final List<ClubIdentifier> EXCLUDE_CLUB_IDENTIFIERS = List.of(ClubIdentifier.UNKNOWN);
    public static final Integer MAX_SAPSA_NUMBER = 99_999;

    public static final String REPLACE_IN_NAMES_REGEX = "(\\(RO\\)|RO)$";

    public static final int STAGE_POINTS_SCALE = 4;
    public static final int MATCH_POINTS_SCALE = 4;
    public static final int HIT_FACTOR_SCALE = 4;
    public static final int TIME_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 2;

    public static final MatchCategory DEFAULT_MATCH_CATEGORY = MatchCategory.CLUB_SHOOT;
    /**
     * Club identifier a match defaults to when its {@code club} field is omitted — the seeded joint-club record
     * ({@code "Eufees Clubs"}).
     */
    public static final ClubIdentifier DEFAULT_MATCH_CLUB_IDENTIFIER = ClubIdentifier.ALL;

    /** Abbreviation identifying HPSC as a competitor's home club — the only one that requires a club number. */
    public static final String HOME_CLUB_ABBREVIATION = "HPSC";
    /**
     * {@link ClubIdentifier} resolved from {@link #HOME_CLUB_ABBREVIATION}; null only if that abbreviation ever
     * stopped matching a known identifier — tolerated rather than asserted, so a resolution failure degrades
     * gracefully instead of crashing the app at class-load time.
     */
    public static final ClubIdentifier HOME_CLUB_IDENTIFIER =
            ClubIdentifier.fromAbbreviation(HOME_CLUB_ABBREVIATION).orElse(null);
}
