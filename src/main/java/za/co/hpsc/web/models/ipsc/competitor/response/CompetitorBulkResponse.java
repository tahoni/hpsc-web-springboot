package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of importing a single competitor in a bulk import: which row it was, whether it was imported, a message
 * describing the outcome, and the {@link CompetitorResponse} it relates to.
 *
 * @see CompetitorResponseHolder
 * @see CompetitorResponse
 * @since 16.4.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorBulkResponse {
    /** The row's position among the CSV's data rows, starting at 1 for the first row after the header. */
    private int rowNumber;
    /** Whether the row was imported, either as a new competitor or as an update to an existing one. */
    private boolean success = true;
    /**
     * A message describing the outcome: empty for a row that created a competitor, a note for a row that updated and
     * verified an existing one, and the reason for a row that was skipped.
     */
    private String message = "";
    /** The created or updated competitor; {@code null} for a row that was skipped. */
    @Nullable
    private CompetitorResponse competitor;
}
