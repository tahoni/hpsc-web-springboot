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
 * Data Transfer Object (DTO) representing a shooting match.
 *
 * <p>
 * The {@code MatchDto} class is used to transfer match-related data between various layers
 * of the application.
 * It encapsulates details such as the match's unique identifier, associated club, name,
 * scheduled date, division, category, and timestamps for creation and updates.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Constructs a new {@code MatchDto} instance using the provided {@link Match} entity
     * and {@link ClubDto} object.
     *
     * @param match the {@link Match} entity containing match-related information such as
     *              the unique identifier, name, scheduled date, division, category,
     *              creation timestamp, and update timestamp. Must not be null.
     * @param club  the {@link ClubDto} instance representing the club associated with the match.
     *              Must not be null.
     */
    public MatchDto(@NotNull Match match, @NotNull ClubDto club) {
        this.id = match.getId();
        this.club = club;
        this.name = match.getName();
        this.scheduledDate = match.getScheduledDate();
        this.matchDivision = match.getMatchDivision();
        this.matchCategory = match.getMatchCategory();
        this.dateCreated = match.getDateCreated();
        this.dateUpdated = match.getDateUpdated();
    }

    // TODO: Javadoc
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
