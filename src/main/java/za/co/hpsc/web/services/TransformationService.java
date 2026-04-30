package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.common.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.common.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.common.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.common.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.common.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;

import java.util.List;
import java.util.Optional;

/**
 * Contract for transforming IPSC-related payloads between request, DTO, and response models.
 * <p>
 * This service is responsible for:
 * </p>
 * <ul>
 *   <li>Mapping incoming request holders into structured response holders.</li>
 *   <li>Producing record-holder views from grouped match data.</li>
 *   <li>Initialising internal DTO aggregates used by downstream domain/persistence flows.</li>
 * </ul>
 */
public interface TransformationService {

    /**
     * Maps a full IPSC request payload into a structured response holder.
     * <p>
     * Implementations typically align and group related collections (matches, stages,
     * scores, competitors, tags, clubs) by match context and then assemble a coherent
     * list of {@link IpscResponse} entries.
     * </p>
     *
     * @param ipscRequestHolder container holding raw IPSC request collections to transform
     * @return an {@link IpscResponseHolder} containing mapped and grouped IPSC responses
     */
    IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder);

    // TODO: add Javadoc
    // TODO: add tests
    Optional<MatchOnlyDto> mapMatchOnly(MatchOnlyRequest matchOnlyRequest);

    /**
     * Generates an {@link IpscMatchRecordHolder} from a list of grouped match-holder objects.
     * <p>
     * Implementations convert match holder structures into record-oriented output suitable
     * for downstream consumption (for example, export/reporting layers).
     * </p>
     *
     * @param ipscMatchHolderList list of {@link MatchHolder} items to convert
     * @return an {@link IpscMatchRecordHolder} containing generated IPSC match records
     */
    IpscMatchRecordHolder generateIpscMatchRecordHolder(List<MatchHolder> ipscMatchHolderList);

    /**
     * Initialises a match-results DTO aggregate from a single IPSC response object.
     *
     * @param ipscResponse source IPSC response containing match, club, stage, competitor, and score data
     * @return an {@link Optional} containing {@link MatchResultsDto} when initialisation succeeds;
     * otherwise {@link Optional#empty()}
     */
    Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse);
}