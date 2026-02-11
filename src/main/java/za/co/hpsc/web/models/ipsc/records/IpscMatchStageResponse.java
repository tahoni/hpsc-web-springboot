package za.co.hpsc.web.models.matchresults.response;

import za.co.hpsc.web.domain.MatchStageCompetitor;

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

        List<MatchStageCompetitor> matchStageCompetitors
) {
}
