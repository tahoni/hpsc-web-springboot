package za.co.hpsc.web.models.ipsc.match.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class holding the {@link MatchResponse}s created by a bulk CSV import, and the
 * {@link MatchBulkResponse} of each row.
 *
 * @see za.co.hpsc.web.controllers.IpscMatchController
 * @since 8.3.0
 */
@Getter
@Setter
public class MatchBulkResponseHolder {
    /** The list of matches created by the bulk import. */
    @NotNull
    private List<MatchResponse> matches;
    /** The result of each row of the import, in the same order as the CSV. */
    @NotNull
    private List<MatchBulkResponse> results;

    /**
     * Creates a holder without row results.
     *
     * @param matches the matches created.
     */
    public MatchBulkResponseHolder(List<MatchResponse> matches) {
        this(matches, new ArrayList<>());
    }

    /**
     * Creates a holder.
     *
     * @param matches the matches created.
     * @param results the result of each row, in CSV order.
     */
    public MatchBulkResponseHolder(List<MatchResponse> matches, List<MatchBulkResponse> results) {
        this.matches = matches;
        this.results = results;
    }
}
