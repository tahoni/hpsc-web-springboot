package za.co.hpsc.web.helpers;

import za.co.hpsc.web.constants.MatchConstants;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.utils.StringUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class MatchHelpers {
    public static String getMatchDisplayName(Match match) {
        // Prepare date formatters
        DateTimeFormatter longDateFormatter =
                DateTimeFormatter.ofPattern(MatchConstants.MATCH_LONG_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "clubName", match.getClub().getName(),
                "divisionName", (match.getMatchDivision() != null ?
                        match.getMatchDivision().getDisplayName() : ""),
                "categoryName", (match.getMatchCategory() != null ?
                        match.getMatchCategory().getDisplayName() : ""),
                "longDate", longDateFormatter.format(match.getScheduledDate())
        );

        // Format and return match name
        return StringUtil.formatStringWithNamedParameters(MatchConstants.SCHEDULED_MATCH_NAME_FORMAT,
                parameters);
    }

    public static String getMatchOverallDisplayName(Match match) {
        // Prepare date formatters
        DateTimeFormatter isoDateFormatter =
                DateTimeFormatter.ofPattern(MatchConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "matchName", getMatchDisplayName(match),
                "isoDate", isoDateFormatter.format(match.getScheduledDate())
        );

        // Format and return stage match name
        return StringUtil.formatStringWithNamedParameters(MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                parameters);
    }

    public static String getMatchStageDisplayName(MatchStage matchStage) {
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
        return StringUtil.formatStringWithNamedParameters(MatchConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                parameters);
    }
}
