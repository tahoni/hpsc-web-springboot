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
     * Searches for a {@link Club} entity by its unique identifier.
     *
     * @param id the unique identifier of the club to be searched for.
     *           Must not be null.
     * @return an {@code Optional} containing the {@link Club} if found,
     * or an empty {@code Optional} if no club with the given ID exists.
     */
    Optional<Club> findClubById(Long id);

    /**
     * Searches for a {@link Club} entity by its name or abbreviation.
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

    /**
     * Searches for a {@link Club} entity by its name.
     *
     * @param name the name of the club to be searched for.
     *             Must not be null.
     * @return an {@code Optional} containing the {@link Club} if a matching name is found,
     * or an empty {@code Optional} if no club with the given name exists.
     */
    Optional<Club> findClubByName(String name);

    /**
     * Searches for a {@link Club} entity by its abbreviation.
     *
     * @param abbreviation the abbreviation of the club to be searched for.
     *                     Must not be null.
     * @return an {@code Optional} containing the {@link Club} if a matching abbreviation is found,
     * or an empty {@code Optional} if no club with the given abbreviation exists.
     */
    Optional<Club> findClubByAbbreviation(String abbreviation);
}

