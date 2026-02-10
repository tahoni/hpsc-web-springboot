package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.ClubService;

import java.util.Optional;

@Slf4j
@Service
public class ClubServiceImpl implements ClubService {
    protected final ClubRepository clubRepository;

    public ClubServiceImpl(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public Optional<Club> findClub(String name, String abbreviation) {
        Optional<Club> club = Optional.empty();

        // Attempt to find the club by name
        if ((name != null) && (!name.isBlank())) {
            club = clubRepository.findByName(name);
        }

        // If the club was not found
        if (club.isEmpty()) {
            if ((abbreviation != null) && (!abbreviation.isBlank())) {
                // Attempt to find the club by abbreviation
                club = clubRepository.findByAbbreviation(abbreviation);
            }
        }

        return club;
    }
}
