package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.utils.StringUtil;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Setter
@Entity
public class MatchOverall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @Override
    public String toString() {
        // Prepare date formatters
        DateTimeFormatter isoDateFormatter =
                DateTimeFormatter.ofPattern(IpscConstants.MATCH_ISO_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "matchName", match.toString(),
                "isoDate", isoDateFormatter.format(getMatch().getScheduledDate())
        );

        // Format and return stage match name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                parameters);
    }
}
