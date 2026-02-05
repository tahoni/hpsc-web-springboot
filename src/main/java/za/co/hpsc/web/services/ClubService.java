package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Club;

import java.util.Optional;

// TODO: Javadoc
public interface ClubService {
    Optional<Club> findClub(String name, String abbreviation);
}
