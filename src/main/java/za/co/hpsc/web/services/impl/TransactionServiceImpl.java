package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.services.TransactionService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final IpscMatchStageRepository ipscMatchStageRepository;
    private final TransactionTemplate transactionTemplate;

    public TransactionServiceImpl(CompetitorRepository competitorRepository, IpscMatchRepository ipscMatchRepository,
                                  IpscMatchStageRepository ipscMatchStageRepository,
                                  PlatformTransactionManager transactionManager) {
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
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
    public IpscMatch saveMatch(IpscMatch match, List<IpscMatchStage> stages, StageSaveMode stageSaveMode) {
        return transactionTemplate.execute(status -> {
            IpscMatch saved = ipscMatchRepository.save(match);
            if (stageSaveMode == StageSaveMode.REPLACE) {
                replaceStages(saved, stages);
            } else if (stages != null) {
                upsertStages(saved, stages);
            }
            return loadAssociations(saved);
        });
    }

    @Override
    public List<IpscMatch> saveMatches(List<IpscMatch> matches) {
        return transactionTemplate.execute(status -> matches.stream()
                .map(match -> loadAssociations(ipscMatchRepository.save(match)))
                .toList());
    }

    @Override
    public void deleteMatch(IpscMatch match) {
        // The match's stages are removed by IpscMatch.stages' cascade, which deletes them before
        // their match within the same flush. Flushed inside the transaction rather than at
        // commit, so a foreign-key violation surfaces from this call as a
        // DataIntegrityViolationException the caller can handle.
        transactionTemplate.executeWithoutResult(status -> {
            ipscMatchRepository.delete(match);
            ipscMatchRepository.flush();
        });
    }

    /**
     * Replaces all of a match's persisted stages with the given ones.
     *
     * @param match  the match the stages belong to; must not be null and must be managed by the
     *               current transaction.
     * @param stages the stages to add; may be null or empty, in which case the match is simply
     *               left with no stages.
     */
    protected void replaceStages(@NotNull IpscMatch match, List<IpscMatchStage> stages) {
        // The old stages are removed explicitly rather than left to IpscMatch.stages' orphanRemoval,
        // which only sees removals relative to the collection's last-flushed snapshot and so would
        // miss stages added since. Flushed immediately so the deletes are applied before any
        // replacement stages are inserted — otherwise Hibernate would order the inserts first,
        // tripping the (match_id, stage_number) unique constraint on a reused stage number.
        ipscMatchStageRepository.deleteAll(List.copyOf(match.getStages()));
        match.getStages().clear();
        ipscMatchRepository.flush();

        if (stages == null) {
            return;
        }

        for (IpscMatchStage stage : stages) {
            stage.setMatch(match);
            match.getStages().add(stage);
            ipscMatchStageRepository.save(stage);
        }
    }

    /**
     * Updates or adds stages on a match, matching each given stage to an existing one by its
     * stage number. Stages already on the match that aren't mentioned in {@code stages} are left
     * untouched.
     *
     * @param match  the match the stages belong to; must not be null and must be managed by the
     *               current transaction.
     * @param stages the stages to upsert; must not be null.
     */
    protected void upsertStages(@NotNull IpscMatch match, @NotNull List<IpscMatchStage> stages) {
        Map<Integer, IpscMatchStage> existingByNumber = match.getStages().stream()
                .collect(Collectors.toMap(IpscMatchStage::getStageNumber, Function.identity()));

        for (IpscMatchStage stage : stages) {
            IpscMatchStage existing = existingByNumber.get(stage.getStageNumber());
            if (existing == null) {
                stage.setMatch(match);
                match.getStages().add(stage);
                ipscMatchStageRepository.save(stage);
            } else if (stage.getStageName() != null) {
                existing.setStageName(stage.getStageName());
            }
        }
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
        Hibernate.initialize(match.getStages());
        return match;
    }
}
