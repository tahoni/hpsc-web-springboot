package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request to create or fully replace a shooter log.
 *
 * @see ShooterLogPatchRequest
 * @since 16.1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ShooterLogRequest {
    /** Date the shooter log starts on. */
    @NotNull(message = "Shooter log start date is required.")
    @JsonProperty(required = true)
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate startDate;
    /** Date the shooter log ends on; not before the start date. */
    @NotNull(message = "Shooter log end date is required.")
    @JsonProperty(required = true)
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate endDate;
    /**
     * The identifiers of the matches the shooter log covers. May be null or empty, for a log that covers no matches
     * yet. A replacement request's list replaces the log's matches in full.
     */
    private List<Long> matchIds = new ArrayList<>();

    /**
     * Constructs a {@code ShooterLogRequest} from its JSON representation.
     *
     * @param startDate the date the shooter log starts on. Must not be null.
     * @param endDate   the date the shooter log ends on. Must not be null or before {@code startDate}.
     * @param matchIds  the identifiers of the matches the shooter log covers; may be null or empty.
     */
    @JsonCreator
    public ShooterLogRequest(@JsonProperty(value = "startDate", required = true) LocalDate startDate,
                             @JsonProperty(value = "endDate", required = true) LocalDate endDate,
                             @JsonProperty("matchIds") List<Long> matchIds) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.matchIds = (matchIds == null) ? new ArrayList<>() : matchIds;
    }

    /**
     * Checks that the request carries what is needed to create a shooter log before any of it is used. Nothing is
     * looked up, so a match identifier that is present but unknown is not caught here.
     *
     * @throws ValidationException if the start date or end date is missing, the end date is before the start date, or
     *                             a match identifier is null.
     */
    public void validate() {
        if (getStartDate() == null) {
            throw new ValidationException("Shooter log start date is required.");
        }
        if (getEndDate() == null) {
            throw new ValidationException("Shooter log end date is required.");
        }
        if (getEndDate().isBefore(getStartDate())) {
            throw new ValidationException("Shooter log end date cannot be before its start date.");
        }
        if ((matchIds != null) && matchIds.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Shooter log match IDs must not be null.");
        }
    }
}
