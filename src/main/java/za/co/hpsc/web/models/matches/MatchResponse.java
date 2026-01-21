package za.co.hpsc.web.models.matches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.Response;
import za.co.hpsc.web.models.shared.Placing;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MatchResponse extends Response {
    @NotBlank
    private String date;
    private String club;

    @NotNull
    private List<Placing> placings;

    /**
     * Constructs a new {@code MatchResponse} object with default values.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * Ensures that the list of placings is not null by initialising it to an empty list.
     * </p>
     */
    public MatchResponse() {
        super();
        this.date = null;
        this.club = null;
        this.placings = new ArrayList<>();
    }
}
