package za.co.hpsc.web.mappers;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.helpers.CompetitorHelpers;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.IpscEntityCompetitorService;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Copies the fields of a {@link MatchCompetitorRequest} or {@link MatchCompetitorPatchRequest} onto a
 * {@link MatchCompetitor}, resolving the competitor, match and enumerated values that need a lookup along the way.
 *
 * <p>
 * Kept out of the request classes because resolving a competitor or match needs repositories, which a request model
 * shouldn't depend on.
 * </p>
 */
@Component
public class MatchCompetitorMapper {
    private final CompetitorRepository competitorRepository;
    private final IpscMatchRepository ipscMatchRepository;
    private final IpscEntityCompetitorService ipscEntityCompetitorService;

    public MatchCompetitorMapper(CompetitorRepository competitorRepository,
                                 IpscMatchRepository ipscMatchRepository,
                                 IpscEntityCompetitorService ipscEntityCompetitorService) {
        this.competitorRepository = competitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscEntityCompetitorService = ipscEntityCompetitorService;
    }

    /**
     * Copies the fields of a {@link MatchCompetitorRequest} onto a {@link MatchCompetitor}, resolving the
     * competitor, match and enumerated values in the process.
     *
     * @param matchCompetitor the entity to populate; must not be null.
     * @param request         the request carrying the field values; must not be null.
     * @throws ValidationException if an enumerated value doesn't match a known one, or the division doesn't
     *                             belong to the firearm type. A blank firearm type is taken from the division.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    public void applyFields(@NonNull MatchCompetitor matchCompetitor, @NonNull MatchCompetitorRequest request) {

        matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                CompetitorHelpers.cleanCompetitorName(request.getCompetitorName())));
        matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        matchCompetitor.setFirearmType(hasText(request.getFirearmType())
                ? resolveFirearmType(request.getFirearmType()) : null);
        matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        initialiseFirearmTypeFromDivision(matchCompetitor);
        validateDivisionMatchesFirearmType(matchCompetitor.getDivision(), matchCompetitor.getFirearmType());
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
     * Copies only the supplied fields of a {@link MatchCompetitorPatchRequest} onto a {@link MatchCompetitor},
     * resolving the competitor, match and enumerated values in the process. Fields that are null in the request
     * (or blank, for the competitor and enumerated values) are left unchanged.
     *
     * @param matchCompetitor the entity to patch; must not be null.
     * @param request         the request carrying the field values; must not be null.
     * @throws ValidationException if an enumerated value doesn't match a known one, the division doesn't belong
     *                             to the firearm type, or the competitor number and name match more than one
     *                             competitor.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    public void applyPatchFields(@NonNull MatchCompetitor matchCompetitor,
                                 @NonNull MatchCompetitorPatchRequest request) {
        if ((request.getCompetitorId() != null) || hasText(request.getCompetitorNumber()) || hasText(request.getCompetitorName())) {
            matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                    CompetitorHelpers.cleanCompetitorName(request.getCompetitorName())));
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
        initialiseFirearmTypeFromDivision(matchCompetitor);
        if (hasText(request.getFirearmType()) || hasText(request.getDivision())) {
            validateDivisionMatchesFirearmType(matchCompetitor.getDivision(), matchCompetitor.getFirearmType());
        }
        if (hasText(request.getPowerFactor())) {
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
    }

    /**
     * Retrieves an existing competitor or throws if none exists with the given ID.
     *
     * @param competitorId the identifier to look up.
     * @return the matching {@link Competitor}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    public Competitor findCompetitorOrThrow(Long competitorId) {
        return competitorRepository.findById(competitorId)
                .orElseThrow(() -> new NonFatalException("No competitor found with ID " + competitorId));
    }

    /**
     * Retrieves the single existing competitor that matches the given competitor number and name, or throws if
     * there is none.
     *
     * <p>
     * The lookup is delegated to {@link IpscEntityCompetitorService#findCompetitor(String, String)}, which tries the
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
    public Competitor findCompetitorOrThrow(String competitorNumber, String competitorName) {
        String trimmedCompetitorNumber = (competitorNumber == null) ? null : competitorNumber.trim();
        return ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(trimmedCompetitorNumber, competitorName)
                .orElseThrow(() -> new NonFatalException(
                        String.format("No competitor found with competitor number of %s or name %s ",
                                competitorNumber, competitorName)));
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
    public Competitor resolveCompetitor(Long competitorId, String competitorNumber, String name) {
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
    public IpscMatch findMatchOrThrow(Long matchId) {
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
    public @Nullable ClubIdentifier resolveMatchClub(String matchClub) {
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
    public CompetitorCategory resolveCompetitorCategory(String competitorCategory) {
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
    public FirearmType resolveFirearmType(String firearmType) {
        return FirearmType.fromName(firearmType)
                .orElseThrow(() -> new ValidationException("Unknown firearm type: " + firearmType));
    }

    /**
     * Sets the firearm type from the division when the match competitor has a division but no firearm type.
     *
     * @param matchCompetitor the match competitor to initialise.
     */
    private void initialiseFirearmTypeFromDivision(MatchCompetitor matchCompetitor) {
        if ((matchCompetitor.getFirearmType() == null) && (matchCompetitor.getDivision() != null)) {
            matchCompetitor.setFirearmType(matchCompetitor.getDivision().getFirearmType());
        }
    }

    /**
     * Checks that a division is one shot with the given firearm type.
     *
     * @param division    the division to check; ignored if null.
     * @param firearmType the firearm type it must belong to; ignored if null.
     * @throws ValidationException if {@code division} belongs to a different firearm type.
     */
    public void validateDivisionMatchesFirearmType(@Nullable Division division, @Nullable FirearmType firearmType) {
        if ((division != null) && (firearmType != null) && (division.getFirearmType() != firearmType)) {
            throw new ValidationException("Division " + division + " is not a " + firearmType + " division");
        }
    }

    /**
     * Resolves a division by name.
     *
     * @param division the division name to look up.
     * @return the matching {@link Division}.
     * @throws ValidationException if no division matches {@code division}.
     */
    public Division resolveDivision(String division) {
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
    public @Nullable PowerFactor resolvePowerFactor(String powerFactor) {
        if ((powerFactor == null) || powerFactor.isBlank()) {
            return null;
        }

        return PowerFactor.fromName(powerFactor)
                .orElseThrow(() -> new ValidationException("Unknown power factor: " + powerFactor));
    }
}
