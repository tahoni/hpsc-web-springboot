package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.dto.MatchStageCompetitorDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the relationship between a competitor and a specific match stage,
 * along with performance metrics specific to that stage.
 *
 * <p>
 * The {@code MatchStageCompetitor} class serves as an entity in the persistence layer,
 * linking a competitor ({@link MatchCompetitor}) and a match stage ({@link MatchStage})
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_stage_id")
    private MatchStage matchStage;

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

    // TODO: Javadoc
    public void init(MatchStageCompetitorDto matchStageCompetitorDto, MatchStage matchStageEntity,
                     Competitor competitorEntity) {

        this.matchStage = matchStageEntity;
        this.competitor = competitorEntity;

        this.firearmType = matchStageCompetitorDto.getFirearmType();
        this.discipline = matchStageCompetitorDto.getDiscipline();
        this.powerFactor = matchStageCompetitorDto.getPowerFactor();

        this.scoreA = matchStageCompetitorDto.getScoreA();
        this.scoreB = matchStageCompetitorDto.getScoreB();
        this.scoreC = matchStageCompetitorDto.getScoreC();
        this.scoreD = matchStageCompetitorDto.getScoreD();

        this.points = matchStageCompetitorDto.getPoints();
        this.misses = matchStageCompetitorDto.getMisses();
        this.penalties = matchStageCompetitorDto.getPenalties();
        this.procedurals = matchStageCompetitorDto.getProcedurals();

        this.hasDeduction = matchStageCompetitorDto.getHasDeduction();
        this.deductionPercentage = matchStageCompetitorDto.getDeductionPercentage();

        this.time = matchStageCompetitorDto.getTime();
        this.hitFactor = matchStageCompetitorDto.getHitFactor();

        this.stagePoints = matchStageCompetitorDto.getStagePoints();
        this.stagePercentage = matchStageCompetitorDto.getStagePercentage();
        this.stageRanking = matchStageCompetitorDto.getStageRanking();

        this.isDisqualified = matchStageCompetitorDto.getIsDisqualified();

        this.dateCreated = matchStageCompetitorDto.getDateCreated();
        this.dateUpdated = matchStageCompetitorDto.getDateUpdated();
        this.dateEdited = matchStageCompetitorDto.getDateEdited();
    }

    @Override
    public String toString() {
        return matchStage.toString() + ": " + competitor.toString();
    }
}
