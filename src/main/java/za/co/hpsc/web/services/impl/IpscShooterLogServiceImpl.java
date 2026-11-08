package za.co.hpsc.web.services.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.ShooterLogHelpers;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.services.IpscShooterLogService;
import za.co.hpsc.web.services.TransactionService;

import java.util.*;

/**
 * Default {@link IpscShooterLogService}, validating requests and building {@link ShooterLog}s outside any
 * transaction, then handing them to the {@link TransactionService} to persist.
 *
 * @since 16.1.0
 */
@Service
public class IpscShooterLogServiceImpl implements IpscShooterLogService {
    private final ShooterLogRepository shooterLogRepository;
    private final ShooterLogOverallRepository shooterLogOverallRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final TransactionService transactionService;

    public IpscShooterLogServiceImpl(ShooterLogRepository shooterLogRepository,
                                     ShooterLogOverallRepository shooterLogOverallRepository,
                                     IpscMatchRepository ipscMatchRepository,
                                     TransactionService transactionService) {
        this.shooterLogRepository = shooterLogRepository;
        this.shooterLogOverallRepository = shooterLogOverallRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.transactionService = transactionService;
    }

    @Override
    public ShooterLogResponse createShooterLog(ShooterLogRequest request) {
        validateForCreate(request);

        ShooterLog shooterLog = new ShooterLog();
        applyFields(shooterLog, request);
        validateMatchesWithinDates(shooterLog);
        return toResponse(transactionService.saveShooterLog(shooterLog));
    }

    @Override
    public ShooterLogResponse updateShooterLog(Long shooterLogId, ShooterLogRequest request) {
        validateForCreate(request);
        ShooterLog shooterLog = findShooterLogOrThrow(shooterLogId);

        applyFields(shooterLog, request);
        validateMatchesWithinDates(shooterLog);
        return toResponse(transactionService.saveShooterLog(shooterLog));
    }

    @Override
    public ShooterLogResponse patchShooterLog(Long shooterLogId, ShooterLogPatchRequest request) {
        ShooterLog shooterLog = findShooterLogOrThrow(shooterLogId);

        if (request.getStartDate() != null) {
            shooterLog.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            shooterLog.setEndDate(request.getEndDate());
        }
        if ((shooterLog.getStartDate() != null) && (shooterLog.getEndDate() != null)
                && shooterLog.getEndDate().isBefore(shooterLog.getStartDate())) {
            throw new ValidationException("Shooter log end date cannot be before its start date.");
        }
        if (request.getMatchIds() != null) {
            shooterLog.setMatches(resolveMatches(request.getMatchIds()));
        }
        validateMatchesWithinDates(shooterLog);
        return toResponse(transactionService.saveShooterLog(shooterLog));
    }

    @Override
    public ShooterLogResponse getShooterLog(Long shooterLogId) {
        return toResponse(findShooterLogOrThrow(shooterLogId));
    }

    @Override
    public List<ShooterLogResponse> getAllShooterLogs() {
        return shooterLogRepository.findAllWithMatches().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteShooterLog(Long shooterLogId) {
        ShooterLog shooterLog = findShooterLogOrThrow(shooterLogId);

        if (shooterLogOverallRepository.existsByShooterLogId(shooterLogId)) {
            throw new ValidationException("Shooter log with ID " + shooterLogId
                    + " cannot be deleted: it has overall results.");
        }

        // TransactionService flushes the delete before committing, so a reference added by another
        // transaction since the check above surfaces here and is reported as a 400, not a 500.
        try {
            transactionService.deleteShooterLog(shooterLog);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Shooter log with ID " + shooterLogId
                    + " cannot be deleted: it is referenced by other records.", e);
        }
    }

    /**
     * Copies a request's dates onto a shooter log and replaces its matches with the ones the request names.
     *
     * @param shooterLog the shooter log to populate; must not be null.
     * @param request    the request carrying the field values; must not be null.
     * @throws NonFatalException if a match in the request does not exist.
     */
    protected void applyFields(ShooterLog shooterLog, ShooterLogRequest request) {
        shooterLog.setStartDate(request.getStartDate());
        shooterLog.setEndDate(request.getEndDate());
        shooterLog.setMatches(resolveMatches(request.getMatchIds()));
    }

    /**
     * Finds the matches with the given identifiers, in the order given and each once.
     *
     * @param matchIds the identifiers of the matches to find; may be null or empty, and may repeat an identifier.
     * @return the matches, as a new modifiable list; empty if {@code matchIds} is null or empty.
     * @throws NonFatalException if a match does not exist.
     */
    protected List<IpscMatch> resolveMatches(List<Long> matchIds) {
        if ((matchIds == null) || matchIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> distinctIds = matchIds.stream().distinct().toList();
        Map<Long, IpscMatch> matchesById = new HashMap<>();
        for (IpscMatch match : ipscMatchRepository.findAllById(distinctIds)) {
            matchesById.put(match.getId(), match);
        }

        List<IpscMatch> matches = new ArrayList<>();
        for (Long matchId : distinctIds) {
            IpscMatch match = matchesById.get(matchId);
            if (match == null) {
                throw new NonFatalException("No IPSC match found with ID " + matchId);
            }
            matches.add(match);
        }
        return matches;
    }

    /**
     * Checks that every match a shooter log covers is scheduled on a date within the log's start and end dates,
     * both inclusive.
     *
     * @param shooterLog the shooter log to check; must not be null. A log without a start or end date, or without
     *                   matches, has nothing to check.
     * @throws ValidationException if a match has no scheduled date, or is scheduled before the log's start date or
     *                             after its end date.
     */
    protected void validateMatchesWithinDates(ShooterLog shooterLog) {
        ShooterLogHelpers.validateMatchesWithinDates(shooterLog);
    }

    /**
     * Retrieves an existing shooter log with its matches, or throws if none exists with the given ID.
     *
     * @param shooterLogId the identifier to look up.
     * @return the matching {@link ShooterLog}.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     */
    protected ShooterLog findShooterLogOrThrow(Long shooterLogId) {
        return shooterLogRepository.findByIdWithMatches(shooterLogId)
                .orElseThrow(() -> new NonFatalException("No shooter log found with ID " + shooterLogId));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a shooter log.
     *
     * @param request the request to validate.
     * @throws ValidationException if the request is null, a date is missing or the end date is before the start date.
     */
    protected void validateForCreate(ShooterLogRequest request) {
        if (request == null) {
            throw new ValidationException("Shooter log request cannot be null.");
        }
        request.validate();
    }

    /**
     * Maps a shooter log to the response shape returned by the controller.
     *
     * @param shooterLog the shooter log to map.
     * @return the mapped {@link ShooterLogResponse}.
     */
    protected ShooterLogResponse toResponse(ShooterLog shooterLog) {
        return new ShooterLogResponse(shooterLog);
    }
}
