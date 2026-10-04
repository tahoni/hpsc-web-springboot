package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The outcome of importing a single match competitor in a bulk import: whether it succeeded, a
 * message describing the outcome, and the {@link MatchCompetitorResponse} it relates to.
 *
 * @see MatchCompetitorResultHolder
 * @see MatchCompetitorResponse
 * @since 10.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCompetitorResult {
    /** Whether the match competitor was imported successfully. */
    private boolean success;
    /** A message describing the outcome of the import. */
    private String message;
    /** The match competitor this result relates to. */
    @NotNull
    private MatchCompetitorResponse matchCompetitor;
}
