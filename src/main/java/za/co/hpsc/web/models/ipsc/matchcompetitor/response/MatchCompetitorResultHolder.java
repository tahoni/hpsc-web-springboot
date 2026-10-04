package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link MatchCompetitorResult}s of a bulk match competitor import,
 * one per imported row, each recording whether that row succeeded.
 *
 * @see MatchCompetitorResult
 * @see MatchCompetitorResponseHolder
 * @since 11.0.0
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchCompetitorResultHolder {
    /** The result of each imported match competitor, in the same order as the import. */
    @NotNull
    private List<MatchCompetitorResult> matchCompetitorResults;
}
