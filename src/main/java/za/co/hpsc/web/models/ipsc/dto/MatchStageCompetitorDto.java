package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.disciplines.FirearmTypeToDisciplinesForFirearmType;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;
import za.co.hpsc.web.utils.NumberUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private UUID uuid = UUID.randomUUID();
    private Long id;

    @NotNull
    private CompetitorDto competitor;
    @NotNull
    private MatchStageDto matchStage;

    private FirearmType firearmType;
    private Discipline discipline;
    private PowerFactor powerFactor;

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
    private BigDecimal stageRanking;

    private Boolean isDisqualified;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

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

        this.firearmType = matchStageCompetitorEntity.getFirearmType();
        this.discipline = matchStageCompetitorEntity.getDiscipline();
        this.powerFactor = matchStageCompetitorEntity.getPowerFactor();

        this.scoreA = matchStageCompetitorEntity.getScoreA();
        this.scoreB = matchStageCompetitorEntity.getScoreB();
        this.scoreC = matchStageCompetitorEntity.getScoreC();
        this.scoreD = matchStageCompetitorEntity.getScoreD();

        this.points = matchStageCompetitorEntity.getPoints();
        this.misses = matchStageCompetitorEntity.getMisses();
        this.penalties = matchStageCompetitorEntity.getPenalties();
        this.procedurals = matchStageCompetitorEntity.getProcedurals();

        this.hasDeduction = matchStageCompetitorEntity.getHasDeduction();
        this.deductionPercentage = matchStageCompetitorEntity.getDeductionPercentage();

        this.time = matchStageCompetitorEntity.getTime();
        this.hitFactor = matchStageCompetitorEntity.getHitFactor();

        this.stagePoints = matchStageCompetitorEntity.getStagePoints();
        this.stagePercentage = matchStageCompetitorEntity.getStagePercentage();
        this.stageRanking = matchStageCompetitorEntity.getStageRanking();

        this.isDisqualified = matchStageCompetitorEntity.getIsDisqualified();

        this.dateCreated = matchStageCompetitorEntity.getDateCreated();
        this.dateUpdated = LocalDateTime.now();
        this.dateEdited = matchStageCompetitorEntity.getDateEdited();
    }

    /**
     * Constructs a new {@code MatchStageCompetitorDto} instance, associating a competitor
     * with a match stage.
     *
     * @param competitorDto the {@link  CompetitorDto} representing the competitor in the match stage.
     *                      Must not be null.
     * @param matchStageDto the {@link MatchStageDto} representing the match stage in which
     *                      the competitor participates.
     *                      Must not be null.
     */
    public MatchStageCompetitorDto(@NotNull CompetitorDto competitorDto, @NotNull MatchStageDto matchStageDto) {
        this.competitor = competitorDto;
        this.matchStage = matchStageDto;

        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        this.dateEdited = LocalDateTime.now();
    }

    /**
     * Initialises the current {@code MatchStageDto} instance with data from the provided
     * {@link ScoreResponse} object.
     *
     * @param scoreResponse    the {@link ScoreResponse} object containing performance metrics
     *                         and detailed scoring information.
     *                         Must not be null.
     * @param enrolledResponse the {@link EnrolledResponse} object containing information about the
     *                         competitor information in the match stage.
     *                         Can be null.
     * @param matchStageDto    the {@link MatchStageDto} object containing stage-related information,
     *                         Can be null.
     */
    // TODO: Javadoc
    public void init(@NotNull ScoreResponse scoreResponse, EnrolledResponse enrolledResponse,
                     MatchStageDto matchStageDto) {
        this.scoreA = scoreResponse.getScoreA();
        this.scoreB = scoreResponse.getScoreB();
        this.scoreC = scoreResponse.getScoreC();
        this.scoreD = scoreResponse.getScoreD();

        this.points = scoreResponse.getFinalScore();
        this.misses = scoreResponse.getMisses();
        this.penalties = scoreResponse.getPenalties();
        this.procedurals = scoreResponse.getProcedurals();

        this.hasDeduction = scoreResponse.getDeduction();
        this.deductionPercentage = ValueUtil.nullAsZeroBigDecimal(scoreResponse.getDeductionPercentage());

        this.time = ValueUtil.nullAsZeroBigDecimal(scoreResponse.getTime());
        this.hitFactor = ValueUtil.nullAsZeroBigDecimal(scoreResponse.getHitFactor());

        this.stagePoints = BigDecimal.valueOf(ValueUtil.nullAsZero(scoreResponse.getFinalScore()));
        this.stagePercentage =
                NumberUtil.calculatePercentage(this.stagePoints, matchStageDto.getMaxPoints());

        this.isDisqualified = scoreResponse.getIsDisqualified();

        // Don't overwrite an existing date creation timestamp
        this.dateCreated = ((this.dateCreated != null) ? this.dateCreated : LocalDateTime.now());
        // Initialises the date updated
        this.dateUpdated = LocalDateTime.now();
        // Sets the date edited to the latest score update timestamp
        this.dateEdited = scoreResponse.getLastModified();

        if (enrolledResponse != null) {
            // Determines the power factor based on the major power factor flag
            this.powerFactor = (enrolledResponse.getMajorPowerFactor() ? PowerFactor.MAJOR : PowerFactor.MINOR);
            this.firearmType = FirearmType.getByCode(enrolledResponse.getDivisionId()).orElse(null);
            // Determines the discipline based on the division ID
            this.discipline = Discipline.getByCode(enrolledResponse.getDivisionId()).orElse(null);
            // Determines the firearm type from the discipline
            this.firearmType =
                    FirearmTypeToDisciplinesForFirearmType.getFirearmTypeFromDiscipline(this.discipline);
            // TODO: populate category
        }
    }

    @Override
    public String toString() {
        return matchStage.toString() + ": " + competitor.toString();
    }
}
