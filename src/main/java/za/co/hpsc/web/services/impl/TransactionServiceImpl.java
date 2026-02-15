package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.IpscDomainService;
import za.co.hpsc.web.services.TransactionService;

import java.util.Optional;

// TODO: create tests
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final IpscDomainService ipscDomainService;

    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  IpscDomainService ipscDomainService,
                                  ClubRepository clubRepository,
                                  CompetitorRepository competitorRepository,
                                  IpscMatchRepository ipscMatchRepository,
                                  IpscMatchStageRepository ipscMatchStageRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.transactionManager = transactionManager;
        this.ipscDomainService = ipscDomainService;
        this.clubRepository = clubRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<IpscMatch> saveMatchResults(MatchResultsDto matchResults)
            throws FatalException {

        if ((matchResults == null) || (matchResults.getMatch() == null)) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            Optional<MatchEntityHolder> optionalMatch = ipscDomainService.initMatchEntities(matchResults);
            optionalMatch.ifPresent(matchEntityHolder -> {
                if (matchEntityHolder.getClub() != null) {
                    clubRepository.save(matchEntityHolder.getClub());
                }
                competitorRepository.saveAll(matchEntityHolder.getCompetitors());

                ipscMatchRepository.save(matchEntityHolder.getMatch());
                ipscMatchStageRepository.saveAll(matchEntityHolder.getMatchStages());

                matchCompetitorRepository.saveAll(matchEntityHolder.getMatchCompetitors());
                matchStageCompetitorRepository.saveAll(matchEntityHolder.getMatchStageCompetitors());
            });

            transactionManager.commit(transaction);
            return optionalMatch.map(MatchEntityHolder::getMatch);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            log.error(e.getMessage(), e);
            throw new FatalException("Unable to save the match: " + e.getMessage(), e);
        }
    }
}
