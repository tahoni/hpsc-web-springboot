package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;

import java.util.List;

/**
 * The {@code IpscShooterLogService} interface provides CRUD access to shooter logs: a date range and the matches it
 * covers, whose overall results are calculated by {@link IpscShooterLogOverallService}.
 *
 * @since 16.1.0
 */
public interface IpscShooterLogService {
    /**
     * Creates a shooter log.
     *
     * <p>
     * Every match the log covers must be scheduled on a date within the log's start and end dates, both inclusive.
     * </p>
     *
     * @param request the shooter log to create, with the matches it covers set by match ID.
     * @return the created shooter log, including its generated ID.
     * @throws ValidationException if the request is null, a date is missing, the end date is before the start date,
     *                             or a match is scheduled outside the log's dates.
     * @throws NonFatalException   if a match in the request does not exist.
     * @since 16.1.0
     */
    ShooterLogResponse createShooterLog(ShooterLogRequest request) throws ValidationException, NonFatalException;

    /**
     * Creates a batch of shooter logs from CSV data.
     *
     * <p>
     * Each row is a log's {@code StartDate}, {@code EndDate} and {@code MatchIds}, the identifiers of the matches it
     * covers separated by {@code ;}, and is created by the same rules as
     * {@link #createShooterLog(ShooterLogRequest)}. Each row is saved on its own, so a row that fails, such as one with
     * a missing date, an unknown match or a match outside its dates, is reported with the values it supplied and the
     * reason while the rest are still created.
     * </p>
     *
     * @param csvData the CSV data, one shooter log per row, with a header row. Must not be null or blank.
     * @return a {@link ShooterLogBulkResponseHolder} with one result per row, in the same order as the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 16.3.0
     */
    ShooterLogBulkResponseHolder createShooterLogs(String csvData) throws ValidationException, FatalException;

    /**
     * Replaces a batch of existing shooter logs from CSV data.
     *
     * <p>
     * Each row is a log's {@code ShooterLogId}, {@code StartDate}, {@code EndDate} and {@code MatchIds}, the identifiers
     * of the matches it covers separated by {@code ;}, and replaces the log by the same rules as
     * {@link #updateShooterLog(Long, ShooterLogRequest)}, so both dates are required and an empty {@code MatchIds}
     * clears the log's matches. Each row is saved on its own, so a row that fails, such as one with no
     * {@code ShooterLogId}, an unknown log or match, or a match outside its dates, is reported with the values it
     * supplied and the reason while the rest are still replaced.
     * </p>
     *
     * @param csvData the CSV data, one shooter log per row, with a header row. Must not be null or blank.
     * @return a {@link ShooterLogBulkResponseHolder} with one result per row, in the same order as the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 16.4.0
     */
    ShooterLogBulkResponseHolder updateShooterLogs(String csvData) throws ValidationException, FatalException;

    /**
     * Partially updates a batch of existing shooter logs from CSV data.
     *
     * <p>
     * Each row is a log's {@code ShooterLogId} and any of {@code StartDate}, {@code EndDate} and {@code MatchIds}, and
     * updates the log by the same rules as {@link #patchShooterLog(Long, ShooterLogPatchRequest)}. A column left empty
     * leaves that field unchanged, so a non-empty {@code MatchIds} replaces the log's matches in full, and clearing them
     * takes a replacement instead. Each row is saved on its own, so a row that fails is reported with the values it
     * supplied and the reason while the rest are still updated.
     * </p>
     *
     * @param csvData the CSV data, one shooter log per row, with a header row. Must not be null or blank.
     * @return a {@link ShooterLogBulkResponseHolder} with one result per row, in the same order as the CSV rows.
     * @throws ValidationException if the CSV data is null, blank or cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     * @since 16.4.0
     */
    ShooterLogBulkResponseHolder patchShooterLogs(String csvData) throws ValidationException, FatalException;

    /**
     * Replaces an existing shooter log's dates and matches with those on the request.
     *
     * @param shooterLogId the identifier of the shooter log to replace.
     * @param request      the shooter log's replacement dates and matches.
     * @return the updated shooter log.
     * @throws ValidationException if the request is null, a date is missing, the end date is before the start date,
     *                             or a match is scheduled outside the log's dates.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists, or a match in the request does
     *                             not exist.
     * @since 16.1.0
     */
    ShooterLogResponse updateShooterLog(Long shooterLogId, ShooterLogRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Partially updates an existing shooter log, applying only the non-null fields on the request. The matches
     * the log ends up covering, whether changed or not, must all be scheduled within its resulting dates.
     *
     * @param shooterLogId the identifier of the shooter log to update.
     * @param request      the fields to change; any field left {@code null} is left unchanged.
     * @return the updated shooter log.
     * @throws ValidationException if the log's end date would end up before its start date, or a match would be
     *                             scheduled outside its dates.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists, or a match in the request does
     *                             not exist.
     * @since 16.1.0
     */
    ShooterLogResponse patchShooterLog(Long shooterLogId, ShooterLogPatchRequest request)
            throws ValidationException, NonFatalException;

    /**
     * Retrieves a shooter log.
     *
     * @param shooterLogId the identifier of the shooter log to retrieve.
     * @return the shooter log.
     * @throws NonFatalException if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    ShooterLogResponse getShooterLog(Long shooterLogId) throws NonFatalException;

    /**
     * Retrieves every shooter log.
     *
     * @return the shooter logs, earliest start date first; empty if there are none.
     * @since 16.1.0
     */
    List<ShooterLogResponse> getAllShooterLogs();

    /**
     * Deletes a shooter log, along with its links to its matches.
     *
     * @param shooterLogId the identifier of the shooter log to delete.
     * @throws ValidationException if the shooter log still has overall results, or another record references it.
     * @throws NonFatalException   if no shooter log with {@code shooterLogId} exists.
     * @since 16.1.0
     */
    void deleteShooterLog(Long shooterLogId) throws ValidationException, NonFatalException;
}
