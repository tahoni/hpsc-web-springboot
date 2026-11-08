package za.co.hpsc.web.constants;

import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.MatchCategory;

import java.util.List;

/**
 * Defines constants specific to the IPSC module.
 *
 * <p>
 * This class provides a centralised location for settings and configurations
 * used within the IPSC domain: the date and time formats of its request models, the competitor
 * number and name rules applied when matching imported data to a persisted competitor, the default
 * match category and club, and the home club.
 * </p>
 *
 * <p>
 * The class is {@code final} and cannot be instantiated; every member is a {@code public static final}
 * constant. Date and time formats are taken from {@link SystemConstants}.
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
    /** Date-time pattern for every IPSC request DTO's date-time fields. */
    public static final String IPSC_INPUT_DATE_TIME_FORMAT = SystemConstants.ISO_DATE_TIME_FORMAT;
    /** Bare time-of-day pattern for every IPSC request DTO's time-only fields (match start/end time). */
    public static final String IPSC_INPUT_TIME_FORMAT = SystemConstants.TIME_FORMAT;

    /**
     * Competitor numbers that are shared aliases (placeholders in the scoring system's export) rather than a real
     * competitor's number, so they never identify one. A competitor number in this list is treated as no number, as
     * by {@code CompetitorHelpers.getCompetitorNumberAsInteger} and the number stage of
     * {@code IpscEntityCompetitorService.findCompetitorByIdentifierAndFullName}.
     */
    public static final List<Integer> EXCLUDE_ICS_ALIAS = List.of(15000, 16000);

    /** Number of decimal places for a percentage. */
    public static final int PERCENTAGE_SCALE = 2;
    /**
     * Number of a competitor's best matches in a shooter log that count towards their overall percentage; a
     * competitor with fewer matches in the log has all of them counted.
     */
    public static final int SHOOTER_LOG_BEST_MATCHES = 4;

    /** Match category a match defaults to when none is given, by {@code IpscMatchServiceImpl.resolveMatchCategory}. */
    public static final MatchCategory DEFAULT_MATCH_CATEGORY = MatchCategory.CLUB_SHOOT;
    /**
     * Club identifier a match defaults to when its {@code club} field is omitted — the seeded joint-club record
     * ({@code "Eufees Clubs"}).
     */
    public static final ClubIdentifier DEFAULT_MATCH_CLUB_IDENTIFIER = ClubIdentifier.ALL;

    /**
     * Abbreviation identifying HPSC, the home club: the club a competitor must be a member of to have a club
     * number, and the club a bulk match competitor import is limited to unless another is asked for.
     */
    public static final String HOME_CLUB_ABBREVIATION = "HPSC";
    /**
     * {@link ClubIdentifier} resolved from {@link #HOME_CLUB_ABBREVIATION}, the default home club; null only if that
     * abbreviation ever stopped matching a known identifier — tolerated rather than asserted, so a resolution
     * failure degrades gracefully instead of crashing the app at class-load time.
     */
    public static final ClubIdentifier HOME_CLUB_IDENTIFIER =
            ClubIdentifier.fromAbbreviation(HOME_CLUB_ABBREVIATION).orElse(null);
}
