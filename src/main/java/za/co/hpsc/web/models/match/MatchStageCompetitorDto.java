package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the association between a competitor and a match stage,
 * along with performance metrics and scoring details.
 *
 * <p>
 * The {@code MatchStageCompetitorDto} class encapsulates data related to a competitor's
 * performance on a specific stage of a match.
 * It includes scores, penalties, points, time, hit factor, and other related performance metrics.
 * Additionally, it holds references to the associated competitor and match stage entities.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
     * Constructs a new {@code MatchStageCompetitorDto} instance with data from the
     * provided {@link MatchStageCompetitor} entity.
     *
     * @param matchStageCompetitorEntity the {@link MatchStageCompetitor} entity containing information
     *                                   about a competitor's participation in a specific match stage,
     *                                   such as the competitor, match stage, and associated identifier.
     *                                   Must not be null.
     */
    public MatchStageCompetitorDto(@NotNull MatchStageCompetitor matchStageCompetitorEntity) {
        this.id = matchStageCompetitorEntity.getId();
        this.competitor = new CompetitorDto(matchStageCompetitorEntity.getCompetitor());
        this.matchStage = new MatchStageDto(matchStageCompetitorEntity.getMatchStage());
    }

    /**
     * Constructs a new {@code MatchStageCompetitorDto} instance, associating a competitor
     * with a match stage.
     *
     * @param competitorDto the {@link  CompetitorDto} representing the competitor in the match stage.
     *                      Must not be null.
     * @param matchStageDto the {@link MatchStageDto} representing the match stage in which
     *                      the competitor participates. Must not be null.
     */
    public MatchStageCompetitorDto(@NotNull CompetitorDto competitorDto, @NotNull MatchStageDto matchStageDto) {
        this.competitor = competitorDto;
        this.matchStage = matchStageDto;
    }

    /**
     * Initialises the current {@code MatchStageDto} instance with data from the provided
     * {@link ScoreResponse} object.
     *
     * @param scoreResponse the {@link ScoreResponse} object containing performance metrics
     *                      and detailed scoring information. Must not be null.
     */
    public void init(@NotNull ScoreResponse scoreResponse) {
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
