package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.utils.StringUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Setter
@Entity
public class MatchStage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @NotNull
    private Integer stageNumber;
    private Integer rangeNumber;

    public MatchStage(Match match, Integer stageNumber, Integer rangeNumber) {
        this.stageNumber = stageNumber;
        this.rangeNumber = rangeNumber;
        this.match = match;
    }

    @Override
    public String toString() {
        // Prepare date formatters
        DateTimeFormatter isoDateFormatter =
                DateTimeFormatter.ofPattern(IpscConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "matchName", match.toString(),
                "stageNumber", stageNumber.toString(),
                "rangeNumber", rangeNumber.toString(),
                "isoDate", isoDateFormatter.format(getMatch().getScheduledDate())
        );

        // Format and return stage match name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                parameters);
    }
}
