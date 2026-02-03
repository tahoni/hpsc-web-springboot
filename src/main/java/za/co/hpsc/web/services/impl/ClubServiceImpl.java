package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
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

    // TODO: Javadoc
    @Override
    public Optional<Club> findClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        Optional<Club> club = Optional.empty();
        // Attempt to find the club by name
        if ((clubResponse.getClubName() != null) && (!clubResponse.getClubName().isBlank())) {
            club = clubRepository.findByName(clubResponse.getClubName());
        }
        // If no club with the name was found
        if (club.isEmpty() && (clubResponse.getClubCode() != null) && (!clubResponse.getClubCode().isBlank())) {
            // Attempt to find the club by abbreviation
            club = clubRepository.findByAbbreviation(clubResponse.getClubCode());
        }

        return club;
    }
}
