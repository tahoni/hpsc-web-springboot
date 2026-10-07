package za.co.hpsc.web.services.impl;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.services.IpscEntityClubService;

@Service
public class IpscEntityClubServiceImpl implements IpscEntityClubService {
    @Override
    public boolean isSameClub(Club club, ClubIdentifier targetClubIdentifier) {
        return (targetClubIdentifier != null) && (club != null) && (club.getIdentifier() == targetClubIdentifier);
    }

    @Override
    public boolean isSameClub(ClubIdentifier clubIdentifier, ClubIdentifier targetClubIdentifier) {
        return (clubIdentifier != null) && (clubIdentifier == targetClubIdentifier);
    }
}
