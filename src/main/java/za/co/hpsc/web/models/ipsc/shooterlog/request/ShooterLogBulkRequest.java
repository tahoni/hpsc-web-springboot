package za.co.hpsc.web.models.ipsc.shooterlog.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * One row of a bulk shooter log update: the {@code shooterLogId} of the log to change, and the same dates and matches
 * as a {@link ShooterLogRequest}.
 *
 * <p>
 * Read from CSV only, through {@link ShooterLogBulkRequestCsvMixIn}. A bulk replacement uses the row as a full
 * {@link ShooterLogRequest}, so both dates are required, and ignores {@code addMatchIds} and
 * {@code removeMatchIds}. A bulk partial update treats a date left empty, and a {@code matchIds} left empty, as
 * unchanged, and adds and removes the matches in {@code addMatchIds} and {@code removeMatchIds}.
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
    /** The identifiers of matches a bulk partial update adds to the log; ignored by a bulk replacement. */
    private List<Long> addMatchIds = new ArrayList<>();
    /** The identifiers of matches a bulk partial update removes from the log; ignored by a bulk replacement. */
    private List<Long> removeMatchIds = new ArrayList<>();

    /**
     * Constructs a row.
     *
     * @param shooterLogId the identifier of the shooter log to change; may be null, which the import reports for the row.
     * @param startDate    the date the shooter log starts on; may be null.
     * @param endDate      the date the shooter log ends on; may be null.
     * @param matchIds     the identifiers of the matches the shooter log covers; may be null or empty.
     * @param addMatchIds  the identifiers of the matches to add to the log; may be null or empty.
     * @param removeMatchIds the identifiers of the matches to remove from the log; may be null or empty.
     */
    public ShooterLogBulkRequest(Long shooterLogId, LocalDate startDate, LocalDate endDate, List<Long> matchIds,
                                 List<Long> addMatchIds, List<Long> removeMatchIds) {
        super(startDate, endDate, matchIds);
        this.shooterLogId = shooterLogId;
        this.addMatchIds = (addMatchIds == null) ? new ArrayList<>() : addMatchIds;
        this.removeMatchIds = (removeMatchIds == null) ? new ArrayList<>() : removeMatchIds;
    }
}
