package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;

/**
 * The {@code ClubService} interface provides null-safe comparisons of clubs, either as persisted {@link Club}
 * entities or as {@link ClubIdentifier}s, so callers don't each repeat the null and identifier checks.
 *
 * @since 12.0.0
 */
public interface IpscEntityClubService {
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
