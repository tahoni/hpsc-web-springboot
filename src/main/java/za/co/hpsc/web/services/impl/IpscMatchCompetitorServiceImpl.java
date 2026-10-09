package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.MatchCompetitorHelpers;
import za.co.hpsc.web.mappers.MatchCompetitorMapper;
import za.co.hpsc.web.mappers.MatchCompetitorRowMapper;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.IpscEntityClubService;
import za.co.hpsc.web.services.IpscMatchCompetitorService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static za.co.hpsc.web.utils.StringUtil.hasText;

@Slf4j
@Service
public class IpscMatchCompetitorServiceImpl implements IpscMatchCompetitorService {
    private final MatchCompetitorRepository matchCompetitorRepository;

    private final MatchCompetitorMapper matchCompetitorMapper;
    private final MatchCompetitorRowMapper matchCompetitorRowMapper;

    private final IpscEntityClubService ipscEntityClubService;
    private final TransactionService transactionService;

    public IpscMatchCompetitorServiceImpl(MatchCompetitorRepository matchCompetitorRepository,
                                          MatchCompetitorMapper matchCompetitorMapper,
                                          MatchCompetitorRowMapper matchCompetitorRowMapper,
                                          IpscEntityClubService ipscEntityClubService,
                                          TransactionService transactionService) {
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchCompetitorMapper = matchCompetitorMapper;
        this.matchCompetitorRowMapper = matchCompetitorRowMapper;
        this.ipscEntityClubService = ipscEntityClubService;
        this.transactionService = transactionService;
    }

    @Override
    public MatchCompetitorResponse createMatchCompetitor(MatchCompetitorRequest request) {
        validateForCreate(request);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitorMapper.applyFields(matchCompetitor, request);
        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorBulkResponseHolder createMatchCompetitors(String csvData, String club)
            throws FatalException {
        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        // Import the home club's rows unless another club is asked for
        ClubIdentifier targetClub = hasText(club) ? matchCompetitorMapper.resolveMatchClub(club) : IpscConstants.HOME_CLUB_IDENTIFIER;
        List<MatchCompetitorRequest> requests = readMatchCompetitors(csvData);

        // Each row is saved in its own transaction, so a bad row is reported and skipped without affecting the
        // others. save() also refuses a row that duplicates one saved earlier in this import.
        List<MatchCompetitorBulkResponse> matchCompetitorBulkResponses = new ArrayList<>();
        for (MatchCompetitorRequest request : requests) {
            // Set once the row's fields are applied, so a row that then fails to save, as a duplicate does, is
            // reported by its resolved values
            MatchCompetitor matchCompetitor = null;
            try {
                if (!isForClub(request, targetClub)) {
                    String targetClubName = (targetClub != null) ? targetClub.getName() : "";
                    matchCompetitorBulkResponses.add(
                            failedRow("Skipped: match club is not " + targetClubName, request, null));
                    continue;
                }

                // Report every missing or unresolvable required field of the row together, not just the first
                MatchCompetitor resolvable = new MatchCompetitor();
                matchCompetitorMapper.populateResolvableFields(resolvable, request);
                String missingFields = MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields(
                        resolvable, request);
                if (hasText(missingFields)) {
                    log.warn("Match competitor skipped due to missing or unresolvable required fields: {}", missingFields);
                    matchCompetitorBulkResponses.add(failedRow(missingFields, request, resolvable));
                    continue;
                }

                matchCompetitor = new MatchCompetitor();
                matchCompetitorMapper.applyFields(matchCompetitor, request);
                matchCompetitorBulkResponses.add(
                        new MatchCompetitorBulkResponse(true, "", toResponse(save(matchCompetitor)), null));
            } catch (ValidationException | NonFatalException e) {
                log.warn("Match competitor skipped due to error: {}", e.getMessage());
                matchCompetitorBulkResponses.add(failedRow(e.getMessage(), request, matchCompetitor));
            }
        }

        return new MatchCompetitorBulkResponseHolder(matchCompetitorBulkResponses);
    }

    @Override
    public MatchCompetitorResponse updateMatchCompetitor(Long matchCompetitorId, MatchCompetitorRequest request) {
        validateForCreate(request);
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        matchCompetitorMapper.applyFields(matchCompetitor, request);
        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorResponse patchMatchCompetitor(Long matchCompetitorId, MatchCompetitorPatchRequest request) {
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        matchCompetitorMapper.applyPatchFields(matchCompetitor, request);

        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorResponse getMatchCompetitor(Long matchCompetitorId) {
        return toResponse(findMatchCompetitorOrThrow(matchCompetitorId));
    }

    @Override
    public List<MatchCompetitorResponse> getAllMatchCompetitors() {
        return matchCompetitorRepository.findAllWithCompetitorAndMatch().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteMatchCompetitor(Long matchCompetitorId) {
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        // TransactionService flushes the delete before committing, so a reference from another
        // record surfaces here and is reported as a 400, not a 500.
        try {
            transactionService.deleteMatchCompetitor(matchCompetitor);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Match competitor with ID " + matchCompetitorId
                    + " cannot be deleted: it is referenced by other records.", e);
        }
    }

    /**
     * Reads match competitor data from a CSV-formatted string and converts it into a list of
     * {@link MatchCompetitorRequest} objects, binding the CSV column headers onto each through
     * {@link MatchCompetitorRequestCsvMixIn}. A header may omit optional columns and unknown columns are ignored.
     *
     * @param csvData the CSV data containing match competitor information, one match competitor per row.
     *                Must not be null or blank.
     * @return a list of {@link MatchCompetitorRequest} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<MatchCompetitorRequest> readMatchCompetitors(@NonNull @NotBlank String csvData)
            throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        // The columns come from the header row, so the UpperCamelCase names bound by the mix-in are matched directly
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();
        csvMapper.addMixIn(MatchCompetitorRequest.class, MatchCompetitorRequestCsvMixIn.class);

        try (MappingIterator<MatchCompetitorRequest> requestMappingIterator =
                     csvMapper.readerFor(MatchCompetitorRequest.class)
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
     * Saves a match competitor, first refusing one that would duplicate another entry for the same competitor,
     * match and firearm type.
     *
     * @param matchCompetitor the match competitor to save; must not be null.
     * @return the saved match competitor.
     * @throws ValidationException if the competitor already has another entry for the match and firearm type,
     *                             including one added by another transaction since the check.
     */
    protected MatchCompetitor save(@NonNull MatchCompetitor matchCompetitor) {
        boolean duplicate = matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(
                        matchCompetitor.getCompetitor().getId(), matchCompetitor.getMatch().getId(),
                        matchCompetitor.getFirearmType())
                .filter(existing -> !existing.getId().equals(matchCompetitor.getId()))
                .isPresent();
        if (duplicate) {
            throw duplicateEntry(matchCompetitor, null);
        }

        try {
            return transactionService.saveMatchCompetitor(matchCompetitor);
        } catch (DataIntegrityViolationException e) {
            throw duplicateEntry(matchCompetitor, e);
        }
    }

    /**
     * Builds the exception reported for a duplicate competitor, match and firearm type entry.
     *
     * @param matchCompetitor the match competitor that would be a duplicate.
     * @param cause           the underlying exception, or {@code null} when found by the check before saving.
     * @return the {@link ValidationException} to throw.
     */
    protected ValidationException duplicateEntry(MatchCompetitor matchCompetitor, Throwable cause) {
        String message = String.format("Competitor %d already has a %s entry for match %d.",
                matchCompetitor.getCompetitor().getId(), matchCompetitor.getFirearmType(),
                matchCompetitor.getMatch().getId());
        return (cause != null) ? new ValidationException(message, cause) : new ValidationException(message);
    }

    /**
     * Builds the outcome reported for a row that could not be imported: the message and every value the row supplied,
     * with missing ones as empty strings.
     *
     * <p>
     * The outcome carries no {@link MatchCompetitorResponse}, because a response is only built for a match
     * competitor that has every required field, which a failed row by definition lacks. The row's values are in
     * {@link MatchCompetitorBulkResponse#getMatchCompetitorRow()} instead. Nothing is resolved or looked up, so this never throws,
     * whatever is missing or unknown in the row.
     * </p>
     *
     * @param message         the reason the row was not imported.
     * @param request         the request for the row; may be null, as for an empty row, in which case every value is empty.
     * @param matchCompetitor the match competitor populated as far as the row could be resolved, whose resolved
     *                        values are reported in place of the row's text; may be null, as when nothing was resolved.
     * @return an unsuccessful {@link MatchCompetitorBulkResponse} with the message, a {@code null} match competitor
     * and the row's values.
     */
    protected MatchCompetitorBulkResponse failedRow(String message, MatchCompetitorRequest request,
                                                    MatchCompetitor matchCompetitor) {
        return new MatchCompetitorBulkResponse(false, message, null,
                matchCompetitorRowMapper.toRow(request, matchCompetitor));
    }

    /**
     * Retrieves an existing match competitor or throws if none exists with the given ID.
     *
     * @param matchCompetitorId the identifier to look up.
     * @return the matching {@link MatchCompetitor}, with its competitor and match loaded.
     * @throws NonFatalException if no match competitor with {@code matchCompetitorId} exists.
     */
    protected MatchCompetitor findMatchCompetitorOrThrow(Long matchCompetitorId) {
        return matchCompetitorRepository.findByIdWithCompetitorAndMatch(matchCompetitorId)
                .orElseThrow(() -> new NonFatalException("No match competitor found with ID " + matchCompetitorId));
    }

    /**
     * Checks whether a bulk import row is for the club being imported.
     *
     * <p>
     * The checks run in order, and the first that succeeds decides:
     * </p>
     * <ol>
     *     <li>With no target club there is nothing to filter on, so every row matches, and the row isn't inspected at
     *     all, not even for an unknown {@code matchClub}.</li>
     *     <li>The row's {@code matchClub}, by name or abbreviation, is the target club.</li>
     *     <li>Failing that, the home club of the competitor the row identifies is the target club. The competitor is
     *     resolved as by {@link MatchCompetitorMapper#resolveCompetitor(Long, String, String)}: by ID when the row has one, otherwise by
     *     number and name. This is only looked up when the {@code matchClub} check fails.</li>
     * </ol>
     *
     * <p>
     * A row whose competitor can't be resolved (none found, several found, or no ID, number or name given) is simply
     * not for the club, rather than an error, so the import reports that row as skipped. The same goes for a
     * competitor with no home club.
     * </p>
     *
     * @param request    the row to check.
     * @param targetClub the club being imported; may be null, in which case every row matches.
     * @return {@code true} if {@code targetClub} is null, the row's {@code matchClub} resolves to {@code targetClub},
     * or the row's competitor has {@code targetClub} as their home club; {@code false} otherwise.
     * @throws ValidationException if {@code targetClub} is not null and the row's {@code matchClub} was supplied but
     *                             isn't a known club.
     */
    protected boolean isForClub(MatchCompetitorRequest request, ClubIdentifier targetClub) {
        if (targetClub == null) {
            return true;
        }

        // Test the competitor in the match's club against the target club
        if (ipscEntityClubService.isSameClub(matchCompetitorMapper.resolveMatchClub(request.getMatchClub()), targetClub)) {
            return true;
        }

        // Test the competitor's home club against the target club
        try {
            return ipscEntityClubService.isSameClub(resolveCompetitorHomeClub(request), targetClub);
        } catch (ValidationException | NonFatalException e) {
            return false;
        }
    }

    /**
     * Resolves the home club of the competitor a request refers to.
     *
     * <p>
     * The counterpart of {@link MatchCompetitorMapper#resolveMatchClub(String)} for the competitor's own club rather than the club they
     * represented at the match: the competitor is resolved as by {@link MatchCompetitorMapper#resolveCompetitor(Long, String, String)},
     * and their home club's identifier is returned, or {@code null} when they have no home club.
     * </p>
     *
     * @param request the request whose competitor is to be resolved, by ID, or else by number and name.
     * @return the {@link ClubIdentifier} of the competitor's home club, or {@code null} if the competitor has no
     * home club, or the home club has no identifier.
     * @throws ValidationException if the request's competitor ID, number and name are all null or blank, or if more
     *                             than one competitor matches the number and name.
     * @throws NonFatalException   if no competitor matches.
     */
    protected @Nullable ClubIdentifier resolveCompetitorHomeClub(MatchCompetitorRequest request) {
        Competitor competitor = matchCompetitorMapper.resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                request.getCompetitorName());
        Club homeClub = competitor.getHomeClub();
        return (homeClub == null) ? null : homeClub.getIdentifier();
    }

    /**
     * Validates that a request carries every field required to create or fully replace a match competitor.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(MatchCompetitorRequest request) {
        if (request == null) {
            throw new ValidationException("Match competitor request cannot be null.");
        }
        request.validate();
    }

    /**
     * Maps a persisted match competitor to the response shape returned by the controller.
     *
     * @param matchCompetitor the match competitor to map, with its competitor and match loaded.
     * @return the mapped {@link MatchCompetitorResponse}.
     * @throws ValidationException if the match competitor violates a constraint, such as a missing required field;
     *                             every violation is named in the message.
     */
    protected MatchCompetitorResponse toResponse(MatchCompetitor matchCompetitor) {
        return new MatchCompetitorResponse(matchCompetitor);
    }
}
