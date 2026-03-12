package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stage within a match, encompassing details about its associated match,
 * stage number, range number, and the competitors within the stage.
 *
 * <p>
 * The {@code IpscMatchStage} class is an entity in the persistence layer, used to store and
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
public class IpscMatchStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id")
    private IpscMatch match;

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

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchStage")
    private List<MatchStageCompetitor> matchStageCompetitors = new ArrayList<>();

    /**
     * Initialises the current stage with details provided by the given MatchStageDto object.
     * The method sets the stage number, stage name, and range number and copies the targets
     * and scoring information from the provided DTO to the current stage.
     *
     * @param stage the {@code MatchStageDto} object containing the stage attributes and
     *              target/scoring details to be copied to the current stage
     */
    public void init(MatchStageDto stage) {
        // Initialises the stage attributes
        this.stageNumber = stage.getStageNumber();
        this.stageName = stage.getStageName();
        this.rangeNumber = stage.getRangeNumber();

        stage.copyTargetsAndScoringTo(this);
    }

    @Override
    public String toString() {
        return this.stageName + " (" + this.stageNumber + ")";
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
