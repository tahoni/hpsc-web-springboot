package za.co.hpsc.web.models.matches;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.Response;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class MatchResultLogResponse extends Response {
    @NotNull
    private MatchResultResponse results;
    @NotNull
    private MatchLogResponseHolder logs;

    /**
     * Constructs a new {@code MatchResultLogResponse} object with default values.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     */
    public MatchResultLogResponse() {
        super();
        this.results = new MatchResultResponse();
        this.logs = new MatchLogResponseHolder(new HashMap<>());
    }
}
