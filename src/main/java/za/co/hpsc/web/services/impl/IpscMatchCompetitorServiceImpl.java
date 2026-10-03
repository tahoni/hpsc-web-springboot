package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponseHolder;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.IpscMatchCompetitorService;
import za.co.hpsc.web.services.TransactionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class IpscMatchCompetitorServiceImpl implements IpscMatchCompetitorService {
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final TransactionService transactionService;

    public IpscMatchCompetitorServiceImpl(MatchCompetitorRepository matchCompetitorRepository,
                                          CompetitorRepository competitorRepository,
                                          IpscMatchRepository ipscMatchRepository,
                                          TransactionService transactionService) {
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.transactionService = transactionService;
    }

    @Override
    public MatchCompetitorResponse createMatchCompetitor(MatchCompetitorRequest request) {
        validateForCreate(request);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        applyFields(matchCompetitor, request);
        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorResponseHolder createMatchCompetitors(String csvData) throws FatalException {
        if (csvData == null || csvData.isBlank()) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<MatchCompetitorRequest> requests = readMatchCompetitors(csvData);

        // Every row is validated and built before any is saved, then all are saved in one
        // transaction, so a bad row leaves none of them persisted.
        List<MatchCompetitor> matchCompetitors = new ArrayList<>();
        Set<List<Object>> entries = new HashSet<>();
        for (MatchCompetitorRequest request : requests) {
            validateForCreate(request);

            MatchCompetitor matchCompetitor = new MatchCompetitor();
            applyFields(matchCompetitor, request);

            boolean duplicate = matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(
                            matchCompetitor.getCompetitor().getId(), matchCompetitor.getMatch().getId(),
                            matchCompetitor.getFirearmType())
                    .isPresent()
                    || !entries.add(List.of(matchCompetitor.getCompetitor().getId(),
                    matchCompetitor.getMatch().getId(), matchCompetitor.getFirearmType()));
            if (duplicate) {
                throw duplicateEntry(matchCompetitor, null);
            }
            matchCompetitors.add(matchCompetitor);
        }

        List<MatchCompetitor> saved;
        try {
            saved = transactionService.saveMatchCompetitors(matchCompetitors);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("A match competitor in the CSV data duplicates an existing entry.", e);
        }
        return new MatchCompetitorResponseHolder(new ArrayList<>(saved.stream().map(this::toResponse).toList()));
    }

    @Override
    public MatchCompetitorResponse updateMatchCompetitor(Long matchCompetitorId, MatchCompetitorRequest request) {
        validateForCreate(request);
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        applyFields(matchCompetitor, request);
        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorResponse patchMatchCompetitor(Long matchCompetitorId, MatchCompetitorPatchRequest request) {
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        if ((request.getCompetitorId() != null) || hasText(request.getCompetitorName())) {
            matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorName()));
        }
        if (request.getMatchId() != null) {
            matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        }
        if (request.getMatchClub() != null) {
            matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        }
        if ((request.getCompetitorCategory() != null) && !request.getCompetitorCategory().isEmpty()) {
            matchCompetitor.setCompetitorCategories(resolveCompetitorCategories(request.getCompetitorCategory()));
        }
        if ((request.getFirearmType() != null) && !request.getFirearmType().isBlank()) {
            matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        }
        if ((request.getDivision() != null) && !request.getDivision().isBlank()) {
            matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        }
        if (request.getPowerFactor() != null) {
            matchCompetitor.setPowerFactor(resolvePowerFactor(request.getPowerFactor()));
        }
        if (request.getPoints() != null) {
            matchCompetitor.setPoints(request.getPoints());
        }
        if (request.getPercentage() != null) {
            matchCompetitor.setPercentage(request.getPercentage());
        }
        if (request.getTime() != null) {
            matchCompetitor.setTime(request.getTime());
        }
        if (request.getPercentageOfPossiblePoints() != null) {
            matchCompetitor.setPercentageOfPossiblePoints(request.getPercentageOfPossiblePoints());
        }
        if (request.getHitFactor() != null) {
            matchCompetitor.setHitFactor(request.getHitFactor());
        }
        if (request.getAlpha() != null) {
            matchCompetitor.setAlpha(request.getAlpha());
        }
        if (request.getCharlie() != null) {
            matchCompetitor.setCharlie(request.getCharlie());
        }
        if (request.getDelta() != null) {
            matchCompetitor.setDelta(request.getDelta());
        }
        if (request.getMisses() != null) {
            matchCompetitor.setMisses(request.getMisses());
        }
        if (request.getNoPenaltyMisses() != null) {
            matchCompetitor.setNoPenaltyMisses(request.getNoPenaltyMisses());
        }
        if (request.getNoShoots() != null) {
            matchCompetitor.setNoShoots(request.getNoShoots());
        }
        if (request.getProceduralErrors() != null) {
            matchCompetitor.setProceduralErrors(request.getProceduralErrors());
        }
        if (request.getAdditionalPenalties() != null) {
            matchCompetitor.setAdditionalPenalties(request.getAdditionalPenalties());
        }
        if (request.getOverallRanking() != null) {
            matchCompetitor.setOverallRanking(request.getOverallRanking());
        }
        if (request.getClubRanking() != null) {
            matchCompetitor.setClubRanking(request.getClubRanking());
        }
        if (request.getIsVisitor() != null) {
            matchCompetitor.setIsVisitor(request.getIsVisitor());
        }

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
    protected List<MatchCompetitorRequest> readMatchCompetitors(@NotNull @NotBlank String csvData)
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
    protected MatchCompetitor save(@NotNull MatchCompetitor matchCompetitor) {
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
     * Copies the fields of a {@link MatchCompetitorRequest} onto a {@link MatchCompetitor}, resolving the
     * competitor, match and enumerated values in the process.
     *
     * @param matchCompetitor the entity to populate; must not be null.
     * @param request         the request carrying the field values; must not be null.
     * @throws ValidationException if an enumerated value doesn't match a known one.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    protected void applyFields(@NotNull MatchCompetitor matchCompetitor, @NotNull MatchCompetitorRequest request) {
        matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorName()));
        matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        matchCompetitor.setCompetitorCategories(resolveCompetitorCategories(request.getCompetitorCategory()));
        matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        matchCompetitor.setPowerFactor(resolvePowerFactor(request.getPowerFactor()));
        matchCompetitor.setPoints(request.getPoints());
        matchCompetitor.setPercentage(request.getPercentage());
        matchCompetitor.setTime(request.getTime());
        matchCompetitor.setPercentageOfPossiblePoints(request.getPercentageOfPossiblePoints());
        matchCompetitor.setHitFactor(request.getHitFactor());
        matchCompetitor.setAlpha(request.getAlpha());
        matchCompetitor.setCharlie(request.getCharlie());
        matchCompetitor.setDelta(request.getDelta());
        matchCompetitor.setMisses(request.getMisses());
        matchCompetitor.setNoPenaltyMisses(request.getNoPenaltyMisses());
        matchCompetitor.setNoShoots(request.getNoShoots());
        matchCompetitor.setProceduralErrors(request.getProceduralErrors());
        matchCompetitor.setAdditionalPenalties(request.getAdditionalPenalties());
        matchCompetitor.setOverallRanking(request.getOverallRanking());
        matchCompetitor.setClubRanking(request.getClubRanking());
        matchCompetitor.setIsVisitor(request.getIsVisitor());
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
     * Retrieves an existing competitor or throws if none exists with the given ID.
     *
     * @param competitorId the identifier to look up.
     * @return the matching {@link Competitor}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    protected Competitor findCompetitorOrThrow(Long competitorId) {
        return competitorRepository.findById(competitorId)
                .orElseThrow(() -> new NonFatalException("No competitor found with ID " + competitorId));
    }

    /**
     * Resolves the competitor a request refers to: by ID when one is given, otherwise by full name.
     *
     * @param competitorId the identifier to look up; takes precedence over {@code name} when not null.
     * @param name         the competitor's full name, "First Last", matched case-insensitively; only used when
     *                     {@code competitorId} is null.
     * @return the matching {@link Competitor}.
     * @throws ValidationException if neither is given, or {@code name} matches more than one competitor.
     * @throws NonFatalException   if no competitor matches.
     */
    protected Competitor resolveCompetitor(Long competitorId, String name) {
        if (competitorId != null) {
            return findCompetitorOrThrow(competitorId);
        }
        if (!hasText(name)) {
            throw new ValidationException("Competitor ID or name is required.");
        }
        List<Competitor> matches = competitorRepository.findByFullNameIgnoreCase(name.trim());
        if (matches.isEmpty()) {
            throw new NonFatalException("No competitor found with name " + name.trim());
        }
        if (matches.size() > 1) {
            throw new ValidationException("More than one competitor is named " + name.trim()
                    + "; use the competitor ID instead.");
        }
        return matches.getFirst();
    }

    private static boolean hasText(String value) {
        return (value != null) && !value.isBlank();
    }

    /**
     * Retrieves an existing match or throws if none exists with the given ID.
     *
     * @param matchId the identifier to look up.
     * @return the matching {@link IpscMatch}.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    protected IpscMatch findMatchOrThrow(Long matchId) {
        return ipscMatchRepository.findById(matchId)
                .orElseThrow(() -> new NonFatalException("No IPSC match found with ID " + matchId));
    }

    /**
     * Resolves the club a competitor represented at a match, by name or abbreviation.
     *
     * @param matchClub the club name or abbreviation to look up; may be null or blank, in which case no club is
     *                  set.
     * @return the matching {@link ClubIdentifier}, or {@code null} if {@code matchClub} wasn't supplied.
     * @throws ValidationException if {@code matchClub} was supplied but doesn't match a known club.
     */
    protected ClubIdentifier resolveMatchClub(String matchClub) {
        if ((matchClub == null) || matchClub.isBlank()) {
            return null;
        }

        return ClubIdentifier.fromName(matchClub).or(() -> ClubIdentifier.fromAbbreviation(matchClub))
                .orElseThrow(() -> new ValidationException("Unknown match club: " + matchClub));
    }

    /**
     * Resolves a competitor category by name.
     *
     * @param competitorCategory the category name to look up.
     * @return the matching {@link CompetitorCategory}.
     * @throws ValidationException if no category matches {@code competitorCategory}. {@link CompetitorCategory#NONE},
     *                             which {@link CompetitorCategory#fromName(String)} falls back to for an unknown
     *                             or blank name, is treated as no match.
     */
    protected CompetitorCategory resolveCompetitorCategory(String competitorCategory) {
        return CompetitorCategory.fromName(competitorCategory)
                .filter(category -> category != CompetitorCategory.NONE)
                .orElseThrow(() -> new ValidationException("Unknown competitor category: " + competitorCategory));
    }

    /**
     * Resolves a list of competitor categories by name, dropping any repeated category.
     *
     * @param competitorCategories the category names to look up; must contain at least one.
     * @return the matching {@link CompetitorCategory} values, in the order first given, as a new mutable list.
     * @throws ValidationException if the list is null or empty, or a name doesn't match a category (see
     *                             {@link #resolveCompetitorCategory(String)}).
     */
    protected List<CompetitorCategory> resolveCompetitorCategories(List<String> competitorCategories) {
        if ((competitorCategories == null) || competitorCategories.isEmpty()) {
            throw new ValidationException("At least one competitor category is required.");
        }

        return new ArrayList<>(competitorCategories.stream()
                .map(this::resolveCompetitorCategory)
                .distinct()
                .toList());
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
                .orElseThrow(() -> new ValidationException("Unknown firearm type: " + firearmType));
    }

    /**
     * Resolves a division by name.
     *
     * @param division the division name to look up.
     * @return the matching {@link Division}.
     * @throws ValidationException if no division matches {@code division}.
     */
    protected Division resolveDivision(String division) {
        return Division.fromName(division)
                .orElseThrow(() -> new ValidationException("Unknown division: " + division));
    }

    /**
     * Resolves a power factor by name.
     *
     * @param powerFactor the power factor name to look up; may be null or blank, in which case none is set.
     * @return the matching {@link PowerFactor}, or {@code null} if {@code powerFactor} wasn't supplied.
     * @throws ValidationException if {@code powerFactor} was supplied but doesn't match a known power factor.
     */
    protected PowerFactor resolvePowerFactor(String powerFactor) {
        if ((powerFactor == null) || powerFactor.isBlank()) {
            return null;
        }

        return PowerFactor.fromName(powerFactor)
                .orElseThrow(() -> new ValidationException("Unknown power factor: " + powerFactor));
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
        if ((request.getCompetitorId() == null) && !hasText(request.getCompetitorName())) {
            throw new ValidationException("Competitor ID or name is required.");
        }
        if (request.getMatchId() == null) {
            throw new ValidationException("Match ID is required.");
        }
        if ((request.getCompetitorCategory() == null) || request.getCompetitorCategory().isEmpty()) {
            throw new ValidationException("Competitor category is required.");
        }
        if ((request.getFirearmType() == null) || request.getFirearmType().isBlank()) {
            throw new ValidationException("Firearm type is required.");
        }
        if ((request.getDivision() == null) || request.getDivision().isBlank()) {
            throw new ValidationException("Division is required.");
        }
    }

    /**
     * Maps a persisted match competitor to the response shape returned by the controller.
     *
     * @param matchCompetitor the match competitor to map, with its competitor and match loaded.
     * @return the mapped {@link MatchCompetitorResponse}.
     */
    protected MatchCompetitorResponse toResponse(MatchCompetitor matchCompetitor) {
        return new MatchCompetitorResponse(
                matchCompetitor.getId(),
                matchCompetitor.getCompetitor().getId(),
                matchCompetitor.getMatch().getId(),
                matchCompetitor.getMatchClub(),
                new ArrayList<>(matchCompetitor.getCompetitorCategories()),
                matchCompetitor.getFirearmType(),
                matchCompetitor.getDivision(),
                matchCompetitor.getPowerFactor(),
                matchCompetitor.getPoints(),
                matchCompetitor.getPercentage(),
                matchCompetitor.getTime(),
                matchCompetitor.getPercentageOfPossiblePoints(),
                matchCompetitor.getHitFactor(),
                matchCompetitor.getAlpha(),
                matchCompetitor.getCharlie(),
                matchCompetitor.getDelta(),
                matchCompetitor.getMisses(),
                matchCompetitor.getNoPenaltyMisses(),
                matchCompetitor.getNoShoots(),
                matchCompetitor.getProceduralErrors(),
                matchCompetitor.getAdditionalPenalties(),
                matchCompetitor.getOverallRanking(),
                matchCompetitor.getClubRanking(),
                matchCompetitor.getIsVisitor());
    }
}
