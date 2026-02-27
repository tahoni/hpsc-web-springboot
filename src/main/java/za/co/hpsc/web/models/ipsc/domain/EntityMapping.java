package za.co.hpsc.web.models.ipsc.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityMapping {
    private Club club;
    private IpscMatch match;

    private Map<UUID, Competitor> competitorMap = new HashMap<>();
    private Map<UUID, IpscMatchStage> matchStageMap = new HashMap<>();

    private Map<UUID, MatchCompetitor> matchCompetitorMap = new HashMap<>();
    private Map<UUID, MatchStageCompetitor> matchStageCompetitorMap = new HashMap<>();
}
