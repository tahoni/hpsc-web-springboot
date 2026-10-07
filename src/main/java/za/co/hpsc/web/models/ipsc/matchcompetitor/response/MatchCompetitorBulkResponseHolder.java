package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A container class holding the {@link MatchCompetitorBulkResponse}s of a bulk match competitor import,
 * one per imported row, each recording whether that row succeeded.
 *
 * @see MatchCompetitorBulkResponse
 * @since 11.0.0
 */
@Getter
@Setter
@AllArgsConstructor
public class MatchCompetitorBulkResponseHolder {
    /** The result of each imported match competitor, in the same order as the import. */
    @NonNull
    private List<MatchCompetitorBulkResponse> matchCompetitors;
}
