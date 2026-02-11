package za.co.hpsc.web.models.ipsc.records;

public record MatchCompetitorResponse(
        String club,
        String firearmType,
        String division,
        String powerFactor,

        String matchPoints,
        String matchRanking,

        String competitorCategory,

        String dateEdited
) {
}
