package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.ClubEntityService;

import java.util.Optional;

// TODO: add tests
@Slf4j
@Service
public class ClubEntityServiceImpl implements ClubEntityService {
    protected final ClubRepository clubRepository;

    public ClubEntityServiceImpl(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public Optional<Club> findClub(String name, String abbreviation) {
        if (((name == null) || (name.isBlank())) && ((abbreviation == null) || (abbreviation.isBlank()))) {
            return Optional.empty();
        }

        Optional<Club> optionalClub = Optional.empty();

        // Attempt to find the club by name
        if ((name != null) && (!name.isBlank())) {
            optionalClub = clubRepository.findByName(name);
        }

        // If the club was not found
        Club club = optionalClub.orElseGet(() -> {
            if ((abbreviation != null) && (!abbreviation.isBlank())) {
                // Attempt to find the club by abbreviation
                return clubRepository.findByAbbreviation(abbreviation).orElse(null);
            }
            return null;
        });

        return Optional.ofNullable(club);
    }

    @Override
    public Optional<Club> findClubByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return clubRepository.findByName(name);
    }
}
