package za.co.hpsc.web.models.ipsc.matchresult.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.IpscMatch;

import java.time.LocalDateTime;

/**
 * The match that a set of {@link MatchResultResponse}s were shot in, as returned by
 * {@code IpscMatchResultController}.
 *
 * @see MatchResultResponseHolder
 * @since 18.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultMatchResponse {
    /** The identifier of the match. */
    private Long matchId;
    /** The name of the match. */
    private String matchName;
    /** The date and time the match was scheduled for. */
    private LocalDateTime matchDate;

    /**
     * Creates a match description from a persisted match.
     *
     * @param match the match to convert. Must not be null.
     * @since 18.1.0
     */
    public MatchResultMatchResponse(IpscMatch match) {
        this.matchId = match.getId();
        this.matchName = match.getName();
        this.matchDate = match.getScheduledDate();
    }
}
