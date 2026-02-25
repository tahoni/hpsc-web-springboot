package za.co.hpsc.web.models.ipsc.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates information about a match, its associated club, match stages, and competitors.
 *
 * <p>
 * The {@code MatchEntityHolder} class acts as a container, aggregating
 * various entities related to a match. This includes the match details,
 * associated club, stages of the match, competitors, and their participation
 * in the match stages.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntityHolder {
    IpscMatch match;
    Club club;
    List<IpscMatchStage> matchStages = new ArrayList<>();
    List<Competitor> competitors = new ArrayList<>();
    List<MatchCompetitor> matchCompetitors = new ArrayList<>();
    List<MatchStageCompetitor> matchStageCompetitors = new ArrayList<>();
}
