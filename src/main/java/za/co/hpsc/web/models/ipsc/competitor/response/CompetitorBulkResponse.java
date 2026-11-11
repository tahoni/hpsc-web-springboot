package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of importing a single competitor in a bulk import: which row it was, whether it succeeded, a message
 * describing the outcome, and the {@link CompetitorResponse} it was created or updated as, or the
 * {@link CompetitorRow} of the values it supplied if it failed.
 *
 * @see CompetitorResponseHolder
 * @see CompetitorResponse
 * @see CompetitorRow
 * @since 16.4.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorBulkResponse {
    /** The row's position among the CSV's data rows, starting at 1 for the first row after the header. */
    private int rowNumber;
    /** Whether the row was imported; {@code false} for a row that failed, whose reason is in {@link #message}. */
    private boolean success = true;
    /**
     * A message describing the outcome: empty for a row that created a competitor, a note for a row that updated
     * and verified an existing one, and the reason for a row that failed.
     */
    private String message = "";
    /** The created or updated competitor; {@code null} for a row that failed, whose values are in {@link #competitorRow}. */
    @Nullable
    private CompetitorResponse competitor;
    /**
     * Every value the row supplied, as text with a missing value as an empty string; set only for a row that failed,
     * and {@code null} for one that was imported.
     */
    @Nullable
    private CompetitorRow competitorRow;
}
