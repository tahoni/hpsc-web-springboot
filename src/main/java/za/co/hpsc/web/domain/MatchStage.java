package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.helpers.MatchHelpers;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;

import java.util.List;

/**
 * Represents a stage within a match, encompassing details about its associated match,
 * stage number, range number, and the competitors within the stage.
 *
 * <p>
 * The {@code MatchStage} class is an entity in the persistence layer, used to store and
 * retrieve information regarding individual stages of a match. Each stage is uniquely
 * identified and linked to a specific match.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return a context-specific
 * representation of the stage.
 * </p>
 */
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
    private String stageName;
    private Integer rangeNumber;

    private Integer targetPaper;
    private Integer targetPopper;
    private Integer targetPlates;
    private Integer targetDisappear;
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchStageCompetitor> matchStageCompetitors;

    /**
     * Initialises the current {@code MatchStage} entity with data from a DTO
     * and associated entities.
     *
     * <p>
     * This method sets the stage's unique identifier, match association, stage number,
     * name, range number, target details, and point constraints based on the input data.
     * </p>
     *
     * @param stage       The {@code MatchStageDto} containing stage details such as ID, stage number,
     *                    range number, target metrics, and point constraints.
     * @param matchEntity The {@code Match} entity representing the match associated with the stage.
     */
    public void init(MatchStageDto stage, Match matchEntity) {
        // Initialises the stage details
        this.id = stage.getId();
        this.match = matchEntity;

        // Initialises the stage attributes
        this.stageNumber = stage.getStageNumber();
        this.stageName = stage.getStageName();
        this.rangeNumber = stage.getRangeNumber();

        // Initialises the target details
        this.targetPaper = stage.getTargetPaper();
        this.targetPopper = stage.getTargetPopper();
        this.targetPlates = stage.getTargetPlates();
        this.targetDisappear = stage.getTargetDisappear();
        this.targetPenalty = stage.getTargetPenalty();

        // Initialises the possible points details
        this.minRounds = stage.getMinRounds();
        this.maxPoints = stage.getMaxPoints();
    }

    @Override
    public String toString() {
        return MatchHelpers.getMatchStageDisplayName(this);
    }
}
