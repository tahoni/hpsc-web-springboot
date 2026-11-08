package za.co.hpsc.web.services.impl;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.exceptions.NonFatalException;
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
        List<Long> matchIds = (shooterLog.getMatches() == null) ? List.of()
                : shooterLog.getMatches().stream().map(IpscMatch::getId).toList();

        // Brings each match's results up to date, which is a no-op for a match whose results already are
        for (Long matchId : matchIds) {
            ipscShooterResultService.calculateMatchShooterResult(matchId);
        }

        Map<Long, List<ShooterResult>> resultsByCompetitorId = new LinkedHashMap<>();
        if (!matchIds.isEmpty()) {
            for (ShooterResult shooterResult : shooterResultRepository.findAllByMatchIdInWithDetails(matchIds)) {
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
        // recent match in the log, with competitors without a category (NONE) a group of their own
        Map<OverallGroup, List<ShooterLogOverall>> groups = new LinkedHashMap<>();
        LocalDateTime dateCalculated = LocalDateTime.now();
        for (Map.Entry<Long, List<ShooterResult>> entry : resultsByCompetitorId.entrySet()) {
            List<ShooterResult> results = entry.getValue();
            ShooterResult latest = results.stream()
                    .max(Comparator.comparing((ShooterResult result) -> result.getMatch().getScheduledDate(),
                                    Comparator.nullsFirst(Comparator.naturalOrder()))
                            .thenComparing(ShooterResult::getId))
                    .orElseThrow();
            CompetitorCategory category = (latest.getCompetitorCategory() == null)
                    ? CompetitorCategory.NONE : latest.getCompetitorCategory();

            ShooterLogOverall overall = existingByCompetitorId.getOrDefault(entry.getKey(), new ShooterLogOverall());
            overall.setShooterLog(shooterLog);
            overall.setCompetitor(latest.getCompetitor());
            overall.setCompetitorCategory(category);
            overall.setDivision(latest.getDivision());
            overall.setPercentageInLog(averagePercentage(results));
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
     * Whether a shooter log's overall results are up to date: exactly the competitors with a shooter result in the
     * log have
     * one, each has been calculated, no shooter result was calculated after the oldest overall was, and the shooter
     * log was not updated after it either.
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
        for (ShooterLogOverall existing : existingByCompetitorId.values()) {
            if (existing.getDateCalculated() == null) {
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
        return (shooterLog.getDateUpdated() == null) || !shooterLog.getDateUpdated().isAfter(oldestCalculated);
    }

    /**
     * Averages the best percentages a competitor has across the matches of a shooter log: their
     * {@link IpscConstants#SHOOTER_LOG_BEST_MATCHES} highest, or all of them if they have fewer.
     *
     * @param results the competitor's shooter results in the log.
     * @return the average of the results' best percentages, to {@link IpscConstants#PERCENTAGE_SCALE} decimal
     * places, or {@code null} if none of them has a percentage.
     */
    private BigDecimal averagePercentage(List<ShooterResult> results) {
        List<BigDecimal> percentages = results.stream()
                .map(ShooterResult::getPercentageInLog)
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .limit(IpscConstants.SHOOTER_LOG_BEST_MATCHES)
                .toList();
        if (percentages.isEmpty()) {
            return null;
        }
        BigDecimal sum = percentages.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(percentages.size()), IpscConstants.PERCENTAGE_SCALE,
                RoundingMode.HALF_UP);
    }

    /**
     * Ranks a group of overall results by overall percentage, highest first, setting each one's rank. Competitors on an
     * equal percentage share a rank and the next rank is skipped, so percentages of 90, 90 and 80 rank 1, 1 and 3. An
     * overall without a percentage is not ranked.
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
