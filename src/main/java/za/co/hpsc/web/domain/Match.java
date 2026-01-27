package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.helpers.MatchHelpers;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a match within the system, encapsulating details about the match's
 * associated club, name, scheduled date, division, category, stages, and competitors.
 * <p>
 * The {@code Match} class is an entity in the persistence layer, used to store and
 * retrieve match-related data. It enables associations with other entities such as
 * {@code Club}, {@code MatchStage}, and {@code MatchCompetitor}.
 * <p>
 * Key attributes include:
 * - A unique identifier for the match.
 * - The associated club where the match is organised.
 * - The name of the match, which is mandatory and unique.
 * - The scheduled date of the match, which is mandatory.
 * - The division and category of the match, represented as enumerations.
 * - A list of stages and competitors linked to the match.
 * <p>
 * This class overrides the {@code toString} method to return a context-specific
 * representation of the match's display name.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "club_id")
    private Club club;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private Division matchDivision;
    @Enumerated(EnumType.STRING)
    private MatchCategory matchCategory;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchStage> matchStages;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchCompetitor> matchCompetitors;

    @Override
    public String toString() {
        return MatchHelpers.getMatchDisplayName(this);
    }
}
