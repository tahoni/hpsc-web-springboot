package za.co.hpsc.web.models.ipsc.match.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of importing a single match in a bulk import: which row it was, whether it succeeded, a message
 * describing the outcome, and the {@link MatchResponse} it was created as, or the {@link MatchRow} of the values it
 * supplied if it failed.
 *
 * @see MatchBulkResponseHolder
 * @see MatchResponse
 * @see MatchRow
 * @since 17.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchBulkResponse {
    /** The row's position among the CSV's data rows, starting at 1 for the first row after the header. */
    private int rowNumber;
    /** Whether the row was imported; {@code false} for a row that failed, whose reason is in {@link #message}. */
    private boolean success = true;
    /** A message describing the outcome: empty for a row that created a match, and the reason for a row that failed. */
    private String message = "";
    /** The created match; {@code null} for a row that failed, whose values are in {@link #matchRow}. */
    @Nullable
    private MatchResponse match;
    /**
     * Every value the row supplied, as text with a missing value as an empty string; set only for a row that failed,
     * and {@code null} for one that was imported.
     */
    @Nullable
    private MatchRow matchRow;
}
