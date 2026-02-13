package za.co.hpsc.web.models.ipsc.records;

public record MatchCompetitorRecord(
        String club,
        String firearmType,
        String division,
        String powerFactor,

        String competitorCategory,

        String matchPoints,
        String matchRanking,

        String dateEdited
) {
}
