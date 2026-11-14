package za.co.hpsc.web.services.impl;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.helpers.ShooterLogHelpers;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.IpscShooterLogOverallService;
import za.co.hpsc.web.services.IpscShooterResultService;
import za.co.hpsc.web.services.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default {@link IpscShooterLogOverallService}, calculating shooter log overall results from the persisted
 * {@link ShooterResult}s of a log's matches and reading them back from the persisted {@link ShooterLogOverall}s.
 *
 * @since 16.1.0
 */
@Service
public class IpscShooterLogOverallServiceImpl implements IpscShooterLogOverallService {
    private final ShooterLogRepository shooterLogRepository;
    private final ShooterLogOverallRepository shooterLogOverallRepository;
    private final ShooterResultRepository shooterResultRepository;
    private final IpscShooterResultService ipscShooterResultService;
    private final TransactionService transactionService;

    public IpscShooterLogOverallServiceImpl(ShooterLogRepository shooterLogRepository,
                                            ShooterLogOverallRepository shooterLogOverallRepository,
                                            ShooterResultRepository shooterResultRepository,
                                            IpscShooterResultService ipscShooterResultService,
                                            TransactionService transactionService) {
        this.shooterLogRepository = shooterLogRepository;
        this.shooterLogOverallRepository = shooterLogOverallRepository;
        this.shooterResultRepository = shooterResultRepository;
        this.ipscShooterResultService = ipscShooterResultService;
        this.transactionService = transactionService;
    }

    @Override
    public ShooterLogOverallResponseHolder calculateShooterLogOverall(Long shooterLogId) throws NonFatalException {
        ShooterLog shooterLog = findShooterLogOrThrow(shooterLogId);
        // A match can be rescheduled after it was added to the log, so check the dates again before using any of them
        ShooterLogHelpers.validateMatchesWithinDates(shooterLog);
        List<Long> matchIds = matchesOf(shooterLog).stream().map(IpscMatch::getId).toList();

        // Brings each match's results up to date, which is a no-op for a match whose results already are
        for (Long matchId : matchIds) {
            ipscShooterResultService.calculateMatchShooterResult(matchId);
        }

        Map<Long, List<ShooterResult>> resultsByCompetitorId = new LinkedHashMap<>();
        if (!matchIds.isEmpty()) {
            for (ShooterResult shooterResult : shooterResultRepository.findAllByMatchIdInWithDetails(matchIds)) {
                // Only verified competitors are included, so an unverified competitor's overall is removed below
                if (!Boolean.TRUE.equals(shooterResult.getCompetitor().getIsVerified())) {
                    continue;
                }
                resultsByCompetitorId
                        .computeIfAbsent(shooterResult.getCompetitor().getId(), id -> new ArrayList<>())
                        .add(shooterResult);
            }
        }

        // Overall Results already calculated, found by their competitor, so calculating again updates them
        Map<Long, ShooterLogOverall> existingByCompetitorId = new HashMap<>();
        for (ShooterLogOverall existing : shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLogId)) {
            existingByCompetitorId.put(existing.getCompetitor().getId(), existing);
        }

        if (isUpToDate(shooterLog, resultsByCompetitorId, existingByCompetitorId)) {
            return getShooterLogOverallResults(shooterLogId);
        }

        // Competitors are ranked against the others in their division and competitor category, taken from their most
        // recent match in the log, with competitors in the NONE category a group of their own. A match's scheduled date
        // and a result's category are never null: both columns are NOT NULL
        Map<OverallGroup, List<ShooterLogOverall>> groups = new LinkedHashMap<>();
        LocalDateTime dateCalculated = LocalDateTime.now();
        for (Map.Entry<Long, List<ShooterResult>> entry : resultsByCompetitorId.entrySet()) {
            List<ShooterResult> results = entry.getValue();
            ShooterResult latest = results.stream()
                    .max(Comparator.comparing((ShooterResult result) -> result.getMatch().getScheduledDate())
                            .thenComparing(ShooterResult::getId))
                    .orElseThrow();
            CompetitorCategory category = latest.getCompetitorCategory();

            ShooterLogOverall overall = existingByCompetitorId.getOrDefault(entry.getKey(), new ShooterLogOverall());
            overall.setShooterLog(shooterLog);
            overall.setCompetitor(latest.getCompetitor());
            overall.setCompetitorCategory(category);
            overall.setDivision(latest.getDivision());
            List<ShooterResult> bestResults = bestResults(results);
            overall.setPercentageInLog(averagePercentage(bestResults));
            overall.setMatches(matchesOfResults(bestResults));
            overall.setDateCalculated(dateCalculated);
            groups.computeIfAbsent(new OverallGroup(latest.getDivision(), category), group -> new ArrayList<>())
                    .add(overall);
        }

        List<ShooterLogOverall> toSave = new ArrayList<>();
        for (List<ShooterLogOverall> group : groups.values()) {
            rank(group);
            toSave.addAll(group);
        }
        // A competitor who no longer has a result in the log no longer has an overall in it
        List<ShooterLogOverall> toDelete = existingByCompetitorId.entrySet().stream()
                .filter(existing -> !resultsByCompetitorId.containsKey(existing.getKey()))
                .map(Map.Entry::getValue)
                .toList();
        transactionService.replaceShooterLogOverallResults(toSave, toDelete);

        return getShooterLogOverallResults(shooterLogId);
    }

    @Override
    public ShooterLogOverallResponseHolder getShooterLogOverallResults(Long shooterLogId) throws NonFatalException {
        ShooterLog shooterLog = findShooterLogOrThrow(shooterLogId);
        List<ShooterLogOverallResponse> shooterLogOverallResults =
                shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLogId).stream()
                        .map(ShooterLogOverallResponse::new)
                        .toList();
        return new ShooterLogOverallResponseHolder(new ShooterLogResponse(shooterLog), shooterLogOverallResults);
    }

    @Override
    public List<ShooterLogOverallResponseHolder> getAllShooterLogOverallResults() {
        // Grouped by shooter log ID, so a log without overall results is still returned, with none
        Map<Long, List<ShooterLogOverallResponse>> overallResultsByShooterLogId = new HashMap<>();
        for (ShooterLogOverall shooterLogOverall : shooterLogOverallRepository.findAllWithDetails()) {
            overallResultsByShooterLogId
                    .computeIfAbsent(shooterLogOverall.getShooterLog().getId(), id -> new ArrayList<>())
                    .add(new ShooterLogOverallResponse(shooterLogOverall));
        }

        return shooterLogRepository.findAllWithMatches().stream()
                .map(shooterLog -> new ShooterLogOverallResponseHolder(new ShooterLogResponse(shooterLog),
                        overallResultsByShooterLogId.getOrDefault(shooterLog.getId(), List.of())))
                .toList();
    }

    /**
     * Finds a shooter log with its matches, or throws if there is none.
     *
     * @param shooterLogId the identifier of the shooter log to find.
     * @return the shooter log.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     */
    private ShooterLog findShooterLogOrThrow(Long shooterLogId) throws NonFatalException {
        return shooterLogRepository.findByIdWithMatches(shooterLogId)
                .orElseThrow(() -> new NonFatalException("No shooter log found with ID " + shooterLogId));
    }

    /**
     * The matches a shooter log covers.
     *
     * @param shooterLog the shooter log.
     * @return the log's matches, or an empty list if its matches were not loaded.
     */
    private List<IpscMatch> matchesOf(ShooterLog shooterLog) {
        return (shooterLog.getMatches() == null) ? List.of() : shooterLog.getMatches();
    }

    /**
     * Whether a shooter log's overall results are up to date: exactly the competitors with a shooter result in the
     * log have one, each has been calculated, no shooter result was calculated after the oldest overall was, and
     * neither the shooter log nor any of its matches was updated after it either. A match updated within the log's
     * dates, such as one rescheduled to another day, can change which of a competitor's matches is the most recent.
     *
     * @param shooterLog            the shooter log the overall results are for.
     * @param resultsByCompetitorId the log's shooter results, by competitor ID.
     * @param existingByCompetitorId the log's existing overall results, by competitor ID.
     * @return {@code true} if the overall results can be returned as they are; {@code false} if they must be
     * recalculated.
     */
    private boolean isUpToDate(ShooterLog shooterLog, Map<Long, List<ShooterResult>> resultsByCompetitorId,
                               Map<Long, ShooterLogOverall> existingByCompetitorId) {
        if (!resultsByCompetitorId.keySet().equals(existingByCompetitorId.keySet())) {
            return false;
        }
        if (existingByCompetitorId.isEmpty()) {
            // No competitors, so there is nothing to calculate
            return true;
        }

        LocalDateTime oldestCalculated = null;
        for (Map.Entry<Long, ShooterLogOverall> entry : existingByCompetitorId.entrySet()) {
            ShooterLogOverall existing = entry.getValue();
            if (existing.getDateCalculated() == null) {
                return false;
            }
            // An overall calculated before its matches were recorded, or from other matches, is out of date
            Set<Long> expectedMatchIds = matchesOfResults(bestResults(resultsByCompetitorId.get(entry.getKey())))
                    .stream().map(IpscMatch::getId).collect(Collectors.toSet());
            Set<Long> recordedMatchIds = (existing.getMatches() == null) ? Set.of()
                    : existing.getMatches().stream().map(IpscMatch::getId)
                    .collect(Collectors.toSet());
            if (!expectedMatchIds.equals(recordedMatchIds)) {
                return false;
            }
            if ((oldestCalculated == null) || existing.getDateCalculated().isBefore(oldestCalculated)) {
                oldestCalculated = existing.getDateCalculated();
            }
        }

        for (List<ShooterResult> results : resultsByCompetitorId.values()) {
            for (ShooterResult result : results) {
                if ((result.getDateCalculated() == null) || result.getDateCalculated().isAfter(oldestCalculated)) {
                    return false;
                }
            }
        }
        for (IpscMatch match : matchesOf(shooterLog)) {
            if ((match.getDateUpdated() != null) && match.getDateUpdated().isAfter(oldestCalculated)) {
                return false;
            }
        }
        return (shooterLog.getDateUpdated() == null) || !shooterLog.getDateUpdated().isAfter(oldestCalculated);
    }

    /**
     * Picks the results that count towards a competitor's overall: their
     * {@link IpscConstants#SHOOTER_LOG_BEST_MATCHES} highest percentages across the matches of a shooter log, or all
     * of them if they have fewer. Equal percentages are taken from the most recent match first, so the same results
     * are picked every time.
     *
     * <p>
     * A result without a percentage is ignored. {@link ShooterResult#getPercentageInLog()} is nullable, and although
     * {@code IpscShooterResultServiceImpl} always sets it, a row inserted by hand or by an earlier version may lack
     * one. Tolerating that, rather than failing, keeps one bad row from blocking the whole log's calculation.
     * </p>
     *
     * @param results the competitor's shooter results in the log.
     * @return the best results, highest percentage first.
     */
    private List<ShooterResult> bestResults(List<ShooterResult> results) {
        return results.stream()
                .filter(result -> result.getPercentageInLog() != null)
                .sorted(Comparator.comparing(ShooterResult::getPercentageInLog, Comparator.reverseOrder())
                        .thenComparing(result -> result.getMatch().getScheduledDate(), Comparator.reverseOrder())
                        .thenComparing(ShooterResult::getId))
                .limit(IpscConstants.SHOOTER_LOG_BEST_MATCHES)
                .toList();
    }

    /**
     * The matches the given shooter results are from, each once.
     *
     * <p>
     * Matches are told apart by identifier rather than by {@code equals}: {@link IpscMatch}'s generated {@code equals}
     * and {@code hashCode} read its lazily loaded club, which fails with a {@code LazyInitializationException} once the
     * persistence session has closed.
     * </p>
     *
     * @param results the shooter results.
     * @return the results' matches, as a new modifiable list, in the order of the results.
     */
    private List<IpscMatch> matchesOfResults(List<ShooterResult> results) {
        Map<Long, IpscMatch> matchesById = new LinkedHashMap<>();
        for (ShooterResult result : results) {
            matchesById.putIfAbsent(result.getMatch().getId(), result.getMatch());
        }
        return new ArrayList<>(matchesById.values());
    }

    /**
     * Averages the percentages of the results that count towards a competitor's overall.
     *
     * @param bestResults the competitor's best results, as picked by {@link #bestResults(List)}.
     * @return the average of the results' percentages, to {@link IpscConstants#PERCENTAGE_SCALE} decimal places, or
     * {@code null} if there are no results.
     */
    @Nullable private BigDecimal averagePercentage(List<ShooterResult> bestResults) {
        if (bestResults.isEmpty()) {
            return null;
        }
        BigDecimal sum = bestResults.stream().map(ShooterResult::getPercentageInLog)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(bestResults.size()), IpscConstants.PERCENTAGE_SCALE,
                RoundingMode.HALF_UP);
    }

    /**
     * Ranks a group of overall results by overall percentage, highest first, setting each one's rank. Competitors on an
     * equal percentage share a rank and the next rank is skipped, so percentages of 90, 90 and 80 rank 1, 1 and 3. An
     * overall without a percentage is not ranked, for the same reason {@link #averagePercentage} tolerates a missing
     * percentage: it can only come from a row that {@code IpscShooterResultServiceImpl} did not write.
     *
     * @param group the overall results to rank, which are in the same division and competitor category.
     */
    private void rank(List<ShooterLogOverall> group) {
        List<ShooterLogOverall> scored = group.stream()
                .filter(overall -> overall.getPercentageInLog() != null)
                .sorted(Comparator.comparing(ShooterLogOverall::getPercentageInLog, Comparator.reverseOrder()))
                .toList();
        group.forEach(overall -> overall.setRankInLog(null));
        int rank = 0;
        for (int index = 0; index < scored.size(); index++) {
            boolean tiedWithPrevious = (index > 0)
                    && (scored.get(index).getPercentageInLog()
                    .compareTo(scored.get(index - 1).getPercentageInLog()) == 0);
            if (!tiedWithPrevious) {
                rank = index + 1;
            }
            scored.get(index).setRankInLog(rank);
        }
    }

    /** The division and competitor category that overall results are ranked within. */
    private record OverallGroup(Division division, CompetitorCategory competitorCategory) {
    }
}
