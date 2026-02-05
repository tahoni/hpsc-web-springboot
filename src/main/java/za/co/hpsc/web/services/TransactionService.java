package za.co.hpsc.web.services;

import za.co.hpsc.web.models.match.MatchResultsDto;

// TODO: Javadoc
public interface TransactionService {
    void saveMatchResults(MatchResultsDto matchResults);

    void saveMatchLogs();
}
