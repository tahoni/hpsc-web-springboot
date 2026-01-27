package za.co.hpsc.web.models.matches;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchLogResponse extends MatchResponse {
    /**
     * Constructs a new {@code MatchLogResponse} object with default values.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * The superclass constructors also ensure that the list of placings is not null
     * by initialising it to an empty list.
     * </p>
     */
    public MatchLogResponse() {
        super();
    }
}
