package za.co.hpsc.web.helpers;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import za.co.hpsc.web.constants.MatchConstants;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.utils.StringUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A utility class that provides helper methods for generating user-friendly display names
 * for matches and their related components, such as stages and overall scheduled information.
 *
 * <p>
 * This class contains static methods that format matches and their details based on the
 * application's established naming conventions and date formats. It makes use of constants
 * defined in {@link MatchConstants} for string templates and formatting rules.
 * The methods in this class expect various domain-specific inputs, such as {@link Match} and
 * {@link MatchStage}, and produce properly formatted strings for use in the UI or elsewhere
 * in the system.
 * </p>
 */
@Slf4j
public final class MatchHelpers {
    private MatchHelpers() {
        // Utility class, not to be instantiated
    }

    /**
     * Generates a formatted display name for a given match using its associated club, division,
     * category, and scheduled date information.
     * <p>
     * The method retrieves details such as the club name, match division display name (if available),
     * match category display name (if available), and the scheduled date formatted using a defined
     * pattern. It then uses these details to create a user-friendly string representation
     * of the match's display name.
     *
     * @param match the {@link Match} object containing the details used to construct the display name.
     *              Must not be null.
     * @return a formatted string representing the display name of the match, including its name,
     * division, category, and scheduled date.
     */
    public static String getMatchDisplayName(@NotNull Match match) {
        // Prepare date formatters
        DateTimeFormatter longDateFormatter =
                DateTimeFormatter.ofPattern(MatchConstants.MATCH_LONG_DATE_FORMAT);

        // Retrieve match details
        String clubName = (match.getClub() != null ? match.getClub().toString() : "");
        if (clubName.isEmpty()) {
            clubName = match.getClubName().toString();
        }
        String divisionName = (match.getMatchFirearmType() != null ?
                match.getMatchFirearmType().toString().toUpperCase() : "");
        String categoryName = (match.getMatchCategory() != null ?
                match.getMatchCategory().toString() : "");
        String longDate = longDateFormatter.format(match.getScheduledDate());

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "clubName", clubName,
                "divisionName", divisionName,
                "categoryName", categoryName,
                "longDate", longDate
        );

        // Format and return match name
        String result = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_NAME_FORMAT, parameters);
        return result.replaceAll("\\s+", " ");
    }

    /**
     * Generates a formatted string representing the overall display name of a match,
     * including its name and scheduled date in ISO format.
     * <p>
     * The method retrieves the match display name through {@link #getMatchDisplayName(Match)}
     * and formats the scheduled date using the pattern defined in
     * {@link MatchConstants#MATCH_ISO_DATE_FORMAT}. These values are then combined
     * into a user-friendly string format using the template
     * {@link MatchConstants#SCHEDULED_MATCH_OVERALL_NAME_FORMAT}.
     *
     * @param match the {@link Match} object containing the details used to construct
     *              the overall display name.
     *              Must not be null.
     * @return a formatted string representing the overall display name of the match,
     * combining its name and ISO-formatted scheduled date.
     */
    public static String getMatchOverallDisplayName(@NotNull Match match) {
        // Prepare date formatters
        DateTimeFormatter isoDateFormatter =
                DateTimeFormatter.ofPattern(MatchConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "matchName", getMatchDisplayName(match),
                "isoDate", isoDateFormatter.format(match.getScheduledDate())
        );

        // Format and return stage match name
        String result =
                StringUtil.formatStringWithNamedParameters(
                        MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT, parameters);
        return result.replaceAll("\\s+", " ");
    }

    /**
     * Generates a formatted display name for a specific match stage by incorporating
     * the match's overall details, stage number, range number, and the scheduled date
     * in ISO format.
     * <p>
     * The method combines the match name (retrieved through {@link #getMatchDisplayName(Match)}),
     * the stage and range number, and the match's scheduled date (formatted using the
     * pattern defined in {@link MatchConstants#MATCH_ISO_DATE_FORMAT}). These components are
     * assembled into a display string using the template defined in
     * {@link MatchConstants#SCHEDULED_MATCH_STAGE_NAME_FORMAT}.
     *
     * @param matchStage the {@link MatchStage} object containing the details used to construct
     *                   the display name.
     *                   Must not be null.
     * @return a formatted string representing the display name of the match stage, including
     * the match name, stage and range numbers, and the scheduled date in ISO format.
     */
    public static String getMatchStageDisplayName(@NotNull MatchStage matchStage) {
        // Prepare date formatters
        DateTimeFormatter isoDateFormatter =
                DateTimeFormatter.ofPattern(MatchConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "matchName", getMatchDisplayName(matchStage.getMatch()),
                "stageNumber", String.valueOf(ValueUtil.nullAsZero(matchStage.getStageNumber())),
                "rangeNumber", String.valueOf(ValueUtil.nullAsZero(matchStage.getRangeNumber())),
                "isoDate", isoDateFormatter.format(matchStage.getMatch().getScheduledDate())
        );

        // Format and return stage match name
        String result =
                StringUtil.formatStringWithNamedParameters(
                        MatchConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT, parameters);
        return result.replaceAll("\\s+", " ");
    }
}
