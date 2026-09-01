package za.co.hpsc.web.models.ipsc.competitor.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link CompetitorResponse}s created by a bulk CSV import.
 *
 * @see za.co.hpsc.web.controllers.IpscCompetitorController
 * @since 8.1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class CompetitorResponseHolder {
    /** The list of competitors created by the bulk import. */
    @NotNull
    private List<CompetitorResponse> competitors;
}
