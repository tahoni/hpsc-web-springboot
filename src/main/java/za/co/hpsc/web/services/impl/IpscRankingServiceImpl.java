package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.dto.ClubIdentityDto;
import za.co.hpsc.web.models.ipsc.dto.IdentityDto;
import za.co.hpsc.web.models.ipsc.dto.MatchIdentityDto;
import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;
import za.co.hpsc.web.models.ipsc.request.IpscRankingClubRequest;
import za.co.hpsc.web.models.ipsc.request.IpscRankingMatchRequest;
import za.co.hpsc.web.services.ClubEntityService;
import za.co.hpsc.web.services.IpscRankingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Javadoc
// TODO: add tests
// TODO: add comments
@Slf4j
@Service
public class IpscRankingServiceImpl implements IpscRankingService {
    private final ClubEntityService clubEntityService;

    public IpscRankingServiceImpl(ClubEntityService clubEntityService) {
        this.clubEntityService = clubEntityService;
    }

    @Override
    public IpscRankingClubHolderRecord refreshClubRankings(IpscRankingClubRequest rankingClubRequest) {
        if (rankingClubRequest == null) {
            return new IpscRankingClubHolderRecord(new ArrayList<>());
        }
        if ((rankingClubRequest.getClubName() == null) || (rankingClubRequest.getClubName().isBlank())) {
            return new IpscRankingClubHolderRecord(new ArrayList<>());
        }

        Optional<ClubIdentityDto> optionalClubIdentity = initClubIdentity(rankingClubRequest.getClubName());
        boolean mustRefresh = false;
        if (optionalClubIdentity.isPresent()) {
            mustRefresh = isRefreshRequired(optionalClubIdentity.get());
        }

        if (mustRefresh) {
            return refreshRankings(optionalClubIdentity.get());
        }
        return new IpscRankingClubHolderRecord(new ArrayList<>());
    }

    @Override
    public IpscRankingMatchHolderRecord refreshMatchRankings(IpscRankingMatchRequest rankingMatchRequest) {
        if (rankingMatchRequest == null) {
            return new IpscRankingMatchHolderRecord(new ArrayList<>());
        }
        if ((rankingMatchRequest.getClubName() == null) || (rankingMatchRequest.getClubName().isBlank())) {
            return new IpscRankingMatchHolderRecord(new ArrayList<>());
        }
        if ((rankingMatchRequest.getMatchName() == null) || (rankingMatchRequest.getMatchName().isBlank())) {
            return new IpscRankingMatchHolderRecord(new ArrayList<>());
        }

        String clubName = rankingMatchRequest.getClubName();
        String matchName = rankingMatchRequest.getMatchName();

        Optional<MatchIdentityDto> optionalMatchIdentity = initMatchIdentity(clubName, matchName);
        boolean mustRefresh = false;
        if (optionalMatchIdentity.isPresent()) {
            mustRefresh = isRefreshRequired(optionalMatchIdentity.get());
        }

        if (mustRefresh) {
            return refreshRankings(optionalMatchIdentity.get());
        }
        return new IpscRankingMatchHolderRecord(new ArrayList<>());
    }

    protected Optional<ClubIdentityDto> initClubIdentity(String clubName) {
        if (clubName == null || clubName.isEmpty()) {
            return Optional.empty();
        }

        // TODO: implement this method to find the club entity and club identifier associated with the club name
        Optional<Club> optionalClub = clubEntityService.findClub(clubName);
        Optional<ClubIdentifier> optionalClubIdentifier = ClubIdentifier.getByName(clubName);
        Club clubEntity = optionalClub.orElse(null);
        ClubIdentifier clubIdentifier = optionalClubIdentifier.orElse(ClubIdentifier.UNKNOWN);

        if ((clubEntity != null) || (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
            // TODO: implement this method to find matches associated with the club entity and club identifier
            List<IpscMatch> matchEntities = new ArrayList<>();
            ClubIdentityDto identityDto = new ClubIdentityDto(clubEntity, clubIdentifier, matchEntities, clubName);
            return Optional.of(identityDto);
        }

        return Optional.empty();
    }

    protected Optional<MatchIdentityDto> initMatchIdentity(String clubName, String matchName) {
        if ((clubName == null) || (clubName.isBlank()) || (matchName == null) || (matchName.isBlank())) {
            return Optional.empty();
        }

        // TODO: implement this method to find the club identity associated with the club name
        Optional<ClubIdentityDto> optionalClubIdentityDto = initClubIdentity(clubName);
        Optional<IpscMatch> optionalMatchEntity = Optional.empty();
        Optional<MatchIdentityDto> optionalMatchIdentity = Optional.empty();

        if ((optionalClubIdentityDto.isPresent()) && (optionalMatchEntity.isPresent())) {
            MatchIdentityDto matchIdentity = new MatchIdentityDto(optionalClubIdentityDto.get().getClubEntity(),
                    optionalClubIdentityDto.get().getClubIdentifier(), optionalMatchEntity.get(), clubName, matchName);
            optionalMatchIdentity = Optional.of(matchIdentity);
        }
        return optionalMatchIdentity;
    }

    protected boolean isRefreshRequired(IdentityDto identityDto) {
        if (identityDto == null) {
            return false;
        }
        return identityDto.isRefreshRequired();
    }

    protected IpscRankingClubHolderRecord refreshRankings(ClubIdentityDto clubIdentity) {
        // TODO: implement this method to refresh the club rankings based on the provided club identity
        return null;
    }

    protected IpscRankingMatchHolderRecord refreshRankings(MatchIdentityDto matchIdentity) {
        // TODO: implement this method to refresh the match rankings based on the provided match identity
        return null;
    }
}
