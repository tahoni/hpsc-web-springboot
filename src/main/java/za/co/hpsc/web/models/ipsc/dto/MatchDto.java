package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a shooting match.
 *
 * <p>
 * The {@code MatchDto} class is used to transfer match-related data between various layers
 * of the application.
 * It encapsulates details such as the match's unique identifier, associated club, name,
 * scheduled date, firearm type, category, and timestamps for creation and updates.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private LocalDate scheduledDate;
    private String club;

    private FirearmType matchFirearmType;
    private MatchCategory matchCategory;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    /**
     * Constructs a new {@code MatchDto} instance with data from the provided
     * {@link Match} entity.
     *
     * @param matchEntity the {@link Match} entity containing match-related information, such as
     *                    the unique identifier, associated club, name, scheduled date, match
     *                    firearm type, match category, creation timestamp, and update timestamp.
     *                    Must not be null.
     */
    public MatchDto(@NotNull Match matchEntity) {
        if (matchEntity == null) {
            return;
        }

        // Initialises match details
        this.id = matchEntity.getId();
        this.club = matchEntity.getClub();

        // Initialises the match attributes
        this.name = matchEntity.getName();
        this.scheduledDate = matchEntity.getScheduledDate();
        this.matchFirearmType = matchEntity.getMatchFirearmType();
        this.matchCategory = matchEntity.getMatchCategory();

        // Initialises the date fields
        this.dateCreated = matchEntity.getDateCreated();
        this.dateUpdated = LocalDateTime.now();
    }

    // TODO: Javadoc (not yet ready)
    public void init(MatchResponse matchResponse, List<ScoreResponse> scoreResponses) {
        // Initialises match details
        this.club = "";

        // Initialises the match attributes
        this.name = matchResponse.getMatchName();
        this.scheduledDate = matchResponse.getMatchDate().toLocalDate();

        // Determines the firearm type based on the firearm ID
        this.matchFirearmType = FirearmType.getByCode(matchResponse.getFirearmId()).orElse(null);
        // TODO: initialise match category

        // Don't overwrite an existing date creation timestamp
        this.dateCreated = ((this.dateCreated != null) ? this.dateCreated : LocalDateTime.now());
        // Initialises the date updated
        this.dateUpdated = LocalDateTime.now();
        // Sets the date edited to the latest score update timestamp
        if (scoreResponses != null) {
            this.dateEdited = scoreResponses.stream()
                    .map(ScoreResponse::getLastModified)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
        } else {
            this.dateEdited = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        if ((club != null) && (!club.isBlank())) {
            return name + " @ " + club;
        } else {
            return name;
        }
    }
}
