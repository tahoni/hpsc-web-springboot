package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a match within the system, encapsulating details about the match's
 * associated club, name, scheduled date, division, category, stages, and competitors.
 *
 * <p>
 * The {@code Match} class is an entity in the persistence layer, used to store and
 * retrieve match-related data. It enables associations with other entities such as
 * {@link ClubDto}, {@link MatchStageDto}, and {@link MatchCompetitorDto}.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return a context-specific
 * representation of the match's display name.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// TOOO: fix Javadoc
public class MatchDto {
    private Long id;

    private ClubDto club;

    @NotNull
    private String name;
    @NotNull
    private LocalDate scheduledDate;

    private Division matchDivision;
    private MatchCategory matchCategory;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    public MatchDto(Match match, ClubDto club) {
        this.id = match.getId();
        this.club = club;
        this.name = match.getName();
        this.scheduledDate = match.getScheduledDate();
        this.matchDivision = match.getMatchDivision();
        this.matchCategory = match.getMatchCategory();
        this.dateCreated = match.getDateCreated();
        this.dateUpdated = match.getDateUpdated();
    }

    public void init(MatchResponse matchResponse, ClubDto club) {
        this.club = club;
        this.name = matchResponse.getMatchName();
        this.scheduledDate = matchResponse.getMatchDate().toLocalDate();

        this.dateCreated = (this.dateCreated != null) ? this.dateCreated : LocalDateTime.now();

        // TODO: populate division and category
    }

    @Override
    public String toString() {
        return name + " @ " + club.toString();
    }
}
