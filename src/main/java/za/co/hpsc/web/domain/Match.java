package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.helpers.MatchHelpers;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a match within the system, encapsulating details about the match's
 * associated club, name, scheduled date, division, category, stages, and competitors.
 *
 * <p>
 * The {@code Match} class is an entity in the persistence layer, used to store and
 * retrieve match-related data. It enables associations with other entities such as
 * {@link MatchStage}, and {@link MatchCompetitor}.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return a context-specific
 * representation of the match's display name.
 * </p>
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
    @Column(unique = true, nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private LocalDate scheduledDate;
    private String club;

    @Enumerated(EnumType.STRING)
    private FirearmType matchFirearmType;
    @Enumerated(EnumType.STRING)
    private MatchCategory matchCategory;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchStage> matchStages;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatchCompetitor> matchCompetitors;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    // TODO: Javadoc
    public void init(MatchDto matchDto) {
        this.name = matchDto.getName();
        this.scheduledDate = matchDto.getScheduledDate();
        this.club = matchDto.getClub();

        this.matchFirearmType = matchDto.getMatchFirearmType();
        this.matchCategory = matchDto.getMatchCategory();

        this.dateCreated = matchDto.getDateCreated();
        this.dateUpdated = matchDto.getDateUpdated();
        this.dateEdited = matchDto.getDateEdited();
    }

    @Override
    public String toString() {
        return MatchHelpers.getMatchDisplayName(this);
    }
}
