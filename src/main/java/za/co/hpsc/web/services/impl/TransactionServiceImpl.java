package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.repositories.MatchStageRepository;
import za.co.hpsc.web.services.TransactionService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            Optional<Club> optionalClub = modifyClub(ipscResponse.getClub());
            Optional<Match> optionalMatch = modifyMatch(ipscResponse, optionalClub.orElse(null));
            if (optionalMatch.isEmpty()) {
                return;
            }

            Match match = optionalMatch.get();
            List<MatchStage> matchStages = modifyStages(match, ipscResponse.getStages());
            List<ScoreResponse> scoreResponses = filterScores(ipscResponse.getScores(), match.getDateUpdated());
            modifyScores(match, matchStages, scoreResponses, ipscResponse.getMembers());

            match.setDateUpdated(LocalDateTime.now());
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
     * Modifies the attributes of an existing club based on the provided club response data.
     *
     * @param clubResponse the {@link ClubResponse} object containing updated club information.
     *                     If null, no modification is performed
     * @return an {@code Optional} containing the modified {@link Club} if it exists,
     * or {@code Optional.empty()} if the input is null or the club is not found
     */
    protected Optional<Club> modifyClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        Optional<Club> optionalClub = findClub(clubResponse);
        // Initialises club attributes if found
        optionalClub.ifPresent(club -> club.init(clubResponse));
        return optionalClub;
    }

    /**
     * Modifies or creates a {@link Match} based on the provided IPSC response and club.
     *
     * <p>
     * If a {@link Match} already exists in the database and there are no newer scores
     * in the IPSC response, the existing {@link Match} is returned without modification.
     * Otherwise, a new or updated {@link Match} is initialised with the provided data and returned.
     * </p>
     *
     * @param ipscResponse The {@link IpscResponse} containing information about the match and scores.
     *                     Must not be null.
     * @param club         The {@link Club} associated with the match. Must not be null.
     * @return An {@code Optional} containing the modified or newly created {@link Match}.
     * The {@code Optional} returns the existing match as-is if no modification is required.
     */
    protected Optional<Match> modifyMatch(@NotNull IpscResponse ipscResponse, @NotNull Club club) {
        // Get the match from the database if it exists
        Match match = findMatch(ipscResponse.getMatch()).orElse(null);
        boolean ipscMatchExists = (match != null);
        boolean ipscResponseHasNewerScore = false;
        LocalDateTime matchLastUpdated = (match != null ? match.getDateUpdated() : LocalDateTime.now());

        // Skips update if no newer score; otherwise creates match
        if (ipscMatchExists) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.of(match);
            }
        } else {
            match = new Match();
        }

        // Initialise match attributes
        match.init(club, ipscResponse.getMatch());
        return Optional.of(match);
    }

    /**
     * Modifies the list of {@link MatchStage} based on the provided stage responses and
     * initialises their attributes.
     *
     * @param match          the {@link MatchResponse} object with which the stages are associated.
     *                       Must not be null.
     * @param stageResponses the list of {@link MatchResponse} containing stage details.
     *                       Can be null.
     * @return a list of {@link MatchStage} initialized with the provided stage responses,
     * or an empty list if {@code stageResponses} is null.
     */
    protected List<MatchStage> modifyStages(@NotNull Match match, List<StageResponse> stageResponses) {
        if (stageResponses == null) {
            return new ArrayList<>();
        }

        List<MatchStage> matchStages = new ArrayList<>();
        // Initialises stage attributes
        stageResponses.forEach(stageResponse -> {
            MatchStage matchStage = matchStageRepository.findByMatchAndStageNumber(match,
                    stageResponse.getStageId()).orElse(new MatchStage());
            matchStage.init(match, stageResponse);
            matchStages.add(matchStage);
        });
        return matchStages;
    }

    /**
     * Optionally modifies scores by mapping responses to competitors
     */
    protected void modifyScores(Match match, List<MatchStage> matchStages, List<ScoreResponse> scoreResponses, List<MemberResponse> memberResponses) {
        if ((scoreResponses == null) || (memberResponses == null)) {
            return;
        }

        // Maps score responses to corresponding member responses
        Set<Integer> memberIdsWithScores = scoreResponses.stream()
                .map(ScoreResponse::getMemberId)
                .collect(Collectors.toSet());
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(memberResponse -> memberIdsWithScores.contains(memberResponse.getMemberId()))
                .toList();

        // Initialises competitor attributes
        Map<Integer, Competitor> competitorMap = new HashMap<>();
        scoreMembers.forEach(memberResponse -> {
            Competitor competitor = findCompetitor(memberResponse).orElse(new Competitor());
            competitor.init(memberResponse);
            competitorMap.put(memberResponse.getMemberId(), competitor);
        });

        // Find the match overall and stage results
        competitorMap.keySet().forEach(memberId -> {
            Competitor competitor = competitorMap.get(memberId);
            List<ScoreResponse> scores = scoreResponses.stream()
                    .filter(sr -> sr.getMemberId().equals(memberId))
                    .toList();


            MatchCompetitor matchCompetitor = new MatchCompetitor(competitor, match);
            matchStages.forEach(matchStage -> {
                MatchStageCompetitor matchStageCompetitor = new MatchStageCompetitor(matchCompetitor, matchStage);
            });
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

        Optional<Club> club = Optional.empty();
        // Attempt to find the club by name
        if ((clubResponse.getClubName() != null) && (!clubResponse.getClubName().isBlank())) {
            club = clubRepository.findByName(clubResponse.getClubName());
        }
        // If no club with the name was found
        if (club.isEmpty() && (clubResponse.getClubCode() != null) && (!clubResponse.getClubCode().isBlank())) {
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

        // Attempts competitor lookup by SAPSA number or alias
        if ((icsAlias != null) && (!icsAlias.isBlank()) && (NumberUtils.isCreatable((icsAlias)))) {
            Integer sapsaNumber = Integer.parseInt(icsAlias);
            if (!IpscConstants.EXCLUDE_ICS_ALIAS.contains(sapsaNumber)) {
                competitor = competitorRepository.findBySapsaNumber(sapsaNumber);
            }
        }

        // If the competitor was not found
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

            // Returns the first matching competitor without a SAPSA number
            competitor = competitorList.stream()
                    .filter(c -> c.getSapsaNumber() == null)
                    .findFirst();
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
    // TODO: use
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
