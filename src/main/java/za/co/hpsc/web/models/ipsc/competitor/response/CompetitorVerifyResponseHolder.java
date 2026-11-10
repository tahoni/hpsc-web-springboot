package za.co.hpsc.web.models.ipsc.competitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link CompetitorVerifyResponse}s of a bulk competitor verification, one per distinct
 * competitor ID given, each recording whether that competitor was found and verified.
 *
 * @see CompetitorVerifyResponse
 * @since 16.2.0
 */
@Getter
@Setter
@AllArgsConstructor
public class CompetitorVerifyResponseHolder {
    /** The outcome for each competitor ID, in the order first given in the request. */
    @NotNull
    private List<CompetitorVerifyResponse> competitorResults;
}
