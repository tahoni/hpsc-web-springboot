package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.DomainConstants;
import za.co.hpsc.web.enums.CompetitorCategory;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, length = DomainConstants.DEFAULT_STRING_COLUMN_LENGTH)
    private String firsName;
    @NotNull
    @Column(nullable = false, length = DomainConstants.DEFAULT_STRING_COLUMN_LENGTH)
    private String lastName;

    @Column(length = DomainConstants.DEFAULT_STRING_COLUMN_LENGTH)
    private String middleNames;
    @Column(length = DomainConstants.CODE_STRING_COLUMN_LENGTH)
    private String competitorNumber;

    private Integer sapsaNumber;
    private CompetitorCategory category = CompetitorCategory.NONE;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
