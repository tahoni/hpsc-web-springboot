package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record CompetitorRecord(
        String firstName,
        String lastName,
        String middleNames,
        String dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,

        MatchCompetitorRecord overall,
        List<MatchStageCompetitorRecord> stages
) {
}
