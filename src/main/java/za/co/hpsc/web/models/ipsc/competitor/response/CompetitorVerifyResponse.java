package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of verifying a single competitor in a bulk verification: whether the competitor was found, a message
 * describing the outcome, and the {@link CompetitorResponse} it relates to.
 *
 * @see CompetitorVerifyResponseHolder
 * @see CompetitorResponse
 * @since 16.2.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorVerifyResponse {
    /** The identifier of the competitor, as given in the request. */
    private Long competitorId;
    /** Whether a competitor with the identifier exists and is now verified. */
    private boolean success = true;
    /**
     * A message describing the outcome: empty when the competitor was verified, a note when it already was, and the
     * reason when no competitor was found.
     */
    private String message = "";
    /** The verified competitor; {@code null} when no competitor was found with the identifier. */
    @Nullable
    private CompetitorResponse competitor;
}
