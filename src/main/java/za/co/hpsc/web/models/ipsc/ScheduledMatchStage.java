package za.co.hpsc.web.models.ipsc;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.models.ipsc.matches.StageRange;
import za.co.hpsc.web.utils.StringUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ScheduledMatchStage {
    @NotNull
    private ScheduledMatch match;
    @NotNull
    private StageRange stageRange;

    public ScheduledMatchStage() {
        this.match = new ScheduledMatch();
        this.stageRange = new StageRange();
    }

    @Override
    public String toString() {
        // Prepare date formatter
        DateTimeFormatter isoDateTimeFormatter =
                DateTimeFormatter.ofPattern(IpscConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "scheduledMatchName", match.toString(),
                "stageRangeName", stageRange.toString(),
                "isoDate", isoDateTimeFormatter.format(match.getDate())
        );

        // Format and return the scheduled match stage name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                parameters);
    }
}
