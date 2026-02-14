package za.co.hpsc.web.models.ipsc.records;

public record CompetitorRankingMatchRecord(
        String matchName,
        String clubName,

        String firstName,
        String lastName,
        String middleNames,
        String dateOfBirth,

        Integer sapsaNumber,
        String competitorNumber,

        String competitorCategory,

        String points,
        String percentage
) {
}
