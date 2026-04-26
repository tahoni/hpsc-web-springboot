package za.co.hpsc.web.models.ipsc.holders.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchHolder {
    private IpscMatch match;
    private Club club;
    private List<IpscMatchStage> matchStages = new ArrayList<>();
    private List<Competitor> competitors = new ArrayList<>();
    private List<MatchCompetitor> matchCompetitors = new ArrayList<>();
    private List<MatchStageCompetitor> matchStageCompetitors = new ArrayList<>();
}
