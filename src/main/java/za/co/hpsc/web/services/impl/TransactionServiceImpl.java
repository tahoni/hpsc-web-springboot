package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.services.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    protected final PlatformTransactionManager transactionManager;

    protected final MatchRepository matchRepository;
    protected final CompetitorRepository competitorRepository;

    public TransactionServiceImpl(PlatformTransactionManager transactionManager,
                                  MatchRepository matchRepository, CompetitorRepository competitorRepository) {

        this.transactionManager = transactionManager;
        this.matchRepository = matchRepository;
        this.competitorRepository = competitorRepository;
    }

    @Override
    public void saveMatchResults(IpscResponse ipscResponse) {

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));

        // Executes transactional match result persistence; rolls back on failure
        try {
            Match match = findMatch(ipscResponse.getMatch()).orElse(new Match());
            // Skips persistence if no newer scores exist
            if (match.getId() != null) {
                LocalDateTime matchLastUpdated = match.getLastUpdated();
                boolean ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                        .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
                if (!ipscResponseHasNewerScore) {
                    return;
                }
            }
            initMatch(match, ipscResponse.getMatch());

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

    /**
     * Finds a match based on the provided {@link MatchResponse}.
     *
     * <p>
     * This method attempts to locate a match by first filtering on the scheduled date
     * and optionally, the match name if provided in the {@code matchResponse}.
     * It returns the first match that meets the filtering criteria.
     * </p>
     *
     * @param matchResponse the {@link MatchResponse} containing details about the match to be searched,
     *                      such as the scheduled date and match name.
     *                      If {@code matchResponse} is null, the method will return an empty {@code Optional}.
     * @return an {@code Optional} containing the first matching {@link Match} if found;
     * otherwise, an empty {@code Optional}.
     */
    protected Optional<Match> findMatch(MatchResponse matchResponse) {
        if (matchResponse == null) {
            return Optional.empty();
        }

        Optional<Match> match = Optional.empty();

        // Filters matches by date
        List<Match> matchList = matchRepository.findAllByScheduledDate(matchResponse.getMatchDate().toLocalDate());
        // Filters matches by name when present
        if (!matchResponse.getMatchName().isBlank()) {
            matchList = matchList.stream()
                    .filter(m -> matchResponse.getMatchName().equals(m.getName()))
                    .toList();
        }
        match = matchList.stream().findFirst();

        return match;
    }

    /**
     * Attempts to find a competitor based on the details provided in the {@code memberResponse}.
     *
     * <p>
     * If the {@code memberResponse} contains a valid SAPSA number, the competitor is searched
     * using that number. If no competitor is found, the method attempts to match using
     * the first name and last name provided. If a date of birth is available, it filters
     * the results further to ensure accuracy. The first matching competitor, if any, is returned.
     * </p>
     *
     * @param memberResponse the {@link MemberResponse} containing details about the competitor,
     *                       such as SAPSA number, first name, last name, and optionally, date of birth.
     *                       If {@code memberResponse} is null, an empty {@code Optional} is returned.
     * @return an {@code Optional} containing the first matching {@link Competitor} if found;
     * otherwise, an empty {@code Optional}.
     */
    protected Optional<Competitor> findCompetitor(MemberResponse memberResponse) {
        if (memberResponse == null) {
            return Optional.empty();
        }

        Optional<Competitor> competitor = Optional.empty();
        String icsAlias = memberResponse.getIcsAlias();

        // Attempt to find the competitor by SAPSA number
        if ((icsAlias != null) && (!icsAlias.isBlank()) && (NumberUtils.isCreatable(icsAlias))) {
            competitor = competitorRepository.findBySapsaNumber(Integer.parseInt(icsAlias));
        }

        if (competitor.isEmpty()) {
            List<Competitor> competitorList = new ArrayList<>();
            // Attempt to find the competitor by first and last name
            competitorList = competitorRepository.findAllByFirstNameAndLastName(memberResponse.getFirstName(),
                    memberResponse.getLastName());
            if (competitorList.isEmpty()) {
                return Optional.empty();
            }

            // Filters list by date of birth if present
            if (memberResponse.getDateOfBirth() != null) {
                competitorList = competitorList.stream()
                        .filter(c -> memberResponse.getDateOfBirth().toLocalDate().equals(c.getDateOfBirth()))
                        .toList();
            }

            // Returns the first matching competitor
            competitor = competitorList.stream().findFirst();
        }

        return competitor;
    }

    protected void initMatch(Match match, MatchResponse matchResponse) {

    }
}
