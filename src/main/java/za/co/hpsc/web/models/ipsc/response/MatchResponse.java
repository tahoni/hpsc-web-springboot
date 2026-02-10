package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.MatchRequest;

import java.time.LocalDate;
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
    private Integer matchId = 0;
    private String matchName = "";
    @NotNull
    private LocalDateTime matchDate = LocalDate.now().atStartOfDay();
    private Integer clubId = 0;

    private Integer squadCount = 0;
    private Integer firearmId = 0;

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
}
