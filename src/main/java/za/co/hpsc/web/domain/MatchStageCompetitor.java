package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.dto.MatchStageCompetitorDto;
import za.co.hpsc.web.utils.NumberUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the relationship between a competitor and a specific match stage,
 * along with performance metrics specific to that stage.
 *
 * <p>
 * The {@code MatchStageCompetitor} class serves as an entity in the persistence layer,
 * linking a competitor ({@link MatchCompetitor}) and a match stage ({@link IpscMatchStage})
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
@Entity
public class MatchStageCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_stage_id")
    private IpscMatchStage matchStage;

    private FirearmType firearmType;
    private Division division;
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

    @Enumerated(EnumType.STRING)
    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    /**
     * Initialises the current {@code MatchStageCompetitor} entity with data from a DTO
     * and associated entities.
     *
     * <p>
     * This method sets the relevant fields in the entity, including association with a stage,
     * competitor information, performance metrics, and date attributes.
     * </p>
     *
     * @param matchStageCompetitorDto the DTO containing data needed to populate the entity fields.
     * @param matchStageEntity        the associated match stage entity.
     * @param competitorEntity        the associated competitor entity.
     */
    public void init(MatchStageCompetitorDto matchStageCompetitorDto, IpscMatchStage matchStageEntity,
                     Competitor competitorEntity) {

        // Initialises the match stage and competitor details
        this.matchStage = matchStageEntity;
        this.competitor = competitorEntity;

        // Initialises the match stage and competitor attributes
        this.competitorCategory = matchStageCompetitorDto.getCompetitorCategory();
        this.firearmType = matchStageCompetitorDto.getFirearmType();
        this.division = matchStageCompetitorDto.getDivision();
        this.powerFactor = matchStageCompetitorDto.getPowerFactor();

        // Initialises the detailed breakdown of the score
        this.scoreA = matchStageCompetitorDto.getScoreA();
        this.scoreB = matchStageCompetitorDto.getScoreB();
        this.scoreC = matchStageCompetitorDto.getScoreC();
        this.scoreD = matchStageCompetitorDto.getScoreD();

        // Initialises the overall performance metrics
        this.points = matchStageCompetitorDto.getPoints();
        this.misses = matchStageCompetitorDto.getMisses();
        this.penalties = matchStageCompetitorDto.getPenalties();
        this.procedurals = matchStageCompetitorDto.getProcedurals();

        // Initialises the deduction details, if applicable
        this.hasDeduction = matchStageCompetitorDto.getHasDeduction();
        this.deductionPercentage = matchStageCompetitorDto.getDeductionPercentage();

        // Initialises whether the competitor is disqualified
        this.isDisqualified = matchStageCompetitorDto.getIsDisqualified();

        // Initialises the time and hit factor details
        this.time = matchStageCompetitorDto.getTime();
        this.hitFactor = matchStageCompetitorDto.getHitFactor();

        // Initialises the stage ranking and percentage
        this.stagePoints = matchStageCompetitorDto.getStagePoints();
        this.stagePercentage = matchStageCompetitorDto.getStagePercentage();
        this.stageRanking = matchStageCompetitorDto.getStageRanking();

        // Initialises the date fields
        this.dateCreated = matchStageCompetitorDto.getDateCreated();
        this.dateUpdated = matchStageCompetitorDto.getDateUpdated();
        this.dateEdited = matchStageCompetitorDto.getDateEdited();
    }

    // TODO: Javadoc
    public void refreshRankings(BigDecimal highestScore) {
        this.stagePoints = NumberUtil.calculatePercentage(stagePoints, highestScore);
        this.dateRefreshed = LocalDateTime.now();
        this.getMatchStage().getMatch().setDateRefreshed(this.dateRefreshed);
    }

    @Override
    public String toString() {
        return this.matchStage.toString() + ": " + this.competitor.toString();
    }
}
