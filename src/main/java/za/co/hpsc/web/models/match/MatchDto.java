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
     * Constructs a new {@code MatchDto} instance with data from the provided
     * {@link Match} entity.
     *
     * @param matchEntity the {@link Match} entity containing match-related information, such as
     *                    the unique identifier, associated club, name, scheduled date, match division,
     *                    match category, creation timestamp, and update timestamp.
     *                    Must not be null.
     */
    public MatchDto(@NotNull Match matchEntity) {
        this.id = matchEntity.getId();
        this.club = new ClubDto(matchEntity.getClub());
        this.name = matchEntity.getName();
        this.scheduledDate = matchEntity.getScheduledDate();
        this.matchDivision = matchEntity.getMatchDivision();
        this.matchCategory = matchEntity.getMatchCategory();
        this.dateCreated = matchEntity.getDateCreated();
        this.dateUpdated = matchEntity.getDateUpdated();
    }

    /**
     * Constructs a new {@code MatchDto} instance using the provided {@link Match} entity
     * and {@link ClubDto} object.
     *
     * @param matchEntity the {@link Match} entity containing match-related information such as
     *                    the unique identifier, name, scheduled date, division, category,
     *                    creation timestamp, and update timestamp. Must not be null.
     * @param clubDto     the {@link ClubDto} instance representing the club associated with the match.
     *                    Must not be null.
     */
    public MatchDto(@NotNull Match matchEntity, @NotNull ClubDto clubDto) {
        this.id = matchEntity.getId();
        this.club = clubDto;
        this.name = matchEntity.getName();
        this.scheduledDate = matchEntity.getScheduledDate();
        this.matchDivision = matchEntity.getMatchDivision();
        this.matchCategory = matchEntity.getMatchCategory();
        this.dateCreated = matchEntity.getDateCreated();
        this.dateUpdated = matchEntity.getDateUpdated();
    }

    // TODO: Javadoc (not yet ready)
    public void init(MatchResponse matchResponse, ClubDto clubDto) {
        this.club = clubDto;
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
