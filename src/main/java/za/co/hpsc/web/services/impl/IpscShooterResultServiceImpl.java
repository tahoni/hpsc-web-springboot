package za.co.hpsc.web.services.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterMatchResultResponse;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.IpscShooterResultService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.utils.NumberUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link IpscShooterResultService}, reading shooter results from the persisted {@link ShooterResult}s.
 *
 * @since 15.0.0
 */
@Service
public class IpscShooterResultServiceImpl implements IpscShooterResultService {
    /**
     * How long after the results were calculated the match or one of its competitors must have been updated for the
     * results to be recalculated. A smaller difference is treated as no change, so updates made around the time of
     * the calculation, or lost to a database's timestamp rounding, do not trigger one.
     */
    private static final Duration RECALCULATION_THRESHOLD = Duration.ofMinutes(5);

    private final IpscMatchRepository ipscMatchRepository;
    private final ShooterResultRepository shooterResultRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final TransactionService transactionService;

    public IpscShooterResultServiceImpl(IpscMatchRepository ipscMatchRepository,
                                        ShooterResultRepository shooterResultRepository,
                                        MatchCompetitorRepository matchCompetitorRepository,
                                        TransactionService transactionService) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.shooterResultRepository = shooterResultRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.transactionService = transactionService;
    }

    @Override
    public ShooterResponseHolder calculateMatchShooterResult(Long matchId) throws NonFatalException {
        IpscMatch match = findMatchOrThrow(matchId);
        List<MatchCompetitor> matchCompetitors =
                matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(matchId);

        // Results already calculated, found by their match competitor, so calculating again updates them
        Map<Long, ShooterResult> existingByMatchCompetitorId = new HashMap<>();
        for (ShooterResult existing : shooterResultRepository.findAllByMatchIdWithDetails(matchId)) {
            existingByMatchCompetitorId.put(existing.getMatchCompetitor().getId(), existing);
        }

        if (isUpToDate(match, matchCompetitors, existingByMatchCompetitorId)) {
            return getShooterResults(matchId);
        }

        // Competitors are ranked against the others in their firearm type and division
        Map<ResultGroup, List<MatchCompetitor>> groups = new LinkedHashMap<>();
        for (MatchCompetitor matchCompetitor : matchCompetitors) {
            groups.computeIfAbsent(new ResultGroup(matchCompetitor.getFirearmType(), matchCompetitor.getDivision()),
                    group -> new ArrayList<>()).add(matchCompetitor);
        }

        LocalDateTime dateCalculated = LocalDateTime.now();
        List<ShooterResult> shooterResults = new ArrayList<>();
        for (List<MatchCompetitor> group : groups.values()) {
            Map<Long, Integer> ranks = rank(group);
            BigDecimal winnerPoints = group.stream()
                    .map(MatchCompetitor::getPoints)
                    .filter(points -> points != null)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            for (MatchCompetitor matchCompetitor : group) {
                ShooterResult shooterResult = existingByMatchCompetitorId
                        .getOrDefault(matchCompetitor.getId(), new ShooterResult());
                shooterResult.setMatch(match);
                shooterResult.setCompetitor(matchCompetitor.getCompetitor());
                shooterResult.setMatchCompetitor(matchCompetitor);
                shooterResult.setCompetitorCategory(matchCompetitor.getCompetitorCategory());
                shooterResult.setDivision(matchCompetitor.getDivision());
                shooterResult.setRankInLog(ranks.get(matchCompetitor.getId()));
                shooterResult.setPercentageInLog(matchCompetitor.getPoints() == null ? null
                        : NumberUtil.calculatePercentage(matchCompetitor.getPoints(), winnerPoints,
                        IpscConstants.PERCENTAGE_SCALE));
                shooterResult.setDateCalculated(dateCalculated);
                shooterResults.add(shooterResult);
            }
        }
        transactionService.saveShooterResults(shooterResults);

        return getShooterResults(matchId);
    }

    @Override
    public ShooterResponseHolder getShooterResults(Long matchId) throws NonFatalException {
        IpscMatch match = findMatchOrThrow(matchId);
        List<ShooterResultResponse> shooterResults =
                shooterResultRepository.findAllByMatchIdWithDetails(matchId).stream()
                        .map(ShooterResultResponse::new)
                        .toList();
        return new ShooterResponseHolder(new ShooterMatchResultResponse(match), shooterResults);
    }

    @Override
    public List<ShooterResponseHolder> getAllShooterResults() {
        // Grouped by match ID, so a match without results is still returned, with none
        Map<Long, List<ShooterResultResponse>> shooterResultsByMatchId = new HashMap<>();
        for (ShooterResult shooterResult : shooterResultRepository.findAllWithDetails()) {
            shooterResultsByMatchId
                    .computeIfAbsent(shooterResult.getMatch().getId(), id -> new ArrayList<>())
                    .add(new ShooterResultResponse(shooterResult));
        }

        return ipscMatchRepository.findAll(Sort.by("scheduledDate", "id")).stream()
                .map(match -> new ShooterResponseHolder(new ShooterMatchResultResponse(match),
                        shooterResultsByMatchId.getOrDefault(match.getId(), List.of())))
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
     * Whether a match's shooter results are up to date: every competitor has a result, each result has been
     * calculated, and neither the match nor any competitor was updated at least {@link #RECALCULATION_THRESHOLD}
     * after the oldest result was calculated.
     *
     * @param match                       the match the results are for.
     * @param matchCompetitors            the match's competitors.
     * @param existingByMatchCompetitorId the match's existing results, by match competitor ID.
     * @return {@code true} if the results can be returned as they are; {@code false} if they must be recalculated.
     */
    private boolean isUpToDate(IpscMatch match, List<MatchCompetitor> matchCompetitors,
                               Map<Long, ShooterResult> existingByMatchCompetitorId) {
        LocalDateTime oldestCalculated = null;
        for (MatchCompetitor matchCompetitor : matchCompetitors) {
            ShooterResult existing = existingByMatchCompetitorId.get(matchCompetitor.getId());
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
     * Ranks a group of match competitors by match points, highest first. Competitors on equal points share a rank
     * and the next rank is skipped, so points of 90, 90 and 80 rank 1, 1 and 3. A competitor without points is not
     * ranked.
     *
     * @param group the match competitors to rank, who shot the same firearm type and division.
     * @return each ranked match competitor's rank, by match competitor ID.
     */
    private Map<Long, Integer> rank(List<MatchCompetitor> group) {
        List<MatchCompetitor> scored = group.stream()
                .filter(matchCompetitor -> matchCompetitor.getPoints() != null)
                .sorted(Comparator.comparing(MatchCompetitor::getPoints, Comparator.<BigDecimal>reverseOrder()))
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

    /** The firearm type and division that match competitors are ranked within. */
    private record ResultGroup(FirearmType firearmType, Division division) {
    }
}
