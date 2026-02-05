package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.Optional;

// TODO: Javadoc
public interface MatchCompetitorService {
    Optional<MatchCompetitor> findMatchCompetitor(Long competitorId, Long matchId);
}
