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

    /**
     * Constructs a response from a persisted/generated match ID and a {@link MatchDto}.
     * <p>
     * The {@code Long} ID is converted to {@code Integer}. Club ID is derived from
     * {@code matchDto.getClub().getIndex()} when a club is present; otherwise it is {@code null}.
     * </p>
     *
     * @param matchId  persisted or generated match ID
     * @param matchDto source DTO containing match details
     */
    public MatchResponse(Long matchId, MatchDto matchDto) {
        this.matchId = matchId.intValue();
        this.matchName = matchDto.getName();
        this.matchDate = matchDto.getScheduledDate();
        this.clubId = ((matchDto.getClub() != null) ? matchDto.getClub().getIndex() : null);
    }

    /**
     * Applies values from another {@link MatchResponse} to this instance using
     * either full-replacement or partial-merge semantics.
     * <p>
     * The provided {@code matchId} is always applied to this object first.
     * </p>
     * <ul>
     *   <li><b>Full update</b> ({@code fullUpdate = true}): all updatable fields are overwritten,
     *       including {@code null} values from {@code right}.</li>
     *   <li><b>Partial update</b> ({@code fullUpdate = false}): only non-null fields from
     *       {@code right} overwrite existing values.</li>
     * </ul>
     *
     * @param matchId    identifier to assign to this response; converted from {@link Long} to {@link Integer}
     * @param right      source response containing values to apply
     * @param fullUpdate {@code true} for full replacement, {@code false} for partial merge
     */
    public void init(Long matchId, MatchResponse right, boolean fullUpdate) {
        this.matchId = matchId.intValue();

        if (fullUpdate) {
            this.matchName = right.matchName;
            this.matchDate = right.matchDate;
            this.clubId = right.clubId;

            this.squadCount = right.squadCount;
            this.firearmId = right.firearmId;

        } else {
            this.matchName = (right.matchName != null) ? right.matchName : this.matchName;
            this.matchDate = (right.matchDate != null) ? right.matchDate : this.matchDate;
            this.clubId = (right.clubId != null) ? right.clubId : this.clubId;

            this.squadCount = (right.squadCount != null) ? right.squadCount : this.squadCount;
            this.firearmId = (right.firearmId != null) ? right.firearmId : this.firearmId;
        }
    }
}