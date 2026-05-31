package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

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
    @ManyToOne(optional = false)
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

    @OneToMany(mappedBy = "matchStage", fetch = FetchType.EAGER)
    private List<MatchStageCompetitor> matchStageCompetitors = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String stageName = ValueUtil.nullAsDefaultString(this.stageName, "").trim();
        sb.append(stageName).append(" ");

        Integer stageNumber = ValueUtil.nullAsDefault(this.stageNumber, null);
        if (stageNumber != null) {
            sb.append("(").append(stageNumber).append(")");
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
