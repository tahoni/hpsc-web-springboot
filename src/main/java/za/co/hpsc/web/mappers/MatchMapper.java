package za.co.hpsc.web.mappers;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.services.IpscEntityClubService;

import java.util.stream.Stream;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Copies the fields of a {@link MatchRequest} or {@link MatchPatchRequest} onto an {@link IpscMatch}, resolving the
 * values that need a lookup, such as the club, firearm type and match category, along the way.
 *
 * <p>
 * Kept out of the request classes because resolving a club needs the {@link IpscEntityClubService}, which a request
 * model shouldn't depend on.
 * </p>
 *
 * @see IpscEntityClubService
 */
@Component
public class MatchMapper {
    private final IpscEntityClubService ipscEntityClubService;

    /**
     * Creates the mapper.
     *
     * @param ipscEntityClubService the service used to resolve a club by abbreviation, name or identifier.
     */
    public MatchMapper(IpscEntityClubService ipscEntityClubService) {
        this.ipscEntityClubService = ipscEntityClubService;
    }

    /**
     * Copies the match-level fields of a {@link MatchRequest} onto an {@link IpscMatch}, resolving the named club,
     * firearm type and match category in the process. A missing club defaults to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} and a missing match category to
     * {@link IpscConstants#DEFAULT_MATCH_CATEGORY}.
     *
     * @param match   the entity to populate; must not be null.
     * @param request the request carrying the field values; must not be null.
     * @throws ValidationException if the request's club, firearm type or match category doesn't match a known one.
     * @throws NonFatalException   if the request's club is a known club identifier but no club exists with it, or if
     *                             no club exists for {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    public void applyFields(@NonNull IpscMatch match, @NonNull MatchRequest request) {
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
     * unchanged, so unlike {@link #applyFields} a missing club or match category isn't defaulted. A club that is
     * supplied but blank does resolve to {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     *
     * @param match   the entity to patch; must not be null.
     * @param request the request carrying the field values; must not be null.
     * @throws ValidationException if the request's club, firearm type or match category doesn't match a known one.
     * @throws NonFatalException   if the request's club is a known club identifier but no club exists with it, or if
     *                             the club is blank and no club exists for
     *                             {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    public void applyPatchFields(@NonNull IpscMatch match, @NonNull MatchPatchRequest request) {
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
     * Resolves a club by abbreviation, name, or club identifier code or abbreviation, defaulting to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} when none is supplied.
     *
     * <p>
     * The lookup is delegated to {@link IpscEntityClubService#findByCodeOrAbbreviationWithDefault(String,
     * ClubIdentifier)}, passing {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} as the default.
     * </p>
     *
     * @param clubName the club abbreviation, name, or identifier code or abbreviation to look up; may be null or
     *                 blank, in which case the club for {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} is
     *                 resolved instead.
     * @return the matching {@link Club}.
     * @throws ValidationException if {@code clubName} was supplied but matches no existing club and is not a known
     *                             club identifier code or abbreviation.
     * @throws NonFatalException   if {@code clubName} is a known club identifier but no club exists with it, or if
     *                             {@code clubName} wasn't supplied and no club exists for
     *                             {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    public Club resolveClub(String clubName) {
        return ipscEntityClubService.findByCodeOrAbbreviationWithDefault(clubName, IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
    }

    /**
     * Resolves a club by abbreviation, name, or club identifier code or abbreviation, defaulting to
     * {@code defaultIdentifier} when none is supplied.
     *
     * <p>
     * The lookup is delegated to {@link IpscEntityClubService#findByCodeOrAbbreviationWithDefault(String,
     * ClubIdentifier)}. {@code defaultIdentifier} is taken as a parameter, rather than read directly from
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER} in this method, so the default can be varied in tests.
     * </p>
     *
     * @param clubName          the club abbreviation, name, or identifier code or abbreviation to look up; may be
     *                          null or blank, in which case {@code defaultIdentifier} is resolved instead.
     * @param defaultIdentifier the identifier to resolve when {@code clubName} isn't supplied; may be null.
     * @return the matching {@link Club}.
     * @throws ValidationException if {@code clubName} was supplied but matches no existing club and is not a known
     *                             club identifier code or abbreviation.
     * @throws NonFatalException   if {@code clubName} is a known club identifier but no club exists with it, or if
     *                             {@code clubName} wasn't supplied and {@code defaultIdentifier} is null or has no
     *                             club.
     */
    public Club resolveClub(String clubName, ClubIdentifier defaultIdentifier) {
        return ipscEntityClubService.findByCodeOrAbbreviationWithDefault(clubName, defaultIdentifier);
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
