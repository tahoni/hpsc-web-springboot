package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;

import java.util.Optional;

// TODO: Javadoc
public interface ClubService {
    Optional<Club> findClub(ClubResponse clubResponse);
}
