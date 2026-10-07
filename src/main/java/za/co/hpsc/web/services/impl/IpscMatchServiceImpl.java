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
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.mappers.MatchMapper;
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogRepository;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static za.co.hpsc.web.utils.StringUtil.hasText;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    private final IpscMatchRepository ipscMatchRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final ShooterLogRepository shooterLogRepository;
    private final ShooterLogCompetitorRepository shooterLogCompetitorRepository;
    private final MatchMapper matchMapper;
    private final TransactionService transactionService;

    public IpscMatchServiceImpl(IpscMatchRepository ipscMatchRepository,
                                 MatchCompetitorRepository matchCompetitorRepository,
                                 ShooterLogRepository shooterLogRepository,
                                 ShooterLogCompetitorRepository shooterLogCompetitorRepository,
                                 MatchMapper matchMapper,
                                 TransactionService transactionService) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.shooterLogRepository = shooterLogRepository;
        this.shooterLogCompetitorRepository = shooterLogCompetitorRepository;
        this.matchMapper = matchMapper;
        this.transactionService = transactionService;
    }

    @Override
    public MatchResponse createMatch(MatchRequest request) throws FatalException {
        return toResponse(transactionService.saveMatch(newMatch(request)));
    }

    @Override
    public MatchResponseHolder createMatches(String csvData) throws FatalException {
        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<MatchRequest> matchRequests = readMatches(csvData);

        // Every row is validated and built before any is saved, then all are saved in one
        // transaction, so a bad row leaves none of them persisted.
        List<IpscMatch> matches = new ArrayList<>();
        for (MatchRequest matchRequest : matchRequests) {
            matches.add(newMatch(matchRequest));
        }

        List<MatchResponse> matchResponseList = transactionService.saveMatches(matches).stream()
                .map(this::toResponse)
                .toList();
        return new MatchResponseHolder(new ArrayList<>(matchResponseList));
    }

    @Override
    public MatchResponse updateMatch(Long matchId, MatchRequest request) throws FatalException {
        validateForCreate(request);
        IpscMatch match = findMatchOrThrow(matchId);

        matchMapper.applyFields(match, request);
        return toResponse(transactionService.saveMatch(match));
    }

    @Override
    public MatchResponse patchMatch(Long matchId, MatchPatchRequest request) throws FatalException {
        IpscMatch match = findMatchOrThrow(matchId);

        matchMapper.applyPatchFields(match, request);

        return toResponse(transactionService.saveMatch(match));
    }

    @Override
    public MatchResponse getMatch(Long matchId) {
        return toResponse(findMatchOrThrow(matchId));
    }

    @Override
    public List<MatchResponse> getAllMatches() {
        return ipscMatchRepository.findAllWithClub().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteMatch(Long matchId) {
        IpscMatch match = findMatchOrThrow(matchId);

        if (matchCompetitorRepository.existsByMatchId(matchId)) {
            throw new ValidationException("Match with ID " + matchId
                    + " cannot be deleted: it has recorded competitor results.");
        }
        if (shooterLogRepository.existsByMatchesId(matchId)
                || shooterLogCompetitorRepository.existsByMatchCompetitorMatchId(matchId)) {
            throw new ValidationException("Match with ID " + matchId
                    + " cannot be deleted: it is referenced by shooter logs.");
        }

        // TransactionService flushes the delete before committing, so a reference
        // added by another transaction since the checks above surfaces here and is reported as a
        // 400, not a 500.
        try {
            transactionService.deleteMatch(match);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Match with ID " + matchId
                    + " cannot be deleted: it is referenced by other records.", e);
        }
    }

    /**
     * Validates a request and builds the new, not yet persisted, match it describes.
     *
     * @param request the match to build. Must carry a match name, date and firearm
     *                type/category.
     * @return the new match, with a {@code null} ID.
     * @throws ValidationException if a required field is missing, or the firearm type/category
     *                             doesn't match a known {@link FirearmType}/{@link MatchCategory}.
     * @throws NonFatalException   if the request's club name doesn't match an existing club, or no
     *                             club exists for {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @throws FatalException      if {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is null.
     */
    protected IpscMatch newMatch(MatchRequest request) throws FatalException {
        validateForCreate(request);

        IpscMatch match = new IpscMatch();
        matchMapper.applyFields(match, request);
        return match;
    }

    /**
     * Reads match data from a CSV-formatted string and converts it into a list of
     * {@link MatchRequest} objects, binding the CSV column headers onto each through
     * {@link MatchRequestCsvMixIn}. A header may omit optional columns and unknown columns are ignored.
     *
     * @param csvData the CSV data containing match information, one match per row. Must not be
     *                null or blank.
     * @return a list of {@link MatchRequest} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<MatchRequest> readMatches(@NotNull @NotBlank String csvData) throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        // The columns come from the header row, so the UpperCamelCase names bound by the mix-in are matched directly
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        csvMapper.addMixIn(MatchRequest.class, MatchRequestCsvMixIn.class);

        // Read the CSV data using the mapper and schema
        try (MappingIterator<MatchRequest> requestMappingIterator =
                     csvMapper.readerFor(MatchRequest.class)
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
     * Retrieves an existing match or throws if none exists with the given ID.
     *
     * @param matchId the identifier to look up.
     * @return the matching {@link IpscMatch}.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    protected IpscMatch findMatchOrThrow(Long matchId) {
        return ipscMatchRepository.findByIdWithClub(matchId)
                .orElseThrow(() -> new NonFatalException("No IPSC match found with ID " + matchId));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * match: a name, date and firearm type. The club and match category are not required, as each
     * defaults when omitted (see {@link MatchMapper#resolveClub(String)} and {@link MatchMapper#resolveMatchCategory(String)}).
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(MatchRequest request) {
        if (request == null) {
            throw new ValidationException("Match request cannot be null.");
        }
        request.validate();
    }

    /**
     * Maps a match to the response shape returned by the controller.
     *
     * @param match the match to map.
     * @return the mapped {@link MatchResponse}.
     */
    protected MatchResponse toResponse(IpscMatch match) {
        return new MatchResponse(match);
    }
}
