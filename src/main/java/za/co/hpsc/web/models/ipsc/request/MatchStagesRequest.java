package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request to add or update stages on an already-existing IPSC match.
 *
 * <p>
 * Unlike {@link MatchRequest}, which creates a new match together with its stages, this
 * request targets an existing match via {@link #matchId} and carries only the
 * {@link MatchStageRequest}s to attach to it.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchStagesRequest {
    /** Identifier of the existing match these stages belong to. */
    private Long matchId;
    /** The stages to add or update on the match. */
    private List<MatchStageRequest> stages;
}
