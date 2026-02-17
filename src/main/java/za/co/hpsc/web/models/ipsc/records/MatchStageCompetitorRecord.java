package za.co.hpsc.web.models.ipsc.records;

public record MatchStageCompetitorRecord(
        String firearmType,
        String division,
        String powerFactor,

        String competitorCategory,

        Integer scoreA,
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

        String dateEdited
) {
}
