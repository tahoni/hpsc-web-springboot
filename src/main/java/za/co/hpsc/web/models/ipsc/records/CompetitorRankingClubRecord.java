package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record CompetitorRankingClubRecord(
        String clubName,

        String firstName,
        String lastName,
        String middleNames,
        String dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,

        String competitorCategory,

        String points,
        String percentage,

        List<CompetitorRankingMatchRecord> matches
) {
}
