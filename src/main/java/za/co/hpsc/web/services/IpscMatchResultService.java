package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;

import java.util.Optional;

/**
 * Service interface for initialising and handling match result data.
 *
 * <p>
 * The {@code MatchResultService} defines the contract for initialising match results
 * based on the data provided through IPSC responses. It facilitates the transformation
 * of raw response data into structured data transfer objects (DTOs) for further processing
 * and use within the application. The service ensures that all match-related components,
 * including club details, stages, competitors, and scores, are encapsulated into a
 * {@link MatchResultsDto} object.
 * </p>
 */
public interface IpscMatchResultService {
    /**
     * Initialises and returns the match results data transfer object (DTO) based on the
     * provided IPSC response.
     *
     * @param ipscResponse the {@link IpscResponse} object containing data related to the match,
     *                     such as club information, match details, stages, scores, and participants.
     *                     Must not be null.
     * @return an {@link Optional} containing the initialized {@link MatchResultsDto} if successful,
     * or an empty {@link Optional} if the initialisation cannot be performed.
     */
    Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse);

    // TODO: Javadoc
    Optional<MatchEntityHolder> initMatchEntities(MatchResultsDto matchResults);
}
