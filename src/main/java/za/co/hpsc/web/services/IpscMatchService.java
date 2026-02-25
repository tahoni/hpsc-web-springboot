package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

import java.util.List;

/**
 * Service interface for processing IPSC matches, stages, members, scores, tags, and clubs into
 * structured response objects and record holders. It provides methods to map the input data
 * into domain-specific responses and generate match records for further utilisation in the
 * application.
 */
public interface IpscMatchService {
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
     * @param ipscMatchEntityList a list of {@code IpscMatch} entities representing the matches
     *                            to be included in the generated {@code IpscMatchRecordHolder}.
     * @return an {@code IpscMatchRecordHolder} containing a list of structured match records
     * generated from the provided {@code IpscMatch} entities.
     */
    IpscMatchRecordHolder generateIpscMatchRecordHolder(List<IpscMatch> ipscMatchEntityList);

}
