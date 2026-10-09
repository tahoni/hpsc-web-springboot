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

    /**
     * Regular expression matching the range officer marker some imported names carry, {@code "RO"} or
     * {@code "(RO)"}, anywhere in the name: before, between or after the other parts. {@code "RO"} must be a whole,
     * upper-case word, so it is not taken from inside a name such as "Romeo" or "PEDRO"; {@code "(RO)"} matches
     * wherever it appears. Every match is replaced by a space, and the name's whitespace then collapsed and trimmed,
     * before a competitor is matched by full name, so {@code "Jane Doe (RO)"}, {@code "RO Jane Doe"} and
     * {@code "Jane RO Doe"} all match {@code "Jane Doe"}.
     */
    public static final String REPLACE_IN_NAMES_REGEX = "(\\(RO\\)|\\bRO\\b)";

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
