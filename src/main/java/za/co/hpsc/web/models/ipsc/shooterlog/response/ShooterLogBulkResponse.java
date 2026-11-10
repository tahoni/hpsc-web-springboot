package za.co.hpsc.web.models.ipsc.shooterlog.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of importing a single shooter log in a bulk import: whether it succeeded, a message describing the
 * outcome, and the {@link ShooterLogResponse} it relates to.
 *
 * @see ShooterLogBulkResponseHolder
 * @see ShooterLogResponse
 * @since 16.3.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterLogBulkResponse {
    /** Whether the shooter log was imported successfully. */
    private boolean success = true;
    /** A message describing the outcome of the import: empty for a row that was imported, the reason otherwise. */
    private String message = "";
    /** The imported shooter log; {@code null} for a row that failed, whose values are in {@link #shooterLogRow}. */
    @Nullable
    private ShooterLogResponse shooterLog;
    /**
     * Every value the row supplied, as text with a missing value as an empty string; set only for a row that failed,
     * and {@code null} for one that was imported.
     */
    @Nullable
    private ShooterLogRow shooterLogRow;
}
