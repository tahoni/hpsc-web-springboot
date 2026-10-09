package za.co.hpsc.web.mappers;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.helpers.CompetitorHelpers;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.IpscEntityCompetitorService;

import java.util.Optional;

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
     *                             belong to the firearm type. A null, blank or unrecognised firearm type is taken
     *                             from the division, see {@link #resolveFirearmType(String, Division)}.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    public void applyFields(@NonNull MatchCompetitor matchCompetitor, @NonNull MatchCompetitorRequest request) {

        matchCompetitor.setCompetitor(resolveCompetitor(request.getCompetitorId(), request.getCompetitorNumber(),
                CompetitorHelpers.cleanCompetitorName(request.getCompetitorName())));
        matchCompetitor.setMatch(findMatchOrThrow(request.getMatchId()));
        matchCompetitor.setMatchClub(resolveMatchClub(request.getMatchClub()));
        matchCompetitor.setCompetitorCategory(resolveCompetitorCategory(request.getCompetitorCategory()));
        matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType(), matchCompetitor.getDivision()));
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
     * Copies the fields of a {@link MatchCompetitorRequest} onto a {@link MatchCompetitor} as far as they can be
     * resolved, leaving anything missing or unresolvable {@code null} rather than throwing, so the caller can report
     * every such field at once, for example with
     * {@link za.co.hpsc.web.helpers.MatchCompetitorHelpers#getErrorMessagesForMissingRequiredFields}.
     *
     * <p>
     * The firearm type is taken from the division when it is not recognised, as in
     * {@link #applyFields(MatchCompetitor, MatchCompetitorRequest)}. The division is not checked against the firearm
     * type here.
     * </p>
     *
     * @param matchCompetitor the entity to populate; must not be null.
     * @param request         the request carrying the field values; must not be null.
     */
    public void populateResolvableFields(@NonNull MatchCompetitor matchCompetitor,
                                         @NonNull MatchCompetitorRequest request) {
        matchCompetitor.setCompetitor(findCompetitor(request).orElse(null));
        matchCompetitor.setMatch((request.getMatchId() == null) ? null
                : ipscMatchRepository.findById(request.getMatchId()).orElse(null));
        matchCompetitor.setMatchClub(hasText(request.getMatchClub())
                ? ClubIdentifier.fromName(request.getMatchClub())
                .or(() -> ClubIdentifier.fromAbbreviation(request.getMatchClub()))
                .or(() -> ClubIdentifier.fromCode(request.getMatchClub()))
                .orElse(null) : null);
        matchCompetitor.setCompetitorCategory(CompetitorCategory.fromName(request.getCompetitorCategory())
                .filter(category -> category != CompetitorCategory.NONE).orElse(null));
        matchCompetitor.setDivision(Division.fromName(request.getDivision()).orElse(null));
        matchCompetitor.setFirearmType(FirearmType.fromName(request.getFirearmType())
                .or(() -> Optional.ofNullable(matchCompetitor.getDivision()).map(Division::getFirearmType))
                .orElse(null));
        matchCompetitor.setPowerFactor(PowerFactor.fromName(request.getPowerFactor()).orElse(null));
    }

    /**
     * Finds the competitor a request refers to without throwing: by ID when one is given, otherwise by competitor
     * number and cleaned name. A request that names no competitor, or whose number and name match none or several,
     * has none.
     */
    private Optional<Competitor> findCompetitor(MatchCompetitorRequest request) {
        if (request.getCompetitorId() != null) {
            return competitorRepository.findById(request.getCompetitorId());
        }
        if (!hasText(request.getCompetitorNumber()) && !hasText(request.getCompetitorName())) {
            return Optional.empty();
        }
        try {
            return Optional.of(resolveCompetitor(null, request.getCompetitorNumber(),
                    CompetitorHelpers.cleanCompetitorName(request.getCompetitorName())));
        } catch (ValidationException | NonFatalException e) {
            // The competitor service reports "none" and "several" by throwing
            return Optional.empty();
        }
    }

    /**
     * Copies only the supplied fields of a {@link MatchCompetitorPatchRequest} onto a {@link MatchCompetitor},
     * resolving the competitor, match and enumerated values in the process. Fields that are null in the request
     * (or blank, for the competitor and enumerated values) are left unchanged.
     *
     * <p>
     * The firearm type is resolved with {@link #resolveFirearmType(String, Division)}, against the division after
     * any patch to it, so an unrecognised firearm type is taken from the division. When the request patches the
     * division but not the firearm type, and the entity has no firearm type yet, the firearm type is taken from the
     * new division; otherwise an existing firearm type is left as it is. The division and firearm type are then
     * checked against each other whenever either is patched.
     * </p>
     *
     * @param matchCompetitor the entity to patch; must not be null.
     * @param request         the request carrying the field values; must not be null.
     * @throws ValidationException if an enumerated value doesn't match a known one, the division doesn't belong
     *                             to the firearm type, the request's firearm type is unrecognised and the entity has
     *                             no division to take it from, or the competitor number and name match more than one
     *                             competitor.
     * @throws NonFatalException   if the competitor or match cannot be found.
     */
    public void applyPatchFields(@NonNull MatchCompetitor matchCompetitor,
                                 @NonNull MatchCompetitorPatchRequest request) {
        if ((request.getCompetitorId() != null) || hasText(request.getCompetitorNumber()) ||
                hasText(request.getCompetitorName())) {
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
        if (hasText(request.getDivision())) {
            matchCompetitor.setDivision(resolveDivision(request.getDivision()));
        }
        if (hasText(request.getFirearmType())) {
            matchCompetitor.setFirearmType(resolveFirearmType(request.getFirearmType(), matchCompetitor.getDivision()));
        } else if ((matchCompetitor.getFirearmType() == null) && (hasText(request.getDivision()))) {
            matchCompetitor.setFirearmType(resolveFirearmType(null, matchCompetitor.getDivision()));
        }
        if ((hasText(request.getFirearmType())) || (hasText(request.getDivision()))) {
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
     * The lookup is delegated to {@link IpscEntityCompetitorService#findCompetitorByIdentifierAndFullName(String, String)},
     * which tries the competitor number first, then the ID number, then the full name and itself throws when it does not find
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
     * Resolves the club a competitor represented at a match, by name, abbreviation or code.
     *
     * @param matchClub the club name, abbreviation or code to look up, tried in that order; may be null or blank,
     *                  in which case no club is set.
     * @return the matching {@link ClubIdentifier}, or {@code null} if {@code matchClub} wasn't supplied.
     * @throws ValidationException if {@code matchClub} was supplied but doesn't match a known club.
     */
    public @Nullable ClubIdentifier resolveMatchClub(String matchClub) {
        if (!hasText(matchClub)) {
            return null;
        }

        return ClubIdentifier.fromName(matchClub)
                .or(() -> ClubIdentifier.fromAbbreviation(matchClub))
                .or(() -> ClubIdentifier.fromCode(matchClub))
                .orElseThrow(() -> new ValidationException("Unknown match club: " + matchClub));
    }

    /**
     * Resolves a competitor category by name.
     *
     * <p>
     * A blank name resolves to {@link CompetitorCategory#NONE}, as per
     * {@link CompetitorCategory#fromName(String)}.
     * </p>
     *
     * @param competitorCategory the category name to look up; may be blank.
     * @return the matching {@link CompetitorCategory}, or {@link CompetitorCategory#NONE} if the name is blank.
     * @throws ValidationException if {@code competitorCategory} is null or non-blank and matches no category.
     */
    public CompetitorCategory resolveCompetitorCategory(String competitorCategory) {
        return CompetitorCategory.fromName(competitorCategory)
                .orElseThrow(() -> new ValidationException("Unknown competitor category: " + competitorCategory));
    }

    /**
     * Resolves a firearm type by name, falling back to the firearm type of the division.
     *
     * <p>
     * If the name matches a known firearm type, that type is returned, even when it differs from the division's;
     * use {@link #validateDivisionMatchesFirearmType(Division, FirearmType)} to reject such a mismatch. Otherwise,
     * including when the name is null or blank, the firearm type belonging to the division is used instead.
     * </p>
     *
     * @param firearmType the firearm type name to look up; may be null or blank when {@code division} is given.
     * @param division    the division to take the firearm type from when the name is not recognised; may be
     *                    {@code null}, in which case the name must match a known firearm type.
     * @return the matching {@link FirearmType}, or the division's firearm type when the name is not recognised.
     * @throws ValidationException if the name matches no firearm type and the division is {@code null} or has no
     *                             firearm type.
     */
    public FirearmType resolveFirearmType(String firearmType, Division division) {
        Optional<FirearmType> optionalFirearmType = FirearmType.fromName(firearmType);
        if (optionalFirearmType.isPresent()) {
            // If the firearm type is provided, return it
            return optionalFirearmType.get();
        } else if (division != null) {
            // If the firearm type is not provided, return the firearm type from the division if it exists,
            // otherwise throw an exception
            FirearmType divisionFirearmType = division.getFirearmType();
            if (divisionFirearmType != null) {
                return divisionFirearmType;
            } else {
                throw new ValidationException("Division " + division + " has no default firearm type " +
                        "and unknown firearm type" + firearmType);
            }
        } else {
            throw new ValidationException("Unknown firearm type: " + firearmType);
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
