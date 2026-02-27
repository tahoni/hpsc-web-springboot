package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.TransactionService;

import java.util.Optional;

// TODO: finish this
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final DomainService domainService;

    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  DomainService domainService,
                                  ClubRepository clubRepository,
                                  CompetitorRepository competitorRepository,
                                  IpscMatchRepository ipscMatchRepository,
                                  IpscMatchStageRepository ipscMatchStageRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  MatchStageCompetitorRepository matchStageCompetitorRepository) {

        this.transactionManager = transactionManager;
        this.domainService = domainService;
        this.clubRepository = clubRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<IpscMatch> saveMatchResults(DtoToEntityMapping dtoToEntityMapping)
            throws FatalException {

        if ((dtoToEntityMapping == null) || (dtoToEntityMapping.getMatch() == null)) {
            return Optional.empty();
        }

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            if (dtoToEntityMapping.getClub() != null) {
                Club club = null;
                if (dtoToEntityMapping.getClub().getId() != null) {
                    club = clubRepository.findById(dtoToEntityMapping.getClub().getId()).orElseGet(Club::new);
                } else {
                    return Optional.empty();
                }
                club.init(dtoToEntityMapping.getClub());

                clubRepository.save(club);
            }

            IpscMatch matchEntity = null;
            if (dtoToEntityMapping.getMatch().getId() != null) {
                matchEntity = ipscMatchRepository.findById(dtoToEntityMapping.getMatch().getId()).orElseGet(IpscMatch::new);
            } else {
                matchEntity = new IpscMatch();
            }
            matchEntity.init(dtoToEntityMapping.getMatch());
            ipscMatchRepository.save(matchEntity);

            transactionManager.commit(transaction);

            return Optional.of(matchEntity);

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            log.error(e.getMessage(), e);
            throw new FatalException("Unable to save the match: " + e.getMessage(), e);
        }
    }
}
