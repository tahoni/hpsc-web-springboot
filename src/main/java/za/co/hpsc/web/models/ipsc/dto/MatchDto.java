package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private Integer index;

    private ClubDto club;
    private Integer clubIndex;

    private List<ScoreDto> scores;

    @NotNull
    private String name = "";
    @NotNull
    private LocalDateTime scheduledDate;

    private FirearmType matchFirearmType;
    private MatchCategory matchCategory;

    private LocalDateTime dateEdited;

    /**
     * Constructs a new {@code MatchDto} instance with data from the provided
     * {@link IpscMatch} entity.
     *
     * @param matchEntity the {@link IpscMatch} entity containing match-related information, such as
     *                    the unique identifier, associated club, name, scheduled date, match
     *                    firearm type, match category, creation timestamp, and update timestamp.
     *                    Must not be null.
     */
    public MatchDto(@NotNull IpscMatch matchEntity) {
        if (matchEntity == null) {
            return;
        }

        // Initialises match details
        this.id = matchEntity.getId();

        // Initialises club details from the associated entity
        if (matchEntity.getClub() != null) {
            this.club = new ClubDto(matchEntity.getClub());
        }

        // Initialises the match attributes
        this.name = matchEntity.getName();
        this.scheduledDate = matchEntity.getScheduledDate();
        this.matchFirearmType = matchEntity.getMatchFirearmType();
        this.matchCategory = matchEntity.getMatchCategory();

        // Initialises the date fields
        this.dateEdited = matchEntity.getDateEdited();
    }

    /**
     * Constructs a new {@code MatchDto} instance using the provided {@link IpscMatch} entity
     * and {@link ClubDto} object.
     *
     * @param matchEntity the {@link IpscMatch} entity containing match-related information such as
     *                    the unique identifier, name, scheduled date, division, category,
     *                    creation timestamp, and update timestamp. Must not be null.
     * @param clubDto     the {@link ClubDto} instance representing the club associated with the match.
     *                    Can be null.
     */
    public MatchDto(IpscMatch matchEntity, ClubDto clubDto) {
        if (matchEntity != null) {
            // Initialises match details
            this.id = matchEntity.getId();

            // Initialises club details from the DTO or associated entity
            if (clubDto != null) {
                this.club = clubDto;
            } else if (matchEntity.getClub() != null) {
                this.club = new ClubDto(matchEntity.getClub());
            }

            // Initialises the match attributes
            this.name = matchEntity.getName();
            this.scheduledDate = matchEntity.getScheduledDate();
            this.matchFirearmType = matchEntity.getMatchFirearmType();
            this.matchCategory = matchEntity.getMatchCategory();

            // Initialises the date fields
            this.dateEdited = matchEntity.getDateEdited();
        }

        if (clubDto != null) {
            this.clubIndex = clubDto.getIndex();
            this.club = clubDto;
        }
    }


    /**
     * Initialises the current {@code MatchDto} object using the provided
     * {@link MatchResponse}, {@link ClubDto}, and a list of {@link ScoreResponse} objects.
     *
     * <p>
     * The method sets various attributes of the match, including the name, associated club,
     * scheduled date, firearm type, and other metadata such as creation and update timestamps.
     * The last edited timestamp is determined either from the list of score responses or
     * defaults to the current time.
     * </p>
     *
     * @param matchResponse  the {@link MatchResponse} object containing information about the match,
     *                       such as its name, date, and firearm type.
     *                       Must not be null.
     * @param clubDto        the {@link ClubDto} object representing the club associated with the match.
     *                       Can be null if no club association is required.
     * @param scoreResponses a list of {@link ScoreResponse} objects containing information about scores
     *                       and their last modification times.
     *                       Can be null if no score data is available.
     */
    public void init(MatchResponse matchResponse, ClubDto clubDto, List<ScoreResponse> scoreResponses) {
        if (matchResponse != null) {
            // Initialises match details
            this.index = matchResponse.getMatchId();

            // Initialises club details from the DTO or associated entity
            if (clubDto != null) {
                this.club = clubDto;
                this.clubIndex = clubDto.getIndex();
            } else {
                this.clubIndex = matchResponse.getClubId();
            }

            // Initialises the match attributes
            this.name = matchResponse.getMatchName();
            this.scheduledDate = matchResponse.getMatchDate();

            // Determines the firearm type based on the firearm ID
            this.matchFirearmType = FirearmType.getByCode(matchResponse.getFirearmId())
                    .orElse(this.matchFirearmType);
            this.matchCategory = IpscConstants.DEFAULT_MATCH_CATEGORY;

            // Sets the date edited to the latest score update timestamp
            if (scoreResponses != null) {
                this.scores = scoreResponses.stream()
                        .filter(Objects::nonNull)
                        .filter(scoreResponse -> Objects.equals(scoreResponse.getMatchId(), this.index))
                        .map(ScoreDto::new)
                        .toList();
                this.dateEdited = this.scores.stream()
                        .map(ScoreDto::getLastModified)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());
            } else {
                this.dateEdited = LocalDateTime.now();
            }
        }
    }

    /**
     * Returns a string representation of the match's information, including the name and,
     * if available, information about the associated club.
     *
     * <p>
     * If a club object is available, its string representation is included in the result.
     * If a club object is not available, the value of the clubName field is used instead.
     * If neither contains a value, only the name is returned.
     * </p>
     *
     * @return a string combining the match name and the associated club information if available.
     */
    @Override
    public String toString() {
        String clubString = ((this.club != null) ? this.club.toString().trim() : null);
        String nameString = ((this.name != null) ? this.name.trim() : null);

        // Returns name, optionally with club if available
        String result = "";
        if ((clubString != null) && (!clubString.isBlank())) {
            result = nameString + " @ " + clubString;
        } else if (nameString != null) {
            result = nameString;
        }
        return result.trim();
    }
}
