package za.co.hpsc.web.models.ipsc.common.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.common.request.MatchRequest;

import java.time.LocalDateTime;

/**
 * Response DTO representing match information exposed by the API.
 * <p>
 * This model contains identity and scheduling fields for a match, along with
 * optional metadata such as squad count and firearm type reference.
 * It can be created from request-layer objects or DTO-layer objects depending
 * on the calling workflow.
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
     * Constructs a response by copying values from a {@link MatchRequest}.
     *
     * @param matchRequest source request object containing match values
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