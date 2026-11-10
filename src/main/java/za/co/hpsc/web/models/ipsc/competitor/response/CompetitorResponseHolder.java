package za.co.hpsc.web.models.ipsc.competitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class holding the {@link CompetitorResponse}s created or updated by a bulk CSV import, and the
 * {@link CompetitorBulkResponse} of each row.
 *
 * @see za.co.hpsc.web.controllers.IpscCompetitorController
 * @since 8.1.0
 */
@Getter
@Setter
public class CompetitorResponseHolder {
    /** The list of competitors created or updated by the bulk import. */
    @NotNull
    private List<CompetitorResponse> competitors;
    /** The result of each row of the import, in the same order as the CSV. */
    @NotNull
    private List<CompetitorBulkResponse> results;

    /**
     * Creates a holder without row results.
     *
     * @param competitors the competitors created or updated.
     */
    public CompetitorResponseHolder(List<CompetitorResponse> competitors) {
        this(competitors, new ArrayList<>());
    }

    /**
     * Creates a holder.
     *
     * @param competitors the competitors created or updated.
     * @param results     the result of each row, in CSV order.
     */
    public CompetitorResponseHolder(List<CompetitorResponse> competitors, List<CompetitorBulkResponse> results) {
        this.competitors = competitors;
        this.results = results;
    }
}
