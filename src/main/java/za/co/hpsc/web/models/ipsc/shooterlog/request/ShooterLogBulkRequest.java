package za.co.hpsc.web.models.ipsc.shooterlog.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * One row of a bulk shooter log update: the {@code shooterLogId} of the log to change, and the same dates and matches
 * as a {@link ShooterLogRequest}.
 *
 * <p>
 * Read from CSV only, through {@link ShooterLogBulkRequestCsvMixIn}. A bulk replacement uses the row as a full
 * {@link ShooterLogRequest}, so both dates are required. A bulk partial update treats a date left empty, and a
 * {@code matchIds} left empty, as unchanged.
 * </p>
 *
 * @see ShooterLogPatchRequest
 * @since 16.4.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ShooterLogBulkRequest extends ShooterLogRequest {
    /** The identifier of the shooter log the row changes; required, but checked per row rather than failing the file. */
    private Long shooterLogId;

    /**
     * Constructs a row.
     *
     * @param shooterLogId the identifier of the shooter log to change; may be null, which the import reports for the row.
     * @param startDate    the date the shooter log starts on; may be null.
     * @param endDate      the date the shooter log ends on; may be null.
     * @param matchIds     the identifiers of the matches the shooter log covers; may be null or empty.
     */
    public ShooterLogBulkRequest(Long shooterLogId, LocalDate startDate, LocalDate endDate, List<Long> matchIds) {
        super(startDate, endDate, matchIds);
        this.shooterLogId = shooterLogId;
    }
}
