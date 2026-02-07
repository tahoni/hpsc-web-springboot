package za.co.hpsc.web.services;

import za.co.hpsc.web.models.match.MatchResultsDto;

// TODO: Javadoc (not yet ready)
public interface TransactionService {
    void saveMatchResults(MatchResultsDto matchResults);

    void saveMatchLogs();
}
