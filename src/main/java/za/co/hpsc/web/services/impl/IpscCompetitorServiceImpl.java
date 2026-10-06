package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.services.IpscCompetitorService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static za.co.hpsc.web.utils.StringUtil.hasText;
import static za.co.hpsc.web.utils.StringUtil.toProperCase;

@Slf4j
@Service
public class IpscCompetitorServiceImpl implements IpscCompetitorService {
    private final CompetitorRepository competitorRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final ShooterLogCompetitorRepository shooterLogCompetitorRepository;
    private final ShooterLogOverallRepository shooterLogOverallRepository;

    private final CompetitorMapper competitorMapper;
    private final TransactionService transactionService;

    public IpscCompetitorServiceImpl(CompetitorRepository competitorRepository,
                                     MatchCompetitorRepository matchCompetitorRepository,
                                     ShooterLogCompetitorRepository shooterLogCompetitorRepository,
                                     ShooterLogOverallRepository shooterLogOverallRepository,
                                     CompetitorMapper competitorMapper,
                                     TransactionService transactionService) {
        this.competitorRepository = competitorRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.shooterLogCompetitorRepository = shooterLogCompetitorRepository;
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

        // Every row is validated and built before any is saved, then all are saved in one
        // transaction, so a bad row leaves none of them persisted.
        List<Competitor> competitors = new ArrayList<>();
        for (CompetitorRequest competitorRequest : competitorRequests) {
            competitors.add(newCompetitor(normaliseCsvRequest(competitorRequest)));
        }

        List<CompetitorResponse> competitorResponseList = transactionService.saveCompetitors(competitors).stream()
                .map(this::toResponse)
                .toList();
        return new CompetitorResponseHolder(new ArrayList<>(competitorResponseList));
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
        if (shooterLogCompetitorRepository.existsByCompetitorId(competitorId)
                || shooterLogOverallRepository.existsByCompetitorId(competitorId)) {
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
    protected List<CompetitorRequest> readCompetitors(@NotNull @NotBlank String csvData)
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
     * Normalises a {@link CompetitorRequest} read from a CSV row into a new {@link CompetitorRequest}, dropping any
     * {@code competitorId}.
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
    protected CompetitorRequest normaliseCsvRequest(@NotNull CompetitorRequest csvRow) {
        return new CompetitorRequest(
                csvRow.getCompetitorId(),
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
