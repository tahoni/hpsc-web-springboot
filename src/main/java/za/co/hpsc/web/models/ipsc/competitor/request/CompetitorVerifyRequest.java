package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.exceptions.ValidationException;

import java.util.List;
import java.util.Objects;

/**
 * Request to mark several competitors as verified in one call.
 *
 * @since 16.2.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CompetitorVerifyRequest {
    /**
     * The identifiers of the competitors to verify. A repeated identifier is verified once, and an identifier that
     * matches no competitor is reported rather than rejecting the request.
     */
    @NotEmpty(message = "At least one competitor ID is required.")
    @JsonProperty(required = true)
    private List<Long> competitorIds;

    /**
     * Constructs a {@code CompetitorVerifyRequest} from its JSON representation.
     *
     * @param competitorIds the identifiers of the competitors to verify. Must not be null, empty or contain a null.
     */
    @JsonCreator
    public CompetitorVerifyRequest(@JsonProperty(value = "competitorIds", required = true) List<Long> competitorIds) {
        this.competitorIds = competitorIds;
    }

    /**
     * Checks that the request names at least one competitor, before any of it is used.
     *
     * @throws ValidationException if no competitor IDs are given, or one of them is null.
     */
    public void validate() {
        if ((competitorIds == null) || competitorIds.isEmpty()) {
            throw new ValidationException("At least one competitor ID is required.");
        }
        if (competitorIds.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Competitor IDs must not be null.");
        }
    }
}
