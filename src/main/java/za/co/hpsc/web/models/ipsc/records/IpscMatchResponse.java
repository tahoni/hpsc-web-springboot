package za.co.hpsc.web.models.matchresults.response;

import java.time.LocalDate;
import java.util.List;

public record IpscMatchResponse(
        String name,
        LocalDate scheduledDate,
        String clubName,

        String matchFirearmType,
        String matchCategory,

        List<IpscMatchStageResponse> matchStages,
        List<MatchCompetitorResponse> matchCompetitors,

        String dateEdited
) {
}
