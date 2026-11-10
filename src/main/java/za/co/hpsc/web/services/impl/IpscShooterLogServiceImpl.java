package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.ShooterLogHelpers;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogBulkRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogBulkRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogRow;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.services.IpscShooterLogService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Default {@link IpscShooterLogService}, validating requests and building {@link ShooterLog}s outside any
 * transaction, then handing them to the {@link TransactionService} to persist.
 *
 * @since 16.1.0
 */
@Slf4j
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
    public ShooterLogBulkResponseHolder createShooterLogs(String csvData) throws FatalException {
        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        // Each row is saved in its own transaction, so a bad row is reported and skipped without affecting the others
        List<ShooterLogBulkResponse> shooterLogBulkResponses = new ArrayList<>();
        for (ShooterLogRequest request : readShooterLogs(csvData)) {
            try {
                shooterLogBulkResponses.add(new ShooterLogBulkResponse(true, "", createShooterLog(request), null));
            } catch (ValidationException | NonFatalException e) {
                log.warn("Shooter log skipped due to error: {}", e.getMessage());
                shooterLogBulkResponses.add(new ShooterLogBulkResponse(false, e.getMessage(), null,
                        new ShooterLogRow(request)));
            }
        }
        return new ShooterLogBulkResponseHolder(shooterLogBulkResponses);
    }

    @Override
    public ShooterLogBulkResponseHolder updateShooterLogs(String csvData) throws FatalException {
        return updateRows(csvData, this::updateShooterLog);
    }

    @Override
    public ShooterLogBulkResponseHolder patchShooterLogs(String csvData) throws FatalException {
        return updateRows(csvData, (shooterLogId, row) -> {
            ShooterLogPatchRequest patchRequest = new ShooterLogPatchRequest();
            patchRequest.setStartDate(row.getStartDate());
            patchRequest.setEndDate(row.getEndDate());
            // A CSV cell cannot tell an empty list from an omitted one, so an empty one leaves the matches unchanged
            patchRequest.setMatchIds((row.getMatchIds() == null || row.getMatchIds().isEmpty()) ? null : row.getMatchIds());
            patchRequest.setAddMatchIds(row.getAddMatchIds());
            patchRequest.setRemoveMatchIds(row.getRemoveMatchIds());
            return patchShooterLog(shooterLogId, patchRequest);
        }, false);
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
        request.validate();
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
        if (hasIds(request.getAddMatchIds()) || hasIds(request.getRemoveMatchIds())) {
            shooterLog.setMatches(adjustMatches(shooterLog.getMatches(), request.getAddMatchIds(),
                    request.getRemoveMatchIds()));
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
     * Whether a list of identifiers has any.
     *
     * @param ids the identifiers; may be null.
     * @return {@code true} if the list is neither null nor empty.
     */
    private static boolean hasIds(List<Long> ids) {
        return (ids != null) && !ids.isEmpty();
    }

    /**
     * Writes identifiers as text, separated by {@link SystemConstants#ARRAY_SEPARATOR}.
     *
     * @param ids the identifiers; may be null.
     * @return the identifiers as text, empty if there are none.
     */
    private static String joinIds(List<Long> ids) {
        return (ids == null) ? "" : ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(
                SystemConstants.ARRAY_SEPARATOR));
    }

    /**
     * Adds and removes matches on a shooter log's current matches, keeping their order: a match already covered is
     * not added again, and one that is not covered is ignored when removed.
     *
     * @param current        the matches the log covers now; may be null.
     * @param addMatchIds    the identifiers of the matches to add; may be null or empty.
     * @param removeMatchIds the identifiers of the matches to remove; may be null or empty.
     * @return the adjusted matches, as a new modifiable list.
     * @throws NonFatalException if a match to add does not exist.
     */
    protected List<IpscMatch> adjustMatches(List<IpscMatch> current, List<Long> addMatchIds,
                                            List<Long> removeMatchIds) {
        List<IpscMatch> adjusted = new ArrayList<>((current == null) ? List.of() : current);
        if (hasIds(addMatchIds)) {
            Set<Long> covered = new HashSet<>();
            adjusted.forEach(match -> covered.add(match.getId()));
            for (IpscMatch match : resolveMatches(addMatchIds)) {
                if (covered.add(match.getId())) {
                    adjusted.add(match);
                }
            }
        }
        if (hasIds(removeMatchIds)) {
            Set<Long> removed = new HashSet<>(removeMatchIds);
            adjusted.removeIf(match -> removed.contains(match.getId()));
        }
        return adjusted;
    }

    /**
     * Applies each row of a bulk update CSV to the shooter log it names, each in its own transaction, so a bad row is
     * reported and skipped without affecting the others.
     *
     * @param csvData the CSV data, one shooter log per row, with a header row. Must not be null or blank.
     * @param update  applies a row to the shooter log with the given identifier.
     * @return one result per row, in CSV order.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected ShooterLogBulkResponseHolder updateRows(String csvData,
                                                      BiFunction<Long, ShooterLogBulkRequest, ShooterLogResponse> update)
            throws FatalException {
        return updateRows(csvData, update, true);
    }

    private ShooterLogBulkResponseHolder updateRows(String csvData,
                                                    BiFunction<Long, ShooterLogBulkRequest, ShooterLogResponse> update,
                                                    boolean replace) throws FatalException {
        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<ShooterLogBulkResponse> shooterLogBulkResponses = new ArrayList<>();
        for (ShooterLogBulkRequest row : readRows(csvData, ShooterLogBulkRequest.class,
                ShooterLogBulkRequestCsvMixIn.class)) {
            try {
                if (row.getShooterLogId() == null) {
                    throw new ValidationException("Shooter log ID is required.");
                }
                shooterLogBulkResponses.add(
                        new ShooterLogBulkResponse(true, "", update.apply(row.getShooterLogId(), row), null));
            } catch (ValidationException | NonFatalException e) {
                log.warn("Shooter log {} skipped due to error: {}", replace ? "replacement" : "update", e.getMessage());
                ShooterLogRow shooterLogRow = new ShooterLogRow(row);
                shooterLogRow.setShooterLogId(Objects.toString(row.getShooterLogId(), ""));
                shooterLogRow.setAddMatchIds(joinIds(row.getAddMatchIds()));
                shooterLogRow.setRemoveMatchIds(joinIds(row.getRemoveMatchIds()));
                shooterLogBulkResponses.add(new ShooterLogBulkResponse(false, e.getMessage(), null, shooterLogRow));
            }
        }
        return new ShooterLogBulkResponseHolder(shooterLogBulkResponses);
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
     * Reads shooter logs from a CSV-formatted string and converts them into {@link ShooterLogRequest}s, binding the
     * CSV column headers onto each through {@link ShooterLogRequestCsvMixIn}. A header may omit any column and unknown
     * columns are ignored, so a row with nothing for a date is reported on its own rather than failing the file.
     *
     * @param csvData the CSV data, one shooter log per row, with a header row. Must not be null or blank.
     * @return the {@link ShooterLogRequest}s parsed from the CSV data, in row order.
     * @throws ValidationException if the CSV data cannot be parsed, such as a date that is not {@code yyyy-MM-dd}.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<ShooterLogRequest> readShooterLogs(@NonNull @NotBlank String csvData) throws FatalException {
        return readRows(csvData, ShooterLogRequest.class, ShooterLogRequestCsvMixIn.class);
    }

    /**
     * Reads rows from a CSV-formatted string, binding the CSV column headers onto each through a mix-in.
     *
     * @param csvData the CSV data, one row per shooter log, with a header row. Must not be null or blank.
     * @param type    the type to read each row as.
     * @param mixIn   the mix-in binding {@code type}'s constructor to the column headers.
     * @param <T>     the row type.
     * @return the rows parsed from the CSV data, in row order.
     * @throws ValidationException if the CSV data cannot be parsed, such as a date that is not {@code yyyy-MM-dd}.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    private <T extends ShooterLogRequest> List<T> readRows(String csvData, Class<T> type, Class<?> mixIn)
            throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        // The columns come from the header row, so the UpperCamelCase names bound by the mix-in are matched directly
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();
        csvMapper.addMixIn(type, mixIn);

        try (MappingIterator<T> requestMappingIterator = csvMapper.readerFor(type)
                .with(csvSchema)
                .readValues(csvData)) {
            return requestMappingIterator.readAll();

        } catch (MismatchedInputException | IllegalArgumentException | CsvReadException e) {
            log.error("Error parsing CSV data: {}", e.getMessage(), e);
            throw new ValidationException("Invalid CSV data format: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error reading CSV data: {}", e.getMessage(), e);
            throw new FatalException("Error reading CSV data: " + e.getMessage(), e);
        }
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
     * @throws ValidationException if a match is scheduled before the log's start date or after its end date.
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
