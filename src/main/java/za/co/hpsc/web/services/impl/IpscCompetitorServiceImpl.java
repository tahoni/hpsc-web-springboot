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
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.CompetitorHelpers;
import za.co.hpsc.web.mappers.CompetitorMapper;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorVerifyRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorBulkResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorVerifyResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorVerifyResponseHolder;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.services.IpscCompetitorService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static za.co.hpsc.web.utils.StringUtil.hasText;
import static za.co.hpsc.web.utils.StringUtil.toProperCase;

@Slf4j
@Service
public class IpscCompetitorServiceImpl implements IpscCompetitorService {
    private final CompetitorRepository competitorRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final ShooterLogOverallRepository shooterLogOverallRepository;

    private final CompetitorMapper competitorMapper;
    private final TransactionService transactionService;

    public IpscCompetitorServiceImpl(CompetitorRepository competitorRepository,
                                     MatchCompetitorRepository matchCompetitorRepository,
                                     ShooterLogOverallRepository shooterLogOverallRepository,
                                     CompetitorMapper competitorMapper,
                                     TransactionService transactionService) {
        this.competitorRepository = competitorRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.shooterLogOverallRepository = shooterLogOverallRepository;
        this.competitorMapper = competitorMapper;
        this.transactionService = transactionService;
    }

    @Override
    public CompetitorResponse createCompetitor(CompetitorRequest request) {
        return toResponse(transactionService.saveCompetitor(newCompetitor(request)));
    }

    @Override
    public CompetitorResponseHolder createCompetitors(String csvData)
            throws FatalException {

        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<CompetitorRequest> competitorRequests = readCompetitors(csvData);

        // Each row is saved in its own transaction, so a bad row is reported and skipped without affecting the
        // others. A row for a competitor that already exists updates and verifies that competitor instead of
        // creating another.
        List<CompetitorResponse> competitorResponses = new ArrayList<>();
        List<CompetitorBulkResponse> results = new ArrayList<>();
        // The club numbers imported so far and the row each came from, so a later row repeating one is refused
        // rather than silently updating the competitor an earlier row of the same file just saved
        Map<String, Integer> importedClubNumbers = new HashMap<>();
        int rowNumber = 0;
        for (CompetitorRequest competitorRequest : competitorRequests) {
            rowNumber++;
            try {
                CompetitorRequest normalised = normaliseCsvRequest(competitorRequest);
                Competitor competitor = newCompetitor(normalised);
                Integer earlierRow = hasText(competitor.getClubNumber())
                        ? importedClubNumbers.get(competitor.getClubNumber()) : null;
                if (earlierRow != null) {
                    throw new ValidationException("Club number " + competitor.getClubNumber()
                            + " was already imported by row " + earlierRow + " of this file.");
                }
                String message = "";
                Optional<Competitor> existing = findExistingCompetitor(competitor);
                if (existing.isPresent()) {
                    competitor = existing.get();
                    competitorMapper.applyFields(competitor, normalised);
                    competitor.setIsVerified(true);
                    message = "Updated and verified the existing competitor with ID " + competitor.getId() + ".";
                }
                CompetitorResponse saved = toResponse(transactionService.saveCompetitor(competitor));
                if (hasText(saved.getClubNumber())) {
                    importedClubNumbers.put(saved.getClubNumber(), rowNumber);
                }
                competitorResponses.add(saved);
                results.add(new CompetitorBulkResponse(rowNumber, true, message, saved));
            } catch (ValidationException | NonFatalException e) {
                log.warn("Competitor row {} skipped due to error: {}", rowNumber, e.getMessage());
                results.add(failedRow(rowNumber, e.getMessage()));
            } catch (DataIntegrityViolationException e) {
                log.warn("Competitor row {} skipped due to a data integrity violation: {}", rowNumber,
                        e.getMessage());
                results.add(failedRow(rowNumber, "The competitor conflicts with an existing record."));
            }
        }

        return new CompetitorResponseHolder(competitorResponses, results);
    }

    /**
     * Builds the outcome reported for a bulk import row that could not be imported.
     *
     * @param rowNumber the row's position among the CSV's data rows, starting at 1.
     * @param message   the reason the row was not imported.
     * @return an unsuccessful {@link CompetitorBulkResponse} with the message and no competitor.
     */
    protected CompetitorBulkResponse failedRow(int rowNumber, String message) {
        return new CompetitorBulkResponse(rowNumber, false, message, null);
    }

    @Override
    public CompetitorVerifyResponseHolder verifyCompetitors(CompetitorVerifyRequest request) {
        if (request == null) {
            throw new ValidationException("Competitor verification request cannot be null.");
        }
        request.validate();

        List<Long> competitorIds = request.getCompetitorIds().stream().distinct().toList();
        Map<Long, Competitor> competitorsById = new HashMap<>();
        for (Competitor competitor : competitorRepository.findAllByIdInWithHomeClubAndEmailAddresses(competitorIds)) {
            competitorsById.put(competitor.getId(), competitor);
        }

        // Only competitors that are not already verified are written, all in one transaction
        List<Competitor> toVerify = new ArrayList<>();
        for (Long competitorId : competitorIds) {
            Competitor competitor = competitorsById.get(competitorId);
            if ((competitor != null) && !Boolean.TRUE.equals(competitor.getIsVerified())) {
                competitor.setIsVerified(true);
                toVerify.add(competitor);
            }
        }
        Set<Long> verifiedNow = new HashSet<>();
        if (!toVerify.isEmpty()) {
            for (Competitor saved : transactionService.saveCompetitors(toVerify)) {
                competitorsById.put(saved.getId(), saved);
                verifiedNow.add(saved.getId());
            }
        }

        List<CompetitorVerifyResponse> results = new ArrayList<>();
        for (Long competitorId : competitorIds) {
            Competitor competitor = competitorsById.get(competitorId);
            if (competitor == null) {
                results.add(new CompetitorVerifyResponse(competitorId, false,
                        "No competitor found with ID " + competitorId, null));
            } else {
                String message = verifiedNow.contains(competitorId) ? "" : "Already verified.";
                results.add(new CompetitorVerifyResponse(competitorId, true, message, toResponse(competitor)));
            }
        }
        return new CompetitorVerifyResponseHolder(results);
    }

    @Override
    public CompetitorResponse updateCompetitor(Long competitorId, CompetitorRequest request) {
        validateForCreate(request);
        Competitor competitor = findCompetitorOrThrow(competitorId);

        competitorMapper.applyFields(competitor, request);
        return toResponse(transactionService.saveCompetitor(competitor));
    }

    @Override
    public CompetitorResponse patchCompetitor(Long competitorId, CompetitorPatchRequest request) {
        Competitor competitor = findCompetitorOrThrow(competitorId);

        competitorMapper.applyPatchFields(competitor, request);
        return toResponse(transactionService.saveCompetitor(competitor));
    }

    @Override
    public CompetitorResponse getCompetitor(Long competitorId) {
        return toResponse(findCompetitorOrThrow(competitorId));
    }

    @Override
    public List<CompetitorResponse> getAllCompetitors() {
        return competitorRepository.findAllWithHomeClubAndEmailAddresses().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteCompetitor(Long competitorId) {
        Competitor competitor = findCompetitorOrThrow(competitorId);

        if (matchCompetitorRepository.existsByCompetitorId(competitorId)) {
            throw new ValidationException("Competitor with ID " + competitorId
                    + " cannot be deleted: they have recorded match results.");
        }
        if (shooterLogOverallRepository.existsByCompetitorId(competitorId)) {
            throw new ValidationException("Competitor with ID " + competitorId
                    + " cannot be deleted: they have shooter logs.");
        }

        // TransactionService flushes the delete before committing, so a reference added by another
        // transaction since the checks above surfaces here and is reported as a 400, not a 500.
        try {
            transactionService.deleteCompetitor(competitor);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Competitor with ID " + competitorId
                    + " cannot be deleted: it is referenced by other records.", e);
        }
    }

    /**
     * Validates a request and builds the new, not yet persisted, competitor it describes.
     *
     * @param request the competitor to build. Must carry a first and last name.
     * @return the new competitor, with a {@code null} ID.
     * @throws ValidationException if a required field is missing or invalid (see
     *                             {@link CompetitorMapper#applyFields}).
     * @throws NonFatalException   if the request's home club name doesn't match an existing club.
     */
    protected Competitor newCompetitor(CompetitorRequest request) {
        validateForCreate(request);

        Competitor competitor = new Competitor();
        competitorMapper.applyFields(competitor, request);
        return competitor;
    }

    /**
     * Finds the competitor a bulk import row refers to: the one with the row's club number, which is unique. A row
     * without a club number, as every row for a home club other than HPSC's is, never refers to an existing
     * competitor, since neither a name nor an ID number is enough to tell two people apart.
     *
     * @param candidate the competitor built from the row, with its club number already resolved.
     * @return the existing competitor with its home club and email addresses loaded, or empty if none has the row's
     * club number and the row so creates a new one.
     */
    protected Optional<Competitor> findExistingCompetitor(Competitor candidate) {
        if (!hasText(candidate.getClubNumber())) {
            return Optional.empty();
        }
        return competitorRepository.findByClubNumber(candidate.getClubNumber())
                .flatMap(found -> competitorRepository.findByIdWithHomeClubAndEmailAddresses(found.getId()));
    }

    /**
     * Reads competitor data from a CSV-formatted string and converts it into a list of
     * {@link CompetitorRequest} objects, binding the CSV column headers onto each through
     * {@link CompetitorRequestCsvMixIn}. A header may omit optional columns and unknown columns are ignored.
     *
     * @param csvData the CSV data containing competitor information, one competitor per row.
     *                Must not be null or blank.
     * @return a list of {@link CompetitorRequest} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<CompetitorRequest> readCompetitors(@NonNull @NotBlank String csvData)
            throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        // The columns come from the header row, so the UpperCamelCase names bound by the mix-in are matched directly
        CsvSchema csvSchema = CsvSchema.emptySchema()
                .withArrayElementSeparator(SystemConstants.ARRAY_SEPARATOR)
                .withHeader();
        csvMapper.addMixIn(CompetitorRequest.class, CompetitorRequestCsvMixIn.class);

        try (MappingIterator<CompetitorRequest> requestMappingIterator =
                     csvMapper.readerFor(CompetitorRequest.class)
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
     * Normalises a {@link CompetitorRequest} read from a CSV row into a new {@link CompetitorRequest}, which has no
     * {@code competitorId} as a CSV row carries none.
     *
     * <p>
     * Every name column and the gender are proper-cased (see {@link za.co.hpsc.web.utils.StringUtil#toProperCase(String)}). The
     * home club name, which must match an existing club's name exactly, the competitor and club
     * numbers, which are codes (club numbers must also stay unique), the ID and cellphone numbers and the
     * email addresses are kept as supplied. The last name then gets surname casing (see
     * {@link CompetitorHelpers#toSentenceCaseLastName(String)}): particles are lower-cased, so "VAN DER MERWE" becomes
     * "van der Merwe", and a "Mc" prefix is corrected, so "MCDONALD" becomes "McDonald".
     * </p>
     *
     * @param csvRow the request read from the CSV row; must not be null.
     * @return the normalised {@link CompetitorRequest}, with a {@code null} {@code competitorId}.
     */
    protected CompetitorRequest normaliseCsvRequest(@NonNull CompetitorRequest csvRow) {
        return new CompetitorRequest(
                toProperCase(csvRow.getFirstName()),
                CompetitorHelpers.toSentenceCaseLastName(toProperCase(csvRow.getLastName())),
                toProperCase(csvRow.getMiddleNames()),
                toProperCase(csvRow.getNickName()),
                csvRow.getDateOfBirth(),
                toProperCase(csvRow.getGender()),
                csvRow.getHomeClub(),
                csvRow.getSapsaNumber(),
                csvRow.getCompetitorNumber(),
                csvRow.getClubNumber(),
                csvRow.getIdNumber(),
                csvRow.getCellphoneNumber(),
                new ArrayList<>(csvRow.getEmailAddresses()),
                csvRow.getPaidUpSapsa(),
                csvRow.getPaidUpNgpsa(),
                csvRow.getPaidUpClub(),
                csvRow.getIsVerified());
    }

    /**
     * Retrieves an existing competitor or throws if none exists with the given ID.
     *
     * @param competitorId the identifier to look up.
     * @return the matching {@link Competitor}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    protected Competitor findCompetitorOrThrow(Long competitorId) {
        return competitorRepository.findByIdWithHomeClubAndEmailAddresses(competitorId)
                .orElseThrow(() -> new NonFatalException("No competitor found with ID " + competitorId));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * competitor.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(CompetitorRequest request) {
        if (request == null) {
            throw new ValidationException("Competitor request cannot be null.");
        }
        request.validate();
    }

    /**
     * Maps a persisted competitor to the response shape returned by the controller.
     *
     * @param competitor the competitor to map.
     * @return the mapped {@link CompetitorResponse}.
     */
    protected CompetitorResponse toResponse(Competitor competitor) {
        return new CompetitorResponse(competitor);
    }
}
