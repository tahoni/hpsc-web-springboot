package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The outcome of importing a single competitor in a bulk import: which row it was, a message describing the outcome,
 * and the {@link CompetitorResponse} it was created or updated as.
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
    /**
     * A message describing the outcome: empty for a row that created a competitor, and a note for a row that updated
     * and verified an existing one.
     */
    private String message = "";
    /** The created or updated competitor. */
    private CompetitorResponse competitor;
}
