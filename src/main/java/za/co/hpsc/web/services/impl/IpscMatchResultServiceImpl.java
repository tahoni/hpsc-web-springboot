package za.co.hpsc.web.services.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchResult;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultMatchResponse;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponseHolder;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchResultRepository;
import za.co.hpsc.web.services.IpscMatchResultService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.utils.NumberUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Default {@link IpscMatchResultService}, reading match results from the persisted {@link MatchResult}s.
 *
 * @since 18.1.0
 */
@Service
public class IpscMatchResultServiceImpl implements IpscMatchResultService {
    /**
     * How long after the results were calculated the match or one of its competitors must have been updated for the
     * results to be recalculated. A smaller difference is treated as no change, so updates made around the time of
     * the calculation, or lost to a database's timestamp rounding, do not trigger one.
     */
    private static final Duration RECALCULATION_THRESHOLD = Duration.ofMinutes(5);

    private final IpscMatchRepository ipscMatchRepository;
    private final MatchResultRepository matchResultRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final TransactionService transactionService;

    public IpscMatchResultServiceImpl(IpscMatchRepository ipscMatchRepository,
                                        MatchResultRepository matchResultRepository,
                                        MatchCompetitorRepository matchCompetitorRepository,
                                        TransactionService transactionService) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.matchResultRepository = matchResultRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.transactionService = transactionService;
    }

    @Override
    public MatchResultResponseHolder calculateMatchResults(Long matchId) throws NonFatalException {
        IpscMatch match = findMatchOrThrow(matchId);
        // Only verified competitors are included. Competitors without points (none, or 0) did not score, so they get
        // no result and are not ranked against the others
        List<MatchCompetitor> matchCompetitors =
                matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(matchId).stream()
                        .filter(this::isVerified)
                        .filter(this::hasPoints)
                        .toList();
        Set<Long> scoredMatchCompetitorIds = new HashSet<>();
        for (MatchCompetitor matchCompetitor : matchCompetitors) {
            scoredMatchCompetitorIds.add(matchCompetitor.getId());
        }

        // Results already calculated, found by their match competitor, so calculating again updates them. A result
        // whose competitor is no longer verified or no longer has points, such as after the points were cleared, is
        // stale and removed.
        Map<Long, MatchResult> existingByMatchCompetitorId = new HashMap<>();
        List<MatchResult> staleMatchResults = new ArrayList<>();
        for (MatchResult existing : matchResultRepository.findAllByMatchIdWithDetails(matchId)) {
            Long matchCompetitorId = existing.getMatchCompetitor().getId();
            if (scoredMatchCompetitorIds.contains(matchCompetitorId)) {
                existingByMatchCompetitorId.put(matchCompetitorId, existing);
            } else {
                staleMatchResults.add(existing);
            }
        }

        if (staleMatchResults.isEmpty() && isUpToDate(match, matchCompetitors, existingByMatchCompetitorId)) {
            return getMatchResults(matchId);
        }

        // Every qualifying competitor is ranked together, regardless of firearm type, division or competitor
        // category, and measured against the match winner
        LocalDateTime dateCalculated = LocalDateTime.now();
        List<MatchResult> matchResults = new ArrayList<>();
        if (!matchCompetitors.isEmpty()) {
            Map<Long, Integer> ranks = rank(matchCompetitors);
            BigDecimal winnerPoints = matchCompetitors.stream()
                    .map(MatchCompetitor::getPoints)
                    .max(Comparator.naturalOrder())
                    .orElseThrow();

            for (MatchCompetitor matchCompetitor : matchCompetitors) {
                MatchResult matchResult = existingByMatchCompetitorId
                        .getOrDefault(matchCompetitor.getId(), new MatchResult());
                matchResult.setMatch(match);
                matchResult.setCompetitor(matchCompetitor.getCompetitor());
                matchResult.setMatchCompetitor(matchCompetitor);
                matchResult.setCompetitorCategory(matchCompetitor.getCompetitorCategory());
                matchResult.setDivision(matchCompetitor.getDivision());
                matchResult.setRankInLog(ranks.get(matchCompetitor.getId()));
                matchResult.setPercentageInLog(NumberUtil.calculatePercentage(matchCompetitor.getPoints(),
                        winnerPoints, IpscConstants.PERCENTAGE_SCALE));
                matchResult.setDateCalculated(dateCalculated);
                matchResults.add(matchResult);
            }
        }
        transactionService.replaceMatchResults(matchResults, staleMatchResults);

        return getMatchResults(matchId);
    }

    /**
     * Whether a match competitor's competitor has been verified. A competitor whose verification was not recorded
     * is not verified.
     *
     * @param matchCompetitor the match competitor to check.
     * @return {@code true} if the competitor is verified.
     */
    private boolean isVerified(MatchCompetitor matchCompetitor) {
        return Boolean.TRUE.equals(matchCompetitor.getCompetitor().getIsVerified());
    }

    /**
     * Whether a match competitor scored: their points are present and above zero.
     *
     * @param matchCompetitor the match competitor to check.
     * @return {@code true} if the match competitor has points above zero.
     */
    private boolean hasPoints(MatchCompetitor matchCompetitor) {
        return (matchCompetitor.getPoints() != null) && (matchCompetitor.getPoints().signum() > 0);
    }

    @Override
    public MatchResultResponseHolder getMatchResults(Long matchId) throws NonFatalException {
        IpscMatch match = findMatchOrThrow(matchId);
        List<MatchResultResponse> matchResults =
                matchResultRepository.findAllByMatchIdWithDetails(matchId).stream()
                        .map(MatchResultResponse::new)
                        .toList();
        return new MatchResultResponseHolder(new MatchResultMatchResponse(match), matchResults);
    }

    @Override
    public List<MatchResultResponseHolder> getAllMatchResults() {
        // Grouped by match ID, so a match without results is still returned, with none
        Map<Long, List<MatchResultResponse>> matchResultsByMatchId = new HashMap<>();
        for (MatchResult matchResult : matchResultRepository.findAllWithDetails()) {
            matchResultsByMatchId
                    .computeIfAbsent(matchResult.getMatch().getId(), id -> new ArrayList<>())
                    .add(new MatchResultResponse(matchResult));
        }

        return ipscMatchRepository.findAll(Sort.by("scheduledDate", "id")).stream()
                .map(match -> new MatchResultResponseHolder(new MatchResultMatchResponse(match),
                        matchResultsByMatchId.getOrDefault(match.getId(), List.of())))
                .toList();
    }

    /**
     * Finds a match, or throws if there is none.
     *
     * @param matchId the identifier of the match to find.
     * @return the match.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    private IpscMatch findMatchOrThrow(Long matchId) throws NonFatalException {
        return ipscMatchRepository.findById(matchId)
                .orElseThrow(() -> new NonFatalException("No IPSC match found with ID " + matchId));
    }

    /**
     * Whether a match's match results are up to date: every competitor has a result, each result has been
     * calculated, and neither the match nor any competitor was updated at least {@link #RECALCULATION_THRESHOLD}
     * after the oldest result was calculated.
     *
     * @param match                       the match the results are for.
     * @param matchCompetitors            the match's competitors.
     * @param existingByMatchCompetitorId the match's existing results, by match competitor ID.
     * @return {@code true} if the results can be returned as they are; {@code false} if they must be recalculated.
     */
    private boolean isUpToDate(IpscMatch match, List<MatchCompetitor> matchCompetitors,
                               Map<Long, MatchResult> existingByMatchCompetitorId) {
        LocalDateTime oldestCalculated = null;
        for (MatchCompetitor matchCompetitor : matchCompetitors) {
            MatchResult existing = existingByMatchCompetitorId.get(matchCompetitor.getId());
            if ((existing == null) || (existing.getDateCalculated() == null)) {
                return false;
            }
            if ((oldestCalculated == null) || existing.getDateCalculated().isBefore(oldestCalculated)) {
                oldestCalculated = existing.getDateCalculated();
            }
        }
        if (oldestCalculated == null) {
            // No competitors, so there is nothing to calculate
            return true;
        }

        if (isUpdatedSignificantlyAfter(match.getDateUpdated(), oldestCalculated)) {
            return false;
        }
        for (MatchCompetitor matchCompetitor : matchCompetitors) {
            if (isUpdatedSignificantlyAfter(matchCompetitor.getDateUpdated(), oldestCalculated)) {
                return false;
            }
        }
        return true;
    }

    private boolean isUpdatedSignificantlyAfter(LocalDateTime dateUpdated, LocalDateTime dateCalculated) {
        return (dateUpdated != null)
                && (Duration.between(dateCalculated, dateUpdated).compareTo(RECALCULATION_THRESHOLD) >= 0);
    }

    /**
     * Ranks match competitors by match points, highest first. Competitors on equal points share a rank
     * and the next rank is skipped, so points of 90, 90 and 80 rank 1, 1 and 3.
     *
     * @param group the match competitors to rank, who all have points, as {@link #hasPoints} has already excluded
     *              the others.
     * @return each match competitor's rank, by match competitor ID.
     */
    private Map<Long, Integer> rank(List<MatchCompetitor> group) {
        List<MatchCompetitor> scored = group.stream()
                .sorted(Comparator.comparing(MatchCompetitor::getPoints, Comparator.reverseOrder()))
                .toList();
        Map<Long, Integer> ranks = new HashMap<>();
        int rank = 0;
        for (int index = 0; index < scored.size(); index++) {
            boolean tiedWithPrevious = (index > 0)
                    && (scored.get(index).getPoints().compareTo(scored.get(index - 1).getPoints()) == 0);
            if (!tiedWithPrevious) {
                rank = index + 1;
            }
            ranks.put(scored.get(index).getId(), rank);
        }
        return ranks;
    }
}
