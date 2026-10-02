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
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogCompetitorRepository;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    private final IpscMatchRepository ipscMatchRepository;
    private final ClubRepository clubRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final ShooterLogCompetitorRepository shooterLogCompetitorRepository;
    private final TransactionService transactionService;

    public IpscMatchServiceImpl(IpscMatchRepository ipscMatchRepository,
                                 ClubRepository clubRepository,
                                 MatchCompetitorRepository matchCompetitorRepository,
                                 ShooterLogCompetitorRepository shooterLogCompetitorRepository,
                                 TransactionService transactionService) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.clubRepository = clubRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.shooterLogCompetitorRepository = shooterLogCompetitorRepository;
        this.transactionService = transactionService;
    }

    @Override
    public MatchResponse createMatch(MatchRequest request) throws FatalException {
        return toResponse(transactionService.saveMatch(newMatch(request)));
    }

    @Override
    public MatchResponseHolder createMatches(String csvData) throws FatalException {
        if (csvData == null || csvData.isBlank()) {
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

        applyFields(match, request);
        return toResponse(transactionService.saveMatch(match));
    }

    @Override
    public MatchResponse patchMatch(Long matchId, MatchPatchRequest request) throws FatalException {
        IpscMatch match = findMatchOrThrow(matchId);

        if (request.getClub() != null) {
            match.setClub(resolveClub(request.getClub()));
        }
        if (request.getMatchName() != null) {
            match.setName(request.getMatchName());
        }
        if (request.getMatchDate() != null) {
            match.setScheduledDate(request.getMatchDate().atStartOfDay());
        }
        if (request.getStartTime() != null) {
            match.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            match.setEndTime(request.getEndTime());
        }
        if (request.getMatchFirearmType() != null) {
            match.setMatchFirearmType(resolveFirearmType(request.getMatchFirearmType()));
        }
        if (request.getMatchCategory() != null) {
            match.setMatchCategory(resolveMatchCategory(request.getMatchCategory()));
        }
        if (request.getUrl() != null) {
            match.setUrl(request.getUrl());
        }

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
        if (shooterLogCompetitorRepository.existsByMatchCompetitorMatchId(matchId)) {
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
        applyFields(match, request);
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
     * Copies the match-level fields of a {@link MatchRequest} onto an {@link IpscMatch},
     * resolving the named club in the process, defaulting to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} when none is supplied.
     *
     * @param match   the entity to populate; must not be null.
     * @param request the request carrying the field values; must not be null.
     * @throws NonFatalException if the request's club name doesn't match an existing club, or no
     *                           club exists for {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @throws FatalException    if {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is null.
     */
    protected void applyFields(@NotNull IpscMatch match, @NotNull MatchRequest request) throws FatalException {
        match.setClub(resolveClub(request.getClub()));
        match.setName(request.getMatchName());
        match.setScheduledDate(request.getMatchDate().atStartOfDay());
        match.setStartTime(request.getStartTime());
        match.setEndTime(request.getEndTime());
        match.setMatchFirearmType(resolveFirearmType(request.getMatchFirearmType()));
        match.setMatchCategory(resolveMatchCategory(request.getMatchCategory()));
        match.setUrl(request.getUrl());
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
     * Resolves a club by name, defaulting to {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}
     * when none is supplied.
     *
     * @param clubName the club name to look up; may be null or blank, in which case the default
     *                 match club identifier is resolved instead.
     * @return the matching {@link Club}.
     * @throws NonFatalException if {@code clubName} was supplied but doesn't match an existing
     *                           club, or if no club exists for
     *                           {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @throws FatalException    if {@code clubName} wasn't supplied and
     *                           {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is null.
     */
    protected Club resolveClub(String clubName) throws FatalException {
        return resolveClub(clubName, IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
    }

    /**
     * Resolves a club by name, defaulting to {@code defaultIdentifier} when none is supplied.
     *
     * <p>
     * {@code defaultIdentifier} is taken as a parameter, rather than read directly from
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} in this method, purely so this check
     * stays unit testable if that constant were ever null (which cannot happen with today's
     * value, but which this method is deliberately written to guard against rather than
     * silently mishandle).
     * </p>
     *
     * @param clubName          the club name to look up; may be null or blank, in which case
     *                          {@code defaultIdentifier} is resolved instead.
     * @param defaultIdentifier the identifier to resolve when {@code clubName} isn't supplied;
     *                          may be null.
     * @return the matching {@link Club}.
     * @throws NonFatalException if {@code clubName} was supplied but doesn't match an existing
     *                           club, or if no club exists for {@code defaultIdentifier}.
     * @throws FatalException    if {@code clubName} wasn't supplied and {@code defaultIdentifier}
     *                           is null.
     */
    protected Club resolveClub(String clubName, ClubIdentifier defaultIdentifier) throws FatalException {
        if ((clubName == null) || clubName.isBlank()) {
            if (defaultIdentifier == null) {
                throw new FatalException("IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER is not configured.");
            }

            return clubRepository.findByIdentifier(defaultIdentifier)
                    .orElseThrow(() -> new NonFatalException("No club found with identifier " + defaultIdentifier));
        }

        return clubRepository.findByName(clubName)
                .orElseThrow(() -> new NonFatalException("No club found with name " + clubName));
    }

    /**
     * Resolves a firearm type by name.
     *
     * @param firearmType the firearm type name to look up.
     * @return the matching {@link FirearmType}.
     * @throws ValidationException if no firearm type matches {@code firearmType}.
     */
    protected FirearmType resolveFirearmType(String firearmType) {
        return FirearmType.fromName(firearmType)
                .orElseThrow(() -> new ValidationException("Unknown match firearm type: " + firearmType));
    }

    /**
     * Resolves a match category by name.
     *
     * @param matchCategory the match category name to look up.
     * @return the matching {@link MatchCategory}.
     * @throws ValidationException if no match category matches {@code matchCategory}.
     */
    protected MatchCategory resolveMatchCategory(String matchCategory) {
        return MatchCategory.fromName(matchCategory)
                .orElseThrow(() -> new ValidationException("Unknown match category: " + matchCategory));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * match.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(MatchRequest request) {
        if (request == null) {
            throw new ValidationException("Match request cannot be null.");
        }
        if ((request.getMatchName() == null) || request.getMatchName().isBlank()) {
            throw new ValidationException("Match name is required.");
        }
        if (request.getMatchDate() == null) {
            throw new ValidationException("Match date is required.");
        }
        if ((request.getMatchFirearmType() == null) || request.getMatchFirearmType().isBlank()) {
            throw new ValidationException("Match firearm type is required.");
        }
        if ((request.getMatchCategory() == null) || request.getMatchCategory().isBlank()) {
            throw new ValidationException("Match category is required.");
        }
    }

    /**
     * Maps a match to the response shape returned by the controller.
     *
     * @param match the match to map.
     * @return the mapped {@link MatchResponse}.
     */
    protected MatchResponse toResponse(IpscMatch match) {
        return new MatchResponse(
                match.getId(),
                match.getName(),
                match.getScheduledDate().toLocalDate(),
                match.getStartTime(),
                match.getEndTime(),
                ((match.getClub() != null) ? match.getClub().getIdentifier() : null),
                match.getMatchFirearmType(),
                match.getMatchCategory(),
                match.getUrl());
    }
}
