package za.co.hpsc.web.models.ipsc.records;

import java.util.List;

public record CompetitorResultRecord(
        String firearmType,
        String division,
        String powerFactor,

        MatchCompetitorOverallResultsRecord overall,
        List<MatchCompetitorStageResultRecord> stages
) {
}
