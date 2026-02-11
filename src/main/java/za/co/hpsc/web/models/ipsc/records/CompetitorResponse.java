package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record CompetitorResponse(
        String firstName,
        String lastName,
        String middleNames,
        String dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,

        MatchCompetitorResponse overall,
        List<MatchStageCompetitorResponse> stages
) {
}
