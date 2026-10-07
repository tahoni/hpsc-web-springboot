package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

/**
 * The outcome of importing a single match competitor in a bulk import: whether it succeeded, a
 * message describing the outcome, and the {@link MatchCompetitorResponse} it relates to.
 *
 * @see MatchCompetitorBulkResponseHolder
 * @see MatchCompetitorResponse
 * @since 11.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCompetitorBulkResponse {
    /** Whether the match competitor was imported successfully. */
    private boolean success = true;
    /** A message describing the outcome of the import. */
    private String message = "";
    /** The match competitor this result relates to. */
    @NonNull
    private MatchCompetitorResponse matchCompetitor;
}
