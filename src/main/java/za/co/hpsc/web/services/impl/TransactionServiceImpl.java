package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.repositories.MatchStageRepository;
import za.co.hpsc.web.services.TransactionService;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final MatchRepository matchRepository;
    protected final ClubRepository clubRepository;
    protected final MatchStageRepository matchStageRepository;
    protected final CompetitorRepository competitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  MatchRepository matchRepository, ClubRepository clubRepository,
                                  MatchStageRepository matchStageRepository,
                                  CompetitorRepository competitorRepository) {

        this.transactionManager = transactionManager;

        this.matchRepository = matchRepository;
        this.clubRepository = clubRepository;
        this.matchStageRepository = matchStageRepository;
        this.competitorRepository = competitorRepository;
    }

    @Override
    public void saveMatchResults(IpscResponse ipscResponse) {
        if (ipscResponse == null) {
            log.error("IPSC response is null.");
            throw new ValidationException("IPSC response can not be null.");
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void saveMatchLogs() {
        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        try {
            transactionManager.commit(transaction);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

}
