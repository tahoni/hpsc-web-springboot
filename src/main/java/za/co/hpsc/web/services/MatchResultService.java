package za.co.hpsc.web.services;

import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.match.MatchResultsDto;

import java.util.Optional;

// TODO: Javadoc
public interface MatchResultService {
    Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse);
}
