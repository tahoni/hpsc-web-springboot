package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing the transformation service for processing and mapping IPSC
 * (International Practical Shooting Confederation) related data.
 *
 * <p>
 * It provides methods for converting raw data into structured response objects and
 * initializing variousIPSC-related records and DTOs for further usage.
 * </p>
 */
public interface TransformationService {
    /**
     * Maps IPSC requests to a list of IPSC responses. This method processes the matches, stages,
     * enrolled members, scores, tags, and clubs from the given {@link IpscRequestHolder}, groups
     * them based on match IDs, and constructs corresponding {@link IpscResponse} objects.
     *
     * @param ipscRequestHolder A holder object that contains lists of matches, stages, enrolled
     *                          members, scores, tags, members, and clubs to be processed.
     * @return A list of {@link IpscResponse} objects that encapsulate the mapped data, including
     * match details, associated tags, stages, enrolled members, scores, members, and club
     * information.
     * @throws ValidationException if the input data is invalid or cannot be processed.
     */
    IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException;

    /**
     * Generates an {@code IpscMatchRecordHolder} from the given list of {@code IpscMatch} entities.
     * This method processes the input list and structures the data into a record holder
     * for convenient access and manipulation.
     *
     * @param ipscMatchHolderList a list of {@code MatchHolder} objcets representing the matches
     *                            to be included in the generated {@code IpscMatchRecordHolder}.
     * @return an {@code IpscMatchRecordHolder} containing a list of structured match records
     * generated from the provided {@code IpscMatch} entities.
     */
    IpscMatchRecordHolder generateIpscMatchRecordHolder(List<MatchHolder> ipscMatchHolderList);

    /*
     * Initialises and returns the match results data transfer object (DTO) based on the
     * provided IPSC response.
     *
     * @param ipscResponse the {@link IpscResponse} object containing data related to the match,
     *                     such as club information, match details, stages, scores, and participants.
     * @return an {@link Optional} containing the initialised {@link MatchResultsDto} if successful,
     * or an empty {@link Optional} if the initialisation cannot be performed.
     */
    Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse);
}
