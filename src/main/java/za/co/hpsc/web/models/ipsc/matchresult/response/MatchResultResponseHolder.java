package za.co.hpsc.web.models.ipsc.matchresult.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class linking one match with the {@link MatchResultResponse}s of all the competitors who shot it.
 *
 * @see za.co.hpsc.web.controllers.IpscMatchResultController
 * @since 18.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultResponseHolder {
    /** The match the results were shot in. */
    @NotNull
    private MatchResultMatchResponse match;
    /** The results of the competitors who shot the match; empty if nobody has a result in it. */
    @NotNull
    private List<MatchResultResponse> matchResults = new ArrayList<>();
}
