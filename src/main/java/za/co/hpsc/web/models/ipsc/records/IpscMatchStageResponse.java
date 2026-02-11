package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record IpscMatchStageResponse(
        Integer stageNumber,
        String stageName,
        Integer rangeNumber,

        Integer targetPaper,
        Integer targetPopper,
        Integer targetPlates,
        Integer targetDisappear,
        Integer targetPenalty,

        Integer minRounds,
        Integer maxPoints,

        List<MatchStageCompetitorResponse> matchStageCompetitors
) {
}
