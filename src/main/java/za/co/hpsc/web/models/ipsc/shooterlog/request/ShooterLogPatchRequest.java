package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Request to partially update an existing shooter log.
 *
 * <p>
 * The shooter log to update is identified by the ID in the request path, so no field here is required: any field left
 * {@code null} is left unchanged on the log. Unlike {@link ShooterLogRequest}, which creates or replaces a shooter log
 * in full, a non-null {@code matchIds} here still replaces the log's matches in full, and an empty list clears them.
 * </p>
 *
 * @see ShooterLogRequest
 * @since 16.1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ShooterLogPatchRequest {
    /** Date the shooter log starts on; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate startDate;
    /** Date the shooter log ends on; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate endDate;
    /** The identifiers of the matches the shooter log covers, replacing its current matches; may be null. */
    private List<Long> matchIds;
}
