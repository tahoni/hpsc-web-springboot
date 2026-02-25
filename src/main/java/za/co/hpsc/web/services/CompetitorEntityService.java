package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The {@code CompetitorService} interface defines the contract for operations related to
 * competitor information within the system.
 * It provides methods for searching and retrieving details about competitors based on
 * specific criteria.
 */
public interface CompetitorEntityService {
    /**
     * Searches for a {@link Competitor} based on the provided details.
     *
     * @param icsAlias        the ICS alias associated with the competitor.
     * @param firstName       the first name of the competitor to be searched for.
     * @param lastName        the last name of the competitor to be searched for.
     * @param dateTimeOfBirth the date and time of birth of the competitor to be searched for.
     * @return an {@code Optional} containing the {@link Competitor} if a match is found,
     * or an empty {@code Optional} if no matching competitor is found.
     */
    Optional<Competitor> findCompetitor(String icsAlias, String firstName, String lastName,
                                        LocalDateTime dateTimeOfBirth);
}
