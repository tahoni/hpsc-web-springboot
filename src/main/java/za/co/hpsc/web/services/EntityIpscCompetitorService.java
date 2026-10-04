package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;

import java.util.Optional;

public interface EntityIpscCompetitorService {
    Optional<Competitor> findCompetitor(String fullName, int competitorNumber);
}
