package za.co.hpsc.web.models.ipsc.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.*;

import java.util.List;

// TODO: Javadoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntityHolder {
    IpscMatch match;
    Club club;
    List<IpscMatchStage> matchStages;
    List<Competitor> competitors;
    List<MatchCompetitor> matchCompetitors;
    List<MatchStageCompetitor> matchStageCompetitors;
}
