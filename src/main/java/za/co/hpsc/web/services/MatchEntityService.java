package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;

import java.util.Optional;

/**
 * The {@code MatchEntityService} interface defines the contract for operations related to
 * match information within the system.
 * It provides methods for searching and retrieving details about matches based on
 * specific criteria.
 */
public interface MatchEntityService {
    /**
     * Searches for an {@link IpscMatch} entity by its name.
     *
     * @param name the name of the match to search for.
     * @return an {@code Optional} containing the {@link IpscMatch} if a match is found,
     * or an empty {@code Optional} if no matching match is found.
     */
    Optional<IpscMatch> findMatchByName(String name);
}
