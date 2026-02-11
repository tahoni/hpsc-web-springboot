package za.co.hpsc.web.models.matchresults.response;

import java.time.LocalDate;

public record CompetitorResponse(
        String firstName,
        String lastName,
        String middleNames,
        LocalDate dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,
        String defaultCompetitorCategory
) {
}
