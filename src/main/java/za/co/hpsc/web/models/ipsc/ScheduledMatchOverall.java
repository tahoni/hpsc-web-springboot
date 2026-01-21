package za.co.hpsc.web.models.ipsc;

import jakarta.validation.constraints.NotNull;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.utils.StringUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ScheduledMatchOverall {
    @NotNull
    ScheduledMatch match;

    ScheduledMatchOverall() {
        this.match = new ScheduledMatch();
    }

    @Override
    public String toString() {
        // Prepare date formatter
        DateTimeFormatter isoDateTimeFormatter =
                DateTimeFormatter.ofPattern(IpscConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "scheduledMatchName", match.toString(),
                "isoDate", isoDateTimeFormatter.format(match.getDate())
        );

        // Format and return the scheduled match stage name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                parameters);
    }
}
