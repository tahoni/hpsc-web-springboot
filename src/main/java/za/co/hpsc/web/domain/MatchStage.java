package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.helpers.MatchHelpers;

import java.util.List;

/**
 * Represents a stage within a match, encompassing details about its associated match,
 * stage number, range number, and the competitors within the stage.
 * <p>
 * The {@code MatchStage} class is an entity in the persistence layer, used to store and
 * retrieve information regarding individual stages of a match. Each stage is uniquely
 * identified and linked to a specific match.
 * <p>
 * Key attributes include:
 * - A unique identifier for the stage.
 * - The match to which this stage belongs, represented by a mandatory many-to-one
 * relationship.
 * - The stage number, which is mandatory and represents the order of the stage.
 * - An optional range number associated with the stage.
 * - A list of competitors participating in this stage, represented by a one-to-many
 * relationship with {@code MatchStageCompetitor}.
 * <p>
 * The class overrides the {@code toString} method to provide a custom display representation
 * for the stage, as determined by {@code MatchHelpers.getMatchStageDisplayName}.
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
    private Integer rangeNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchStageCompetitor> matchStageCompetitors;

    @Override
    public String toString() {
        return MatchHelpers.getMatchStageDisplayName(this);
    }
}
