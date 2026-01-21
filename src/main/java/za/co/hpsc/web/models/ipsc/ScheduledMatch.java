package za.co.hpsc.web.models.ipsc;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.models.ipsc.matches.Match;
import za.co.hpsc.web.utils.StringUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ScheduledMatch {
    LocalDate date;
    @NotNull
    private Match match;
    @NotNull
    private List<ScheduledMatchStage> stages;
    @NotNull
    private ScheduledMatchOverall overall;

    public ScheduledMatch() {
        this.match = new Match();
        this.stages = new ArrayList<>();
        this.overall = new ScheduledMatchOverall();
    }

    @Override
    public String toString() {
        // Prepare date formatter
        DateTimeFormatter longDateFormatter = DateTimeFormatter.ofPattern(IpscConstants.MATCH_LONG_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "scheduledMatchName", match.toString(),
                "matchDateLong", (date != null ? longDateFormatter.format(date) : "")
        );

        // Format and return the scheduled match name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_NAME_FORMAT,
                parameters);
    }
}
