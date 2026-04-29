package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.request.MatchRequest;

import java.time.LocalDateTime;

/**
 * Represents a response model for match-related data within the system.
 *
 * <p>
 * This class encapsulates details of a match, such as its ID, name, date,
 * associated club, squad count, and firearm information. It provides
 * functionality to populate its fields directly from a {@link MatchRequest} object.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    @NotNull
    private Integer matchId;
    private String matchName;
    @NotNull
    private LocalDateTime matchDate;
    private Integer clubId;

    private Integer squadCount;
    private Integer firearmId;

    /**
     * Constructs a new {@code MatchResponse} object by initialising its fields using
     * the values from a given {@link MatchRequest} object.
     *
     * @param matchRequest the {@link MatchRequest} object containing data to initialise
     *                     the {@code MatchResponse} instance.
     */
    public MatchResponse(MatchRequest matchRequest) {
        this.matchId = matchRequest.getMatchId();
        this.matchName = matchRequest.getMatchName();
        this.matchDate = matchRequest.getMatchDate();
        this.clubId = matchRequest.getClubId();

        this.squadCount = matchRequest.getSquadCount();
        this.firearmId = matchRequest.getFirearmId();
    }

    public MatchResponse(Long matchId, MatchDto matchDto) {
        this.matchId = matchId.intValue();
        this.matchName = matchDto.getName();
        this.matchDate = matchDto.getScheduledDate();
        this.clubId = ((matchDto.getClub() != null) ? matchDto.getClub().getIndex() : null);
    }

    public MatchResponse(Long matchId, MatchResponse right) {
        this.matchId = matchId.intValue();
        this.matchName = right.matchName;
        this.matchDate = right.matchDate;
        this.clubId = right.getClubId();

        this.squadCount = right.getSquadCount();
        this.firearmId = right.getFirearmId();
    }

    // TODO: cater for missing values
    public void init(MatchDto matchDto) {
        this.matchName = matchDto.getName();
        this.matchDate = matchDto.getScheduledDate();
        this.clubId = ((matchDto.getClub() != null) ? matchDto.getClub().getIndex() : null);
    }

    // TODO: cater for missing values
    public void init(MatchResponse right, boolean fullUpdate) {
        this.matchName = right.matchName;
        this.matchDate = right.matchDate;
        this.clubId = right.getClubId();

        this.squadCount = right.getSquadCount();
        this.firearmId = right.getFirearmId();
    }
}
