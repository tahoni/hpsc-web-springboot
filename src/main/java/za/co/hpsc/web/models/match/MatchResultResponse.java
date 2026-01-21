package za.co.hpsc.web.models.match;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MatchResultResponse extends MatchResponse {
    private BigDecimal score;

    /**
     * Constructs a new {@code MatchResultResponse} object with default values.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * The superclass constructors also ensures that the list of placings is not null
     * by initialising it to an empty list.
     * </p>
     */
    public MatchResultResponse() {
    }
}
