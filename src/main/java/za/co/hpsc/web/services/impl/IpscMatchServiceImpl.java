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
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequestForCSV;
import za.co.hpsc.web.models.ipsc.match.request.MatchStageRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
import za.co.hpsc.web.models.ipsc.match.response.MatchStageResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogCompetitorRepository;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransactionService.StageSaveMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    private final IpscMatchRepository ipscMatchRepository;
    private final IpscMatchStageRepository ipscMatchStageRepository;
    private final ClubRepository clubRepository;
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final MatchStageCompetitorRepository matchStageCompetitorRepository;
    private final ShooterLogCompetitorRepository shooterLogCompetitorRepository;
    private final TransactionService transactionService;

    public IpscMatchServiceImpl(IpscMatchRepository ipscMatchRepository,
                                 IpscMatchStageRepository ipscMatchStageRepository,
                                 ClubRepository clubRepository,
                                 MatchCompetitorRepository matchCompetitorRepository,
                                 MatchStageCompetitorRepository matchStageCompetitorRepository,
                                 ShooterLogCompetitorRepository shooterLogCompetitorRepository,
                                 TransactionService transactionService) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.clubRepository = clubRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
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

        List<MatchRequestForCSV> matchRequestForCSVList = readMatches(csvData);

        // Every row is validated and built before any is saved, then all are saved in one
        // transaction, so a bad row leaves none of them persisted.
        List<IpscMatch> matches = new ArrayList<>();
        for (MatchRequestForCSV matchRequestForCSV : matchRequestForCSVList) {
            matches.add(newMatch(toRequest(matchRequestForCSV)));
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
        return toResponse(transactionService.saveMatch(match, toStages(request.getStages()), StageSaveMode.REPLACE));
    }

    @Override
    public MatchResponse patchMatch(Long matchId, MatchRequest request) throws FatalException {
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

        IpscMatch saved = (request.getStages() != null)
                ? transactionService.saveMatch(match, toStages(request.getStages()), StageSaveMode.UPSERT)
                : transactionService.saveMatch(match);
        return toResponse(saved);
    }

    @Override
    public MatchResponse getMatch(Long matchId) {
        IpscMatch match = findMatchOrThrow(matchId);
        List<IpscMatchStage> stages = ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(matchId);
        return toResponse(match, stages);
    }

    @Override
    public List<MatchResponse> getAllMatches() {
        return ipscMatchRepository.findAllWithClub().stream()
                .map(match -> toResponse(match,
                        ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(match.getId())))
                .toList();
    }

    @Override
    public void deleteMatch(Long matchId) {
        IpscMatch match = findMatchOrThrow(matchId);

        if (matchCompetitorRepository.existsByMatchId(matchId)
                || matchStageCompetitorRepository.existsByMatchStageMatchId(matchId)) {
            throw new ValidationException("Match with ID " + matchId
                    + " cannot be deleted: it has recorded competitor results.");
        }
        if (shooterLogCompetitorRepository.existsByMatchId(matchId)) {
            throw new ValidationException("Match with ID " + matchId
                    + " cannot be deleted: it is referenced by shooter logs.");
        }

        // TransactionService flushes the delete, stages included, before committing, so a reference
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
     * Validates a request and builds the new, not yet persisted, match it describes, with the
     * request's stages on its {@link IpscMatch#getStages() stages} collection.
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
        List<IpscMatchStage> stages = toStages(request.getStages());
        if (stages != null) {
            for (IpscMatchStage stage : stages) {
                stage.setMatch(match);
                match.getStages().add(stage);
            }
        }
        return match;
    }

    /**
     * Maps a request's stages onto new, not yet persisted, {@link IpscMatchStage}s.
     *
     * @param stageRequests the stages to map; may be null.
     * @return the equivalent stages, in the order given, or {@code null} if {@code stageRequests}
     * is null.
     */
    protected List<IpscMatchStage> toStages(List<MatchStageRequest> stageRequests) {
        if (stageRequests == null) {
            return null;
        }

        return stageRequests.stream()
                .map(stageRequest -> {
                    IpscMatchStage stage = new IpscMatchStage();
                    stage.setStageNumber(stageRequest.getStageNumber());
                    stage.setStageName(stageRequest.getStageName());
                    return stage;
                })
                .toList();
    }

    /**
     * Reads match data from a CSV-formatted string and converts it into a list of
     * {@link MatchRequestForCSV} objects.
     *
     * @param csvData the CSV data containing match information, one match per row. Must not be
     *                null or blank.
     * @return a list of {@link MatchRequestForCSV} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data cannot be parsed.
     * @throws FatalException      if an I/O error occurs while reading the CSV data.
     */
    protected List<MatchRequestForCSV> readMatches(@NotNull @NotBlank String csvData) throws FatalException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        CsvSchema csvSchema = csvMapper
                .schemaFor(MatchRequestForCSV.class)
                .withColumnReordering(true)
                .withHeader();

        try (MappingIterator<MatchRequestForCSV> requestMappingIterator =
                     csvMapper.readerFor(MatchRequestForCSV.class)
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
     * Maps a {@link MatchRequestForCSV} row onto a {@link MatchRequest}.
     *
     * @param matchRequestForCSV the CSV row to map; must not be null.
     * @return the equivalent {@link MatchRequest}, with a {@code null} {@code matchId} and its
     * {@code Stages} cell parsed into {@link MatchStageRequest}s.
     */
    protected MatchRequest toRequest(@NotNull MatchRequestForCSV matchRequestForCSV) {
        return new MatchRequest(
                null,
                matchRequestForCSV.getMatchDate(),
                matchRequestForCSV.getMatchName(),
                matchRequestForCSV.getClub(), matchRequestForCSV.getMatchFirearmType(), matchRequestForCSV.getMatchCategory(), parseStages(matchRequestForCSV.getStages()), matchRequestForCSV.getStartTime(),
                matchRequestForCSV.getEndTime(),
                matchRequestForCSV.getUrl()
        );
    }

    /**
     * Parses a CSV cell of semicolon-separated {@code <stageNumber>:<stageName>} entries into a
     * list of {@link MatchStageRequest}s.
     *
     * @param rawStages the raw CSV cell value (e.g. {@code "1:Stage One;2:Stage Two"}); may be
     *                  null or blank, in which case an empty list is returned.
     * @return the individual stages, in the order given.
     * @throws ValidationException if an entry doesn't start with a numeric stage number followed
     *                             by a {@code ":"}.
     */
    protected List<MatchStageRequest> parseStages(String rawStages) {
        if ((rawStages == null) || rawStages.isBlank()) {
            return new ArrayList<>();
        }

        List<MatchStageRequest> stages = new ArrayList<>();
        for (String entry : rawStages.split(SystemConstants.ARRAY_SEPARATOR)) {
            if (entry.isBlank()) {
                continue;
            }

            int separatorIndex = entry.indexOf(':');
            if (separatorIndex < 0) {
                throw new ValidationException("Invalid stage entry (expected <stageNumber>:<stageName>): " + entry);
            }

            String stageNumber = entry.substring(0, separatorIndex).trim();
            String stageName = entry.substring(separatorIndex + 1).trim();
            try {
                stages.add(new MatchStageRequest(null, Integer.valueOf(stageNumber), stageName));
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid stage number in entry: " + entry, e);
            }
        }

        return stages;
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
     * Maps a saved match, together with the stages on its {@link IpscMatch#getStages() stages}
     * collection, to the response shape returned by the controller.
     *
     * @param match the match to map; its stages must already be loaded.
     * @return the mapped {@link MatchResponse}, with its stages ordered by stage number.
     */
    protected MatchResponse toResponse(IpscMatch match) {
        return toResponse(match, match.getStages().stream()
                .sorted(Comparator.comparing(IpscMatchStage::getStageNumber))
                .toList());
    }

    /**
     * Maps a persisted match and its stages to the response shape returned by the controller.
     *
     * @param match  the match to map.
     * @param stages the match's persisted stages.
     * @return the mapped {@link MatchResponse}.
     */
    protected MatchResponse toResponse(IpscMatch match, List<IpscMatchStage> stages) {
        List<MatchStageResponse> stageResponses = stages.stream()
                .map(stage -> new MatchStageResponse(stage.getId(), stage.getStageNumber(), stage.getStageName()))
                .toList();

        return new MatchResponse(
                match.getId(),
                match.getName(),
                match.getScheduledDate().toLocalDate(),
                match.getStartTime(),
                match.getEndTime(),
                ((match.getClub() != null) ? match.getClub().getIdentifier() : null),
                match.getMatchFirearmType(),
                match.getMatchCategory(),
                match.getUrl(),
                stageResponses);
    }
}
