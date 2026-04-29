package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Defines read-only operations for locating and retrieving {@link IpscMatch} entities.
 * <p>
 * This service abstracts match lookup logic, so callers can query by primary key or by
 * business-identifying attributes (name + scheduled date/time) without coupling to
 * repository details.
 * </p>
 */
public interface MatchEntityService {

    /**
     * Finds a match by its database identifier.
     *
     * @param matchId the unique database ID of the match; expected to be non-null
     *                and represent an existing persisted record.
     * @return an {@link Optional} containing the matching {@link IpscMatch} when found;
     * otherwise, {@link Optional#empty()}.
     */
    Optional<IpscMatch> findMatchById(Long matchId);

    /**
     * Finds a match by its name and scheduled date/time.
     * <p>
     * This lookup is useful when an external payload does not provide an internal ID
     * but does include natural identifying fields for a match occurrence.
     * </p>
     *
     * @param name              the match name to search for; typically expected to match
     *                          persisted naming exactly.
     * @param scheduledDateTime the scheduled start date/time of the match.
     * @return an {@link Optional} containing the matched {@link IpscMatch} if one exists;
     * otherwise, {@link Optional#empty()}.
     */
    Optional<IpscMatch> findMatchByNameAndScheduledDate(String name, LocalDateTime scheduledDateTime);
}