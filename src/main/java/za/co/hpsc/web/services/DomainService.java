package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.common.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.common.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;

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
     * Initialises and maps entities related to a match based on the provided match results,
     * match club abbreviation, and a filter club abbreviation.
     *
     * @param matchResults           an instance of {@code MatchResultsDto} containing detailed
     *                               information about the match, club, competitors, stages,
     *                               and related entities.
     * @param filterClubAbbreviation the abbreviation of the club used to filter matches or
     *                               related entities.
     * @param matchClubAbbreviation  the abbreviation of the club associated with the match being
     *                               processed, optional.
     * @return an {@link Optional} containing a {@link DtoMapping} object that includes
     * the initialised entities, such as match, club, competitors, and stages, or an
     * empty {@link Optional} if no mapping could be generated based on the provided input.
     */
    Optional<DtoMapping> initMatchEntities(MatchResultsDto matchResults,
                                           String filterClubAbbreviation, String matchClubAbbreviation);

    Optional<MatchOnlyResultsDto> initMatchOnlyEntities(MatchOnlyDto matchOnlyDto);
}
