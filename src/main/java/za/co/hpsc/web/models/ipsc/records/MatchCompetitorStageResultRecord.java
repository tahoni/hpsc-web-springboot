package za.co.hpsc.web.models.ipsc.records;

public record MatchCompetitorStageResultRecord(
        String stageName,

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

        String dateEdited
) {
}
