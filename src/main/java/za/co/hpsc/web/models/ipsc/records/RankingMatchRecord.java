package za.co.hpsc.web.models.ipsc.records;

public record RankingMatchRecord(
        String clubName,
        String matchName,

        String powerFactor,

        CompetitorRankingMatchRecord ranking
) {
}
