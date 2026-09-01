package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single stage belonging to an IPSC match — just its identity (number and name), not its
 * results.
 *
 * <p>
 * Used both nested in {@link MatchRequest} (creating a match together with its stages, where
 * {@link #matchId} is typically unset until the match itself has been persisted) and, as
 * elements of a plain {@code List<MatchStageRequest>}, for adding or updating stages on an
 * already-existing match, where {@link #matchId} identifies that match.
 * </p>
 *
 * @since 7.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchStageRequest {
    /** Identifier of the match this stage belongs to; may be unset when nested in a {@link MatchRequest}
     * that is itself creating the match. */
    private Long matchId;
    /** The stage's number/order within the match. */
    @JsonProperty(required = true)
    private Integer stageNumber;
    /** The stage's name, e.g. "Stage 1 - The Bank Job". */
    private String stageName;

    /**
     * Constructs a {@code MatchStageRequest} from its JSON representation.
     *
     * @param matchId     the identifier of the match this stage belongs to; may be unset when nested in a
     *                    {@link MatchRequest} that is itself creating the match.
     * @param stageNumber the stage's number/order within the match. Must not be null.
     * @param stageName   the stage's name, e.g. "Stage 1 - The Bank Job".
     */
    @JsonCreator
    public MatchStageRequest(@JsonProperty("matchId") Long matchId,
                             @JsonProperty(value = "stageNumber", required = true) Integer stageNumber,
                             @JsonProperty("stageName") String stageName) {
        this.matchId = matchId;
        this.stageNumber = stageNumber;
        this.stageName = stageName;
    }
}
