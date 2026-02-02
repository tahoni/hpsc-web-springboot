package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.repositories.MatchStageRepository;
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
            // Persists the match
            Optional<Match> optionalMatch = persistMatch(ipscResponse);
            if (optionalMatch.isEmpty()) {
                return;
            }

            Match match = optionalMatch.get();
            persistMatchStages(match, ipscResponse.getStages());
            match.setLastUpdated(LocalDateTime.now());

            matchRepository.save(match);
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
     * Persists or updates a match based on the provided IPSC response.
     *
     * <p>
     * If the match already exists in the database and the scores in the response
     * are not newer, no updates are made, and the existing match is returned.
     * If the match does not exist or newer scores are available, the match is
     * updated or created, then persisted to the database.
     * </p>
     *
     * @param ipscResponse the {@link IpscResponse} containing match and score data
     *                     to persist. Must not be null.
     * @return an {@code Optional} containing the persisted or updated {@link Match},
     * an empty {@code Optional} if the match cannot be persisted.
     */
    protected Optional<Match> persistMatch(@NotNull IpscResponse ipscResponse) {
        // Get the match from the database if it exists
        Match match = findMatch(ipscResponse.getMatch()).orElse(null);
        boolean ipscMatchExists = match != null;
        boolean ipscResponseHasNewerScore = false;
        LocalDateTime matchLastUpdated = (match != null ? match.getLastUpdated() : LocalDateTime.now());
        // Skips persistence if the match exists and no newer scores were updated
        if (ipscMatchExists) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.of(match);
            }
        } else {
            match = new Match();
        }

        // Updates the match name, club and scheduled date
        match.setName(ipscResponse.getMatch().getMatchName());
        match.setClub(persistClub(ipscResponse.getClub()).orElse(null));
        match.setScheduledDate(ipscResponse.getMatch().getMatchDate().toLocalDate());

        // TODO: update division and category based on IPSC response

        return Optional.of(match);
    }

    /**
     * Persists a club based on the provided club response. If the club already exists,
     * updates its name and abbreviation using the data from the given club response.
     *
     * @param clubResponse the response containing club information; may be null
     * @return an optional containing the updated or existing club, or an empty optional if the input is null
     */
    protected Optional<Club> persistClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        Optional<Club> optionalClub = findClub(clubResponse);
        // Updates club name and abbreviation if present
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            club.setName(clubResponse.getClubName());
            club.setAbbreviation(clubResponse.getClubCode());
        }
        return optionalClub;
    }

    /**
     * Persists and returns a list of {@link MatchStage} objects based on the provided {@link Match} and stage responses.
     * If the input stageResponses is null, an empty list is returned.
     * This method processes the {@link StageResponse} objects, setting properties for each {@link MatchStage},
     * without persisting changes to the repository.
     *
     * @param match          the {@link Match} object associated with the stages; must not be null.
     * @param stageResponses a {@link List} of {@link StageResponse} objects containing stage details; can be null.
     */
    protected void persistMatchStages(@NotNull Match match, List<StageResponse> stageResponses) {
        if (stageResponses == null) {
            return;
        }

        // Sets stage properties; does not persist changes
        stageResponses.forEach(stageResponse -> {
            MatchStage matchStage = matchStageRepository.findByMatchAndStageNumber(match,
                    stageResponse.getStageId()).orElse(new MatchStage());
            matchStage.setMatch(match);
            matchStage.setStageNumber(stageResponse.getStageId());
            matchStage.setStageName(stageResponse.getStageName());
        });
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
     * Searches for a club based on the provided club response details.
     *
     * <p>
     * This method first attempts to find the club by its name, and if not found,
     * it tries to locate the club using its abbreviation.
     * </p>
     *
     * @param clubResponse the {@link ClubResponse} containing details about the club,
     *                     such as its name and abbreviation
     * @return an {@code Optional} containing the found {@link Club}, otherwise,
     * an empty {@code Optional}.
     */
    protected Optional<Club> findClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        // Attempt to find the club by name
        Optional<Club> club = clubRepository.findByName(clubResponse.getClubName());
        // If the club was not found by name
        if (club.isEmpty()) {
            // Attempt to find the club by abbreviation
            club = clubRepository.findByAbbreviation(clubResponse.getClubCode());
        }

        return club;
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

        // If the competitor was not found by SAPSA number
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

    /**
     * Filters a list of ScoreResponse objects based on specific criteria.
     *
     * <p>
     * The method filters out any {@link ScoreResponse} objects whose last modified timestamp
     * is not after the provided {@code matchLastUpdated} timestamp.
     * If the input list is null, an empty list is returned.
     * </p>
     *
     * @param scoreResponseList the list of {@link ScoreResponse} objects to filter. Can be null
     * @param matchLastUpdated  the timestamp representing the last updated time of the match,
     *                          used to filter the scores. Can be null.
     * @return a filtered list of {@link ScoreResponse} objects or an empty list if the input is null
     */
    protected List<ScoreResponse> filterScores(List<ScoreResponse> scoreResponseList,
                                               LocalDateTime matchLastUpdated) {

        if (scoreResponseList == null) {
            return new ArrayList<>();
        }

        // Filters scores updated after the time the match was last updated
        if (matchLastUpdated != null) {
            scoreResponseList = scoreResponseList.stream()
                    .filter(sr -> matchLastUpdated.isBefore(sr.getLastModified()))
                    .toList();
        }

        return scoreResponseList;
    }
}
