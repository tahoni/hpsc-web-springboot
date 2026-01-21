package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import za.co.hpsc.web.enums.CompetitorCategory;

import java.util.List;

@Getter
@Setter
@Entity
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firsName;
    private String middleNames;
    private String lastName;
    private int sapsaNumber;
    @NotNull
    private String competitorNumber;
    private CompetitorCategory category = CompetitorCategory.NONE;

    @Lazy
    @OneToMany
    private List<MatchCompetitor> competitorMatches;

    @Override
    public String toString() {
        if ((middleNames != null) && (!middleNames.isBlank())) {
            return firsName + " " + middleNames + " " + lastName;
        } else {
            return firsName + " " + lastName;
        }
    }
}
