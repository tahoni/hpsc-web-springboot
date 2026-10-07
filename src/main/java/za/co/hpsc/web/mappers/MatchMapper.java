package za.co.hpsc.web.mappers;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
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
import za.co.hpsc.web.repositories.ClubRepository;

import java.util.stream.Stream;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Copies the fields of a {@link MatchRequest} or {@link MatchPatchRequest} onto an {@link IpscMatch}, resolving the
 * values that need a lookup, such as the club, firearm type and match category, along the way.
 *
 * <p>
 * Kept out of the request classes because resolving a club needs the {@link ClubRepository}, which a request model
 * shouldn't depend on.
 * </p>
 */
@Component
public class MatchMapper {
    private final ClubRepository clubRepository;

    public MatchMapper(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
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
    public void applyFields(@NotNull IpscMatch match, @NotNull MatchRequest request) throws FatalException {
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
     * Copies only the non-null fields of a {@link MatchPatchRequest} onto an {@link IpscMatch}, resolving the
     * named club, firearm type and match category in the process. Fields that are null in the request are left
     * unchanged, so unlike {@link #applyFields} a missing club or match category isn't defaulted.
     *
     * @param match   the entity to patch; must not be null.
     * @param request the request carrying the field values; must not be null.
     * @throws NonFatalException   if the request's club name doesn't match an existing club.
     * @throws ValidationException if the request's firearm type or match category doesn't match a known one.
     * @throws FatalException      if the club name is blank and
     *                             {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is null.
     */
    public void applyPatchFields(@NotNull IpscMatch match, @NotNull MatchPatchRequest request) throws FatalException {
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
    public Club resolveClub(String clubName) throws FatalException {
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
    public Club resolveClub(String clubName, ClubIdentifier defaultIdentifier) throws FatalException {
        if (!hasText(clubName)) {
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
    public FirearmType resolveFirearmType(String firearmType) {
        return FirearmType.fromName(firearmType)
                .orElseThrow(() -> new ValidationException("Unknown match firearm type: " + firearmType));
    }

    /**
     * Resolves a match category by name, defaulting when none is given.
     *
     * <p>
     * A match category is not mandatory here: when {@code category} is null, empty or blank, the default,
     * {@link IpscConstants#DEFAULT_MATCH_CATEGORY}, is returned instead of an error. A category that is supplied
     * must still be a known one: its display name (such as {@code "Club Shoot"}) or its constant name (such as
     * {@code "CLUB_SHOOT"}), matched ignoring case and surrounding whitespace.
     * </p>
     *
     * @param category the match category name to look up; may be null or blank, in which case the default
     *                 match category is used.
     * @return the matching {@link MatchCategory}, or {@link IpscConstants#DEFAULT_MATCH_CATEGORY} if
     * {@code category} wasn't supplied.
     * @throws ValidationException if {@code category} was supplied but no match category matches it.
     */
    public MatchCategory resolveMatchCategory(String category) {
        if (!hasText(category)) {
            return IpscConstants.DEFAULT_MATCH_CATEGORY;
        }

        String trimmedCategory = category.trim();
        return MatchCategory.fromName(trimmedCategory)
                .or(() -> Stream.of(MatchCategory.values())
                        .filter(matchCategory -> matchCategory.name().equalsIgnoreCase(trimmedCategory))
                        .findFirst())
                .orElseThrow(() -> new ValidationException("Unknown match category: " + category));
    }
}
