package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;

import java.util.Optional;

/**
 * The {@code ClubEntityService} interface defines the contract for operations related to
 * club information within the system.
 * It provides methods for searching and retrieving details about clubs based on
 * specific criteria.
 */
public interface ClubEntityService {
    /**
     * Searches for a {@link Club} entity based on the provided details.
     *
     * <p>
     * Will first see if it can find it using the name, if it can't,
     * it will try to find it using the abbreviation.
     * </p>
     *
     * @param name         the name of the club to be searched for.
     * @param abbreviation the abbreviation of the club to be searched for.
     * @return an {@code  Optional} containing the {@link Club} if a match is found,
     * or an empty {@code Optional} if no matching club is found.
     */
    Optional<Club> findClubByNameOrAbbreviation(String name, String abbreviation);

    // TODO: Javadoc
    Optional<Club> findClubByName(String name);

    // TODO: Javadoc
    Optional<Club> findClubByAbbreviation(String abbreviation);
}
