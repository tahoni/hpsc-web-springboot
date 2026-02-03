package za.co.hpsc.web.services;

import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;

// TODO: javadoc
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
     * @throws ValidationException if the input data is invalid or cannot be processed
     */
    IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException;

    /**
     * Calculates and updates the summary of match results based on the data provided in the
     * {@code ipscResponse} object. This operation may involve aggregating scores, processing
     * enrolled members, and summarising stage results to provide a comprehensive overview
     * of the match.
     *
     * @param ipscResponse The {@link IpscResponse} object containing data such as match details,
     *                     stage responses, enrolled members, tags, scores, and associated club
     *                     information required for summary calculation.
     * @throws ValidationException if the input data is invalid or cannot be processed
     */
    void calculateMatchResultsSummary(IpscResponse ipscResponse)
            throws ValidationException;
}
