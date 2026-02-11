package za.co.hpsc.web.models.ipsc.records;

public record MatchStageCompetitorResponse(
        String firearmType,
        String division,
        String powerFactor,

        Integer scoreA,
        Integer scoreB,
        Integer scoreC,
        Integer scoreD,

        Integer points,
        Integer misses,
        Integer penalties,
        Integer procedurals,

        String time,
        String hitFactor,

        String stagePoints,
        String stagePercentage,
        String stageRanking,

        String competitorCategory,

        String dateEdited
) {
}
