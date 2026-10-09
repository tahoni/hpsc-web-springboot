package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;

/**
 * The {@code IpscEntityClubService} interface resolves clubs by code, abbreviation or name, and provides null-safe
 * comparisons of clubs, either as persisted {@link Club} entities or as {@link ClubIdentifier}s, so callers don't each
 * repeat the null and identifier checks.
 *
 * @since 12.0.0
 */
public interface IpscEntityClubService {
    /**
     * Finds a club by its abbreviation or name, or by a club identifier's code, abbreviation or name.
     *
     * <p>
     * The persisted clubs are searched first, by abbreviation and then by name. If none matches, the value is
     * interpreted as a {@link ClubIdentifier} code, abbreviation or name, and the club with that identifier is
     * looked up. Unlike {@link #findByCodeOrAbbreviationWithDefault(String, ClubIdentifier)}, there is no default
     * club, so a blank {@code clubName} is an error.
     * </p>
     *
     * @param clubName the club abbreviation or name, or identifier code, abbreviation or name, to look up.
     * @return the matching {@link Club}.
     * @throws ValidationException if {@code clubName} matches no persisted club and is not a known club identifier
     *                             code, abbreviation or name.
     * @throws NonFatalException   if {@code clubName} is {@code null} or blank, or is a known club identifier but no
     *                             club is persisted with it.
     * @since 14.0.0
     */
    Club findByCodeOrAbbreviation(String clubName)
            throws ValidationException, NonFatalException;

    /**
     * Finds a club by its abbreviation or name, or by a club identifier's code, abbreviation or name, falling back
     * to a default club when no name is given.
     *
     * <p>
     * A {@code null} or blank {@code clubName} resolves to the persisted club with {@code defaultClubIdentifier},
     * and fails if there is no default or no such club is persisted. Otherwise, the persisted clubs are searched
     * first, by abbreviation and then by name. If none matches, the value is interpreted as a
     * {@link ClubIdentifier} code, abbreviation or name, and the club with that identifier is looked up. The default
     * is ignored whenever a name is supplied.
     * </p>
     *
     * @param clubName              the club abbreviation or name, or identifier code, abbreviation or name, to look
     *                              up; may be {@code null} or blank.
     * @param defaultClubIdentifier the identifier of the club to return when {@code clubName} is {@code null} or
     *                              blank; may be {@code null}, in which case a blank {@code clubName} is an error.
     * @return the matching {@link Club}, or the club with {@code defaultClubIdentifier} when {@code clubName} is
     * {@code null} or blank.
     * @throws ValidationException if {@code clubName} matches no persisted club and is not a known club identifier
     *                             code, abbreviation or name.
     * @throws NonFatalException   if {@code clubName} is {@code null} or blank and {@code defaultClubIdentifier} is
     *                             {@code null} or has no persisted club, or if {@code clubName} is a known club
     *                             identifier but no club is persisted with it.
     * @since 14.0.0
     */
    Club findByCodeOrAbbreviationWithDefault(String clubName, ClubIdentifier defaultClubIdentifier)
            throws ValidationException, NonFatalException;

    /**
     * Checks whether a club is the club with the given identifier.
     *
     * <p>
     * The comparison is null-safe: it is never an error to pass {@code null}, and a {@code null}
     * club or target identifier is simply not a match, even when both are {@code null}.
     * </p>
     *
     * @param club                 the club to check; may be {@code null}.
     * @param targetClubIdentifier the identifier to compare the club's identifier with; may be
     *                             {@code null}.
     * @return {@code true} only if both arguments are non-null and the club's identifier is
     * {@code targetClubIdentifier}; {@code false} otherwise, including when the club has no
     * identifier.
     * @since 12.0.0
     */
    boolean isSameClub(Club club, ClubIdentifier targetClubIdentifier);

    /**
     * Checks whether a club identifier is the given target club identifier.
     *
     * <p>
     * The comparison is null-safe: it is never an error to pass {@code null}, and a {@code null}
     * identifier is simply not a match, even when the target is also {@code null}.
     * </p>
     *
     * @param clubIdentifier       the identifier to check; may be {@code null}.
     * @param targetClubIdentifier the identifier to compare {@code clubIdentifier} with; may be
     *                             {@code null}.
     * @return {@code true} only if {@code clubIdentifier} is non-null and is
     * {@code targetClubIdentifier}; {@code false} otherwise.
     * @since 12.0.0
     */
    boolean isSameClub(ClubIdentifier clubIdentifier, ClubIdentifier targetClubIdentifier);
}
