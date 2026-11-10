package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Request to partially update an existing shooter log.
 *
 * <p>
 * The shooter log to update is identified by the ID in the request path, so no field here is required: any field left
 * {@code null} is left unchanged on the log. Unlike {@link ShooterLogRequest}, which creates or replaces a shooter log
 * in full, a non-null {@code matchIds} here still replaces the log's matches in full, and an empty list clears them.
 * </p>
 *
 * <p>
 * {@code addMatchIds} and {@code removeMatchIds} change the matches without restating them: matches the log already
 * covers are left in place, the ones in {@code addMatchIds} are added, and the ones in {@code removeMatchIds} are
 * dropped. When {@code matchIds} is given as well it is applied first, then the additions, then the removals.
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
    /**
     * The identifiers of matches to add to the shooter log's current matches, ones it already covers being left as they
     * are; may be null or empty.
     */
    private List<Long> addMatchIds;
    /**
     * The identifiers of matches to remove from the shooter log's current matches, ones it does not cover being
     * ignored; may be null or empty.
     */
    private List<Long> removeMatchIds;

    /**
     * Checks the match identifiers to add and remove before any of it is used. Nothing is looked up, so an identifier
     * that is present but unknown is not caught here.
     *
     * @throws ValidationException if an identifier in {@code addMatchIds} or {@code removeMatchIds} is null, or one is
     *                             in both, as the request would then both add and remove the same match.
     */
    public void validate() {
        if (hasNull(addMatchIds) || hasNull(removeMatchIds)) {
            throw new ValidationException("Shooter log match IDs to add or remove must not be null.");
        }
        if ((addMatchIds != null) && (removeMatchIds != null)) {
            HashSet<Long> overlap = new HashSet<>(addMatchIds);
            overlap.retainAll(removeMatchIds);
            if (!overlap.isEmpty()) {
                throw new ValidationException(
                        "Shooter log match IDs cannot be both added and removed: " + overlap + ".");
            }
        }
    }

    private static boolean hasNull(List<Long> ids) {
        return (ids != null) && ids.stream().anyMatch(Objects::isNull);
    }
}
