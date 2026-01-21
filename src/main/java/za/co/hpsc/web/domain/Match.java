package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.utils.StringUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;
    @ManyToOne
    @JoinColumn(name = "club_id")
    @NotNull
    private Club club;
    @NotNull
    private LocalDate scheduledDate;

    private Division matchDivision;
    private MatchCategory matchCategory;

    @Lazy
    @OneToMany
    private List<MatchStage> matchStages;

    @Lazy
    @OneToMany
    private List<MatchCompetitor> matchCompetitors;

    @Override
    public String toString() {
        // Prepare date formatters
        DateTimeFormatter longDateFormatter =
                DateTimeFormatter.ofPattern(IpscConstants.MATCH_LONG_DATE_FORMAT);

        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "clubName", club.toString(),
                "divisionName", (matchDivision != null ? matchDivision.getDisplayName() : ""),
                "categoryName", (matchCategory != null ? matchCategory.getDisplayName() : ""),
                "longDate", longDateFormatter.format(scheduledDate)
        );

        // Format and return match name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.SCHEDULED_MATCH_NAME_FORMAT,
                parameters);
    }
}
