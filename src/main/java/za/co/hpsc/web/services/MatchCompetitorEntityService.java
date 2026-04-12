package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.List;

/**
 * The {@code MatchCompetitorEntityService} interface defines the contract related to
 * relationships between competitors and matches in the system.
 * It provides methods for searching and retrieving details about the match competitors
 * based on specific criteria.
 */
public interface MatchCompetitorEntityService {
    // TODO: Javadoc
    List<MatchCompetitor> findMatchCompetitors(Long matchId, Long competitorId);
}
