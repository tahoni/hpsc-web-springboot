package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.helpers.MatchHelpers;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MatchStage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id")
    private Match match;

    @NotNull
    @Column(nullable = false)
    private Integer stageNumber;
    private Integer rangeNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchStageCompetitor> matchStageCompetitors;

    public MatchStage(Match match, Integer stageNumber, Integer rangeNumber) {
        this.stageNumber = stageNumber;
        this.rangeNumber = rangeNumber;
        this.match = match;
    }

    @Override
    public String toString() {
        return MatchHelpers.getMatchStageDisplayName(this);
    }
}
