package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;

import java.util.Optional;

// TODO: Javadoc
public interface CompetitorService {
    Optional<Competitor> findCompetitor(MemberResponse memberResponse);
}
