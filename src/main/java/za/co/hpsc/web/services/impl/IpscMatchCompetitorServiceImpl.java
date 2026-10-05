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
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.CompetitorHelpers;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequestCsvMixIn;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.ClubService;
import za.co.hpsc.web.services.EntityIpscCompetitorService;
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
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;

    private final EntityIpscCompetitorService entityIpscCompetitorService;

    private final ClubService clubService;
    private final TransactionService transactionService;

    public IpscMatchCompetitorServiceImpl(MatchCompetitorRepository matchCompetitorRepository,
                                          CompetitorRepository competitorRepository,
                                          IpscMatchRepository ipscMatchRepository,
                                          EntityIpscCompetitorService entityIpscCompetitorService,
                                          ClubService clubService,
                                          TransactionService transactionService) {
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.entityIpscCompetitorService = entityIpscCompetitorService;
        this.clubService = clubService;
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
    public MatchCompetitorBulkResponseHolder createMatchCompetitors(String csvData, String club)
            throws FatalException {
        if (!hasText(csvData)) {
            log.error("The provided csv data is null or empty.");
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        ClubIdentifier targetClub = resolveMatchClub(club);
        List<MatchCompetitorRequest> requests = readMatchCompetitors(csvData);

        // Each row is saved in its own transaction, so a bad row is reported and skipped without affecting the
        // others. save() also refuses a row that duplicates one saved earlier in this import.
        List<MatchCompetitorBulkResponse> matchCompetitorBulkResponses = new ArrayList<>();
        for (MatchCompetitorRequest request : requests) {
            try {
                if (!isForClub(request, targetClub)) {
                    matchCompetitorBulkResponses.add(new MatchCompetitorBulkResponse(false,
                            "Skipped: match club is not " + targetClub.getName(), toFailedResponse(request)));
                    continue;
                }
                validateForCreate(request);

                MatchCompetitor matchCompetitor = new MatchCompetitor();
                applyFields(matchCompetitor, request);
                matchCompetitorBulkResponses.add(
                        new MatchCompetitorBulkResponse(true, "", toResponse(save(matchCompetitor))));
            } catch (ValidationException | NonFatalException e) {
                log.warn("Match competitor skipped: {}", e.getMessage());
                matchCompetitorBulkResponses.add(
                        new MatchCompetitorBulkResponse(false, e.getMessage(), toFailedResponse(request)));
            }
        }

        return new MatchCompetitorBulkResponseHolder(matchCompetitorBulkResponses);
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

        if ((request.getCompetitorId() != null) || hasText(request.getCompetitorNumber()) || hasText(request.getCompetitorName())) {
            matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                    request.getCompetitorName()));
        }
        if (request.getMatchId() != null) {
            matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        }
        if (request.getMatchClub() != null) {
            matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        }
        if (hasText(request.getCompetitorCategory())) {
            matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        }
        if (hasText(request.getFirearmType())) {
            matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        }
        if (hasText(request.getDivision())) {
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
        matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                request.getCompetitorName()));
        matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        matchCompetitor.setPowerFactor(resolvePowerFactor(request.getPowerFactor()));
        matchCompetitor.setPoints(request.getPoints());
        matchCompetitor.setPercentage(request.getPercentage());
        matchCompetitor.setTime(request.getTime());
        matchCompetitor.setPercentageOfPossiblePoints(request.getPercentageOfPossiblePoints());
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
     * Builds the response reported for a row that could not be imported, identifying it by what the request said
     * about the competitor and match. Nothing is resolved or looked up, so this never throws for an unknown
     * competitor, match or enumerated value — the reason the row failed is in the bulk response's message.
     *
     * @param request the request for the row that failed; may be null, as for an empty row.
     * @return a {@link MatchCompetitorResponse} carrying the requested competitor ID, name, competitor number and
     * match ID, with every other field unset — or with every field unset if {@code request} is null.
     */
    protected MatchCompetitorResponse toFailedResponse(MatchCompetitorRequest request) {
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        if (request == null) {
            return response;
        }
        response.setCompetitorId(request.getCompetitorId());
        response.setCompetitorName(request.getCompetitorName());
        response.setCompetitorNumber(CompetitorHelpers.getCompetitorNumberAsInteger(request.getCompetitorNumber()));
        response.setMatchId(request.getMatchId());
        return response;
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
     * Retrieves the single existing competitor that matches the given competitor number and name, or throws if
     * there is none.
     *
     * <p>
     * The lookup is delegated to {@link EntityIpscCompetitorService#findCompetitor(String, String)}, which tries the
     * competitor number first, then the ID number, then the full name and itself throws when it does not find
     * exactly one competitor. The {@link NonFatalException} thrown here is therefore a safeguard for an empty result.
     * </p>
     *
     * @param competitorNumber the competitor's number (SAPSA or club number); surrounding whitespace is ignored.
     * @param competitorName   the competitor's full name, "FirstName LastName" or "NickName LastName", matched
     *                         ignoring case.
     * @return the matching {@link Competitor}.
     * @throws ValidationException if both the competitor number and the name are null or blank, or if more than one
     *                             competitor matches, including when the name matches none of the competitors that
     *                             share the number.
     * @throws NonFatalException   if no competitor matches.
     */
    protected Competitor findCompetitorOrThrow(String competitorNumber, String competitorName) {
        String trimmedCompetitorNumber = (competitorNumber == null) ? null : competitorNumber.trim();
        return entityIpscCompetitorService.findCompetitor(trimmedCompetitorNumber, competitorName)
                .orElseThrow(() -> new NonFatalException(
                        String.format("No competitor found with competitor number of %s or name %s ",
                                competitorNumber, competitorName)));
    }

    /**
     * Converts a competitor number received as text to the whole number it is stored as.
     *
     * @param competitorNumber the competitor number as text; surrounding whitespace is ignored.
     * @return the competitor number as a whole number.
     * @throws ValidationException if the competitor number is not a whole number.
     */
    protected Integer parseCompetitorNumber(String competitorNumber) {
        try {
            return Integer.valueOf(competitorNumber.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Competitor number must be a whole number: " + competitorNumber.trim());
        }
    }

    /**
     * Resolves the competitor a request refers to: by ID when one is given, otherwise by competitor number and
     * name through {@link #findCompetitorOrThrow(String, String)}.
     *
     * @param competitorId     the identifier to look up; takes precedence over the number and name when not null.
     * @param competitorNumber the competitor's number (SAPSA or club number) or ID number; only used when
     *                         {@code competitorId} is null. May be null or blank when {@code name} is given.
     * @param name             the competitor's full name, "FirstName LastName" or "NickName LastName", matched
     *                         ignoring case; only used when {@code competitorId} is null. May be null or blank when
     *                         {@code competitorNumber} is given.
     * @return the matching {@link Competitor}.
     * @throws ValidationException if {@code competitorId}, {@code competitorNumber} and {@code name} are all null
     *                             or blank, or if more than one competitor matches the number and name, including when
     *                             the name matches none of the competitors that share the number.
     * @throws NonFatalException   if no competitor matches the number and name.
     */
    protected Competitor resolveCompetitor(Long competitorId, String competitorNumber, String name) {
        if (competitorId != null) {
            return findCompetitorOrThrow(competitorId);
        }

        return findCompetitorOrThrow(competitorNumber, name);
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
     * Checks whether a bulk import row is for the club being imported.
     *
     * <p>
     * The checks run in order, and the first that succeeds decides:
     * </p>
     * <ol>
     *     <li>With no target club there is nothing to filter on, so every row matches and the row isn't inspected at
     *     all, not even for an unknown {@code matchClub}.</li>
     *     <li>The row's {@code matchClub}, by name or abbreviation, is the target club.</li>
     *     <li>Failing that, the home club of the competitor the row identifies is the target club. The competitor is
     *     resolved as by {@link #resolveCompetitor(Long, String, String)}: by ID when the row has one, otherwise by
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

        // Test the match club against the target club
        if (clubService.isSameClub(resolveMatchClub(request.getMatchClub()), targetClub)) {
            return true;
        }

        // Test the competitor's home club against the target club
        try {
            return clubService.isSameClub(resolveCompetitorClub(request), targetClub);
        } catch (ValidationException | NonFatalException e) {
            return false;
        }
    }

    protected ClubIdentifier resolveMatchCompetitorClub(MatchCompetitorRequest request) {
        return ClubIdentifier.fromAbbreviation(request.getMatchClub()).orElse(null);
    }

    /**
     * Resolves the home club of the competitor a request refers to.
     *
     * <p>
     * The counterpart of {@link #resolveMatchClub(String)} for the competitor's own club rather than the club they
     * represented at the match: the competitor is resolved as by {@link #resolveCompetitor(Long, String, String)},
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
    protected ClubIdentifier resolveCompetitorClub(MatchCompetitorRequest request) {
        Competitor competitor = resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                request.getCompetitorName());
        Club homeClub = competitor.getHomeClub();
        return (homeClub == null) ? null : homeClub.getIdentifier();
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
        if (!hasText(matchClub)) {
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
        if ((request.getCompetitorId() == null) && !hasText(request.getCompetitorNumber())
                && !hasText(request.getCompetitorName())) {
            throw new ValidationException("Competitor ID, number or name is required.");
        }
        if (request.getMatchId() == null) {
            throw new ValidationException("Match ID is required.");
        }
        if (!hasText(request.getCompetitorCategory())) {
            throw new ValidationException("Competitor category is required.");
        }
        if (!hasText(request.getFirearmType())) {
            throw new ValidationException("Firearm type is required.");
        }
        if (!hasText(request.getDivision())) {
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
                matchCompetitor.getCompetitor().getNickName() + ' ' + matchCompetitor.getCompetitor().getLastName(),
                matchCompetitor.getCompetitor().getCompetitorNumber(),
                matchCompetitor.getMatchClub(),
                matchCompetitor.getCompetitorCategory(),
                matchCompetitor.getFirearmType(),
                matchCompetitor.getDivision(),
                matchCompetitor.getPowerFactor(),
                matchCompetitor.getPoints(),
                matchCompetitor.getPercentage(),
                matchCompetitor.getTime(),
                matchCompetitor.getPercentageOfPossiblePoints(),
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
