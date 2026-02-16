package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.ClubMatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) representing a club match.
 *
 * <p>
 * The {@code ClubMatchDto} class is used to transfer club match-related data.
 * It encapsulates details such as the associated club, the match itself,
 * and a list of competitors participating in the club match.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubMatchDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;

    private ClubDto club;
    private MatchDto match;

    private List<ClubMatchCompetitorDto> clubCompetitors = new ArrayList<>();

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    /**
     * Constructs a new {@code ClubMatchDto} instance with data from the
     * provided {@link ClubMatch} entity.
     *
     * @param clubMatchEntity the {@link ClubMatch} entity containing the club match's information.
     *                        Must not be null.
     */
    public ClubMatchDto(@NotNull ClubMatch clubMatchEntity) {
        this.id = clubMatchEntity.getId();
        this.club = new ClubDto(clubMatchEntity.getClub());
        this.match = new MatchDto(clubMatchEntity.getMatch());

        if (clubMatchEntity.getClubCompetitors() != null) {
            this.clubCompetitors = clubMatchEntity.getClubCompetitors().stream()
                    .map(ClubMatchCompetitorDto::new)
                    .collect(Collectors.toList());
        }

        this.dateCreated = clubMatchEntity.getDateCreated();
        this.dateUpdated = clubMatchEntity.getDateUpdated();
        this.dateEdited = clubMatchEntity.getDateEdited();
        this.dateRefreshed = clubMatchEntity.getDateRefreshed();
    }

    /**
     * Returns a string representation of this {@code ClubMatchDto} object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.match.toString() + " at " + this.club.toString();
    }
}
