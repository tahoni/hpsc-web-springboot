package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.ClubMatch;
import za.co.hpsc.web.repositories.ClubMatchRepository;
import za.co.hpsc.web.services.ClubMatchEntityService;

import java.util.List;

@Slf4j
@Service
public class ClubMatchEntityServiceImpl implements ClubMatchEntityService {
    protected final ClubMatchRepository clubMatchRepository;

    public ClubMatchEntityServiceImpl(ClubMatchRepository clubMatchRepository) {
        this.clubMatchRepository = clubMatchRepository;
    }

    @Override
    public List<ClubMatch> findClubMatches(Club clubEntity) {
        return clubMatchRepository.findAllByClub(clubEntity);
    }
}
