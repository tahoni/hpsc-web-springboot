package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.TransactionService;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final TransactionTemplate transactionTemplate;

    public TransactionServiceImpl(CompetitorRepository competitorRepository, IpscMatchRepository ipscMatchRepository,
                                  MatchCompetitorRepository matchCompetitorRepository,
                                  PlatformTransactionManager transactionManager) {
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Competitor saveCompetitor(Competitor competitor) {
        return transactionTemplate.execute(status -> loadAssociations(competitorRepository.save(competitor)));
    }

    @Override
    public List<Competitor> saveCompetitors(List<Competitor> competitors) {
        return transactionTemplate.execute(status -> competitors.stream()
                .map(competitor -> loadAssociations(competitorRepository.save(competitor)))
                .toList());
    }

    @Override
    public void deleteCompetitor(Competitor competitor) {
        // Flushed inside the transaction rather than at commit, so a foreign-key violation
        // surfaces from this call as a DataIntegrityViolationException the caller can handle.
        transactionTemplate.executeWithoutResult(status -> {
            competitorRepository.delete(competitor);
            competitorRepository.flush();
        });
    }

    @Override
    public IpscMatch saveMatch(IpscMatch match) {
        return transactionTemplate.execute(status -> loadAssociations(ipscMatchRepository.save(match)));
    }

    @Override
    public List<IpscMatch> saveMatches(List<IpscMatch> matches) {
        return transactionTemplate.execute(status -> matches.stream()
                .map(match -> loadAssociations(ipscMatchRepository.save(match)))
                .toList());
    }

    @Override
    public void deleteMatch(IpscMatch match) {
        // Flushed inside the transaction rather than at commit, so a foreign-key violation
        // surfaces from this call as a DataIntegrityViolationException the caller can handle.
        transactionTemplate.executeWithoutResult(status -> {
            ipscMatchRepository.delete(match);
            ipscMatchRepository.flush();
        });
    }

    @Override
    public MatchCompetitor saveMatchCompetitor(MatchCompetitor matchCompetitor) {
        return transactionTemplate.execute(status -> matchCompetitorRepository.save(matchCompetitor));
    }

    @Override
    public void deleteMatchCompetitor(MatchCompetitor matchCompetitor) {
        // Flushed inside the transaction rather than at commit, so a foreign-key violation
        // surfaces from this call as a DataIntegrityViolationException the caller can handle.
        transactionTemplate.executeWithoutResult(status -> {
            matchCompetitorRepository.delete(matchCompetitor);
            matchCompetitorRepository.flush();
        });
    }

    /**
     * Loads the associations a competitor's response mapping reads, while the transaction is
     * still open.
     *
     * @param competitor the competitor whose associations to load; must not be null.
     * @return {@code competitor}, for chaining.
     */
    protected Competitor loadAssociations(@NotNull Competitor competitor) {
        Hibernate.initialize(competitor.getHomeClub());
        Hibernate.initialize(competitor.getEmailAddresses());
        return competitor;
    }

    /**
     * Loads the associations a match's response mapping reads, while the transaction is still
     * open.
     *
     * @param match the match whose associations to load; must not be null.
     * @return {@code match}, for chaining.
     */
    protected IpscMatch loadAssociations(@NotNull IpscMatch match) {
        Hibernate.initialize(match.getClub());
        return match;
    }
}
