package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;

import java.util.Optional;

// TODO: Javadoc
public interface TransactionService {
    /**
     *
     * @param matchResults
     * @return
     */
    Optional<IpscMatch> saveMatchResults(MatchResultsDto matchResults);
}
