package za.co.hpsc.web.models.ipsc.match.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single persisted stage belonging to an IPSC match, as returned by the match endpoints.
 *
 * @since 8.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchStageResponse {
    /** The stage's own identifier. */
    private Long stageId;
    /** The stage's number/order within the match. */
    private Integer stageNumber;
    /** The stage's name, e.g. "Stage 1 - The Bank Job". */
    private String stageName;
}
