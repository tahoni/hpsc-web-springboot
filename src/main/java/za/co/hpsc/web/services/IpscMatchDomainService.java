package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;

import java.util.Optional;

// TODO: Javadoc
public interface IpscMatchDomainService {
    /**
     *
     * @param matchResults
     * @return
     */
    Optional<MatchEntityHolder> initMatchEntities(MatchResultsDto matchResults);
}
