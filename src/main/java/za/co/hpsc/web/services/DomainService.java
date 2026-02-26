package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;

import java.util.Optional;

/**
 * Provides methods for initialising and managing domain entities related to a match.
 *
 * <p>
 * The {@code DomainService} interface defines the operations needed to initialise,
 * map, and manage various entities such as matches, competitors, clubs, and stages
 * in the domain layer. Implementations of this interface are responsible for ensuring
 * that these entities are correctly instantiated and linked based on the provided data.
 * </p>
 */
public interface DomainService {
    /**
     * Initialises and maps entities related to a match based on the provided match results data.
     *
     * @param matchResults           an instance of {@code MatchResultsDto} containing detailed information
     *                               about a match, including the match itself, club, competitors, stages,
     *                               and related entities.
     * @param filterClubAbbreviation the abbreviation of the club to filter matches by.
     * @return a {@link DtoToEntityMapping} that contains the initialised match
     * entities such as the match, club, stages, and competitors.
     */
    Optional<DtoToEntityMapping> initMatchEntities(MatchResultsDto matchResults, String filterClubAbbreviation);
}
