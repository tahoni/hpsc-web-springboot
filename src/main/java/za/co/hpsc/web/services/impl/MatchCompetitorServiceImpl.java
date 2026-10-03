package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.MatchCompetitorService;
import za.co.hpsc.web.services.TransactionService;

import java.util.List;

@Service
public class MatchCompetitorServiceImpl implements MatchCompetitorService {
    private final MatchCompetitorRepository matchCompetitorRepository;
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final TransactionService transactionService;

    public MatchCompetitorServiceImpl(MatchCompetitorRepository matchCompetitorRepository,
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
    public MatchCompetitorResponse updateMatchCompetitor(Long matchCompetitorId, MatchCompetitorRequest request) {
        validateForCreate(request);
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        applyFields(matchCompetitor, request);
        return toResponse(save(matchCompetitor));
    }

    @Override
    public MatchCompetitorResponse patchMatchCompetitor(Long matchCompetitorId, MatchCompetitorPatchRequest request) {
        MatchCompetitor matchCompetitor = findMatchCompetitorOrThrow(matchCompetitorId);

        if (request.getCompetitorId() != null) {
            matchCompetitor.setCompetitor(findCompetitorOrThrow(request.getCompetitorId()));
        }
        if (request.getMatchId() != null) {
            matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        }
        if (request.getMatchClub() != null) {
            matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        }
        if (request.getCompetitorCategory() != null) {
            matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        }
        if (request.getFirearmType() != null) {
            matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        }
        if (request.getDivision() != null) {
            matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        }
        if (request.getPowerFactor() != null) {
            matchCompetitor.setPowerFactor(resolvePowerFactor(request.getPowerFactor()));
        }
        if (request.getMatchPoints() != null) {
            matchCompetitor.setMatchPoints(request.getMatchPoints());
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
        matchCompetitor.setCompetitor(findCompetitorOrThrow(request.getCompetitorId()));
        matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType()));
        matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        matchCompetitor.setPowerFactor(resolvePowerFactor(request.getPowerFactor()));
        matchCompetitor.setMatchPoints(request.getMatchPoints());
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
        if (request.getCompetitorId() == null) {
            throw new ValidationException("Competitor ID is required.");
        }
        if (request.getMatchId() == null) {
            throw new ValidationException("Match ID is required.");
        }
        if ((request.getCompetitorCategory() == null) || request.getCompetitorCategory().isBlank()) {
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
                matchCompetitor.getCompetitorCategory(),
                matchCompetitor.getFirearmType(),
                matchCompetitor.getDivision(),
                matchCompetitor.getPowerFactor(),
                matchCompetitor.getMatchPoints(),
                matchCompetitor.getOverallRanking(),
                matchCompetitor.getClubRanking(),
                matchCompetitor.getIsVisitor());
    }
}
