package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

public interface MatchResultsService {
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
     */
    IpscResponseHolder calculateMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException;

    void saveMatchResults(IpscResponseHolder ipscResponseHolder);
}
