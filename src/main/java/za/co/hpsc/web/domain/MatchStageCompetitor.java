package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents the relationship between a competitor and a specific match stage,
 * along with performance metrics specific to that stage.
 * <p>
 * The {@code MatchStageCompetitor} class serves as an entity in the persistence layer,
 * linking a competitor ({@code MatchCompetitor}) and a match stage ({@code MatchStage})
 * while storing detailed performance data for the competitor in the stage.
 * <p>
 * Key attributes include:
 * - A unique identifier for each instance.
 * - A mandatory reference to the {@code MatchCompetitor} associated with this stage.
 * - A mandatory reference to the {@code MatchStage} where the competitor participated.
 * - Performance metrics such as points, penalties, time, hit factor, stage points, and stage percentage.
 * <p>
 * Instances of this class are uniquely stored and managed within the persistence layer.
 * The class also overrides the {@code toString} method to provide a human-readable string
 * representation containing details about the match stage and competitor involved.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MatchStageCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_competitor_id")
    private MatchCompetitor matchCompetitor;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_stage_id")
    private MatchStage matchStage;

    private Integer points;
    private Integer penalties;
    private BigDecimal time;
    private BigDecimal hitFactor;
    private BigDecimal stagePoints;
    private BigDecimal stagePercentage;

    @Override
    public String toString() {
        return matchStage.toString() + ": " + matchCompetitor.toString();
    }
}
