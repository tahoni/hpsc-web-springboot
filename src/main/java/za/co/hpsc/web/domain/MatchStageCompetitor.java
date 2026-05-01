package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.converters.ClubIdentifierConverter;
import za.co.hpsc.web.converters.DivisionConverter;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.PowerFactorConverter;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.common.dto.MatchStageCompetitorDto;
import za.co.hpsc.web.utils.ValueUtil;

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
    @ManyToOne(optional = false)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "match_stage_id")
    private IpscMatchStage matchStage;

    @Convert(converter = ClubIdentifierConverter.class)
    private ClubIdentifier matchClub;
    @Convert(converter = FirearmTypeConverter.class)
    private FirearmType firearmType;
    @Convert(converter = DivisionConverter.class)
    private Division division;
    @Convert(converter = PowerFactorConverter.class)
    private PowerFactor powerFactor;

    @Column(name = "score_a")
    private Integer scoreA;
    @Column(name = "score_b")
    private Integer scoreB;
    @Column(name = "score_c")
    private Integer scoreC;
    @Column(name = "score_d")
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

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    /**
     * Initialises a {@code MatchStageCompetitor} instance using the data provided
     * in the {@code MatchStageCompetitorDto}.
     * <p>
     * The method sets various attributes related to the competitor's performance
     * in a specific match stage, such as scores, penalties, disqualification status,
     * and rankings, based on the data from the provided DTO object.
     *
     * @param matchStageCompetitorDto the data transfer object containing the
     *                                values to initialise the fields of this object
     */
    public void init(MatchStageCompetitorDto matchStageCompetitorDto) {
        if (matchStageCompetitorDto != null) {
            // Initialises the match stage and competitor attributes
            this.matchClub = matchStageCompetitorDto.getClub();
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
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String stage = ValueUtil.nullAsDefaultString(this.matchStage, "").trim();
        sb.append(stage);

        String competitor = ValueUtil.nullAsDefaultString(this.competitor, "").trim();
        if (!competitor.isEmpty()) {
            if (!stage.isEmpty()) {
                sb.append(": ");
            }
            sb.append(competitor);
        }

        return sb.toString().trim();
    }

    @PrePersist
    void onInsert() {
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
