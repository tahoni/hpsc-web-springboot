package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the relationship between a competitor and a specific match stage,
 * along with performance metrics specific to that stage.
 *
 * <p>
 * The {@code MatchStageCompetitor} class serves as an entity in the persistence layer,
 * linking a competitor ({@link MatchCompetitorDto}) and a match stage ({@link MatchStageDto})
 * while storing detailed performance data for the competitor in the stage.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to provide a human-readable string
 * representation containing details about the match stage and competitor involved.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// TOOO: fix Javadoc
public class MatchStageCompetitorDto {
    private Long id;

    @NotNull
    private CompetitorDto competitor;
    @NotNull
    private MatchStageDto matchStage;

    private Integer scoreA;
    private Integer scoreB;
    private Integer scoreC;
    private Integer scoreD;

    private Integer points;
    private Integer misses;
    private Integer penalties;
    private Integer procedurals;

    private Boolean hasDeduction;
    private BigDecimal deductionPercentage;

    private BigDecimal time;
    private BigDecimal hitFactor;
    private BigDecimal stagePoints;
    private BigDecimal stagePercentage;

    private Boolean isDisqualified;

    private LocalDateTime dateUpdated;

    /**
     * Constructs a new instance of the {@code MatchStageCompetitor} class, representing
     * the association between a specific match competitor and a match stage.
     *
     * @param competitor the competitor participating in the match stage.
     *                   Must not be null.
     * @param matchStage the match stage in which the competitor is participating.
     *                   Must not be null.
     */
    public MatchStageCompetitorDto(@NotNull CompetitorDto competitor, @NotNull MatchStageDto matchStage) {
        this.competitor = competitor;
        this.matchStage = matchStage;
    }

    public void init(ScoreResponse scoreResponse) {
        this.scoreA = scoreResponse.getScoreA();
        this.scoreB = scoreResponse.getScoreB();
        this.scoreC = scoreResponse.getScoreC();
        this.scoreD = scoreResponse.getScoreD();

        this.points = scoreResponse.getFinalScore();
        this.misses = scoreResponse.getMisses();
        this.penalties = scoreResponse.getPenalties();
        this.procedurals = scoreResponse.getProcedurals();

        this.hasDeduction = scoreResponse.getDeduction();
        this.deductionPercentage = BigDecimal.valueOf(scoreResponse.getDeductionPercentage());

        this.time = scoreResponse.getTime();
        this.hitFactor = scoreResponse.getHitFactor();
        this.stagePoints = BigDecimal.valueOf(scoreResponse.getFinalScore());

        this.isDisqualified = scoreResponse.getIsDisqualified();

        this.dateUpdated = scoreResponse.getLastModified();
    }

    @Override
    public String toString() {
        return matchStage.toString() + ": " + competitor.toString();
    }
}
