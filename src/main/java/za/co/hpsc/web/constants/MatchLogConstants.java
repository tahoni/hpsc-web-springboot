package za.co.hpsc.web.constants;

/**
 * Provides constants for match logging configurations.
 * <p>
 * This class defines the settings for managing the number of matches and places
 * to be considered when generating match-related logs. These constants ensure
 * consistency across the application for log handling related to matches.
 * <p>
 * Constants:
 * - {@code NUMBER_OF_MATCHES_USED_FOR_LOGS}: Defines the maximum number of matches
 * to be considered for logging purposes.
 * - {@code NUMBER_OF_PLACES_IN_LOGS}: Specifies the maximum number of places or
 * ranks to be included in the logs pertaining to matches.
 * <p>
 * This class is immutable and cannot be instantiated.
 */
// TODO: Javadoc
public final class MatchLogConstants {
    public static final int NUMBER_OF_MATCHES_USED_FOR_LOGS = 6;
    public static final int NUMBER_OF_PLACES_IN_LOGS = 10;
}
