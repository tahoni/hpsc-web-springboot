package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

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
    /** The imported match competitor; {@code null} for a row that failed, whose values are in {@link #matchCompetitorRow}. */
    @Nullable
    private MatchCompetitorResponse matchCompetitor;
    /**
     * Every value the row supplied, as text with a missing value as an empty string; set only for a row that failed,
     * and {@code null} for one that was imported.
     */
    @Nullable
    private MatchCompetitorRow matchCompetitorRow;
}
