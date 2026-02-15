package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.ClubMatch;

import java.util.List;

// TODO: Javadoc
public interface ClubMatchEntityService {
    List<ClubMatch> findClubMatches(Club clubEntity);
}
