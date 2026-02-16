package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.ClubMatch;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.ClubIdentityDto;
import za.co.hpsc.web.models.ipsc.dto.IdentityDto;
import za.co.hpsc.web.models.ipsc.dto.MatchIdentityDto;
import za.co.hpsc.web.models.ipsc.records.IpscRankingClubHolderRecord;
import za.co.hpsc.web.models.ipsc.records.IpscRankingMatchHolderRecord;
import za.co.hpsc.web.models.ipsc.request.IpscRankingClubRequest;
import za.co.hpsc.web.models.ipsc.request.IpscRankingMatchRequest;
import za.co.hpsc.web.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Javadoc
// TODO: add tests
// TODO: add comments
@Slf4j
@Service
public class IpscRankingServiceImpl implements IpscRankingService {
    protected final ClubEntityService clubEntityService;
    protected final MatchEntityService matchEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final ClubMatchEntityService clubMatchEntityService;

    public IpscRankingServiceImpl(ClubEntityService clubEntityService,
                                  MatchEntityService matchEntityService,
                                  MatchCompetitorEntityService matchCompetitorEntityService,
                                  ClubMatchEntityService clubMatchEntityService) {

        this.clubEntityService = clubEntityService;
        this.matchEntityService = matchEntityService;
        this.matchCompetitorEntityService = matchCompetitorEntityService;
        this.clubMatchEntityService = clubMatchEntityService;
    }

    @Override
    public IpscRankingClubHolderRecord initClubRankingEntities(MatchEntityHolder matchEntityHolder) {
        return null;
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
            refreshRankings(optionalClubIdentity.get());
        }

        // TODO: initialise the holder
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
            refreshRankings(optionalMatchIdentity.get());
        }

        // TODO: initialise the holder
        return new IpscRankingMatchHolderRecord(new ArrayList<>());
    }

    protected Optional<ClubIdentityDto> initClubIdentity(String clubName) {
        if (clubName == null || clubName.isBlank()) {
            return Optional.empty();
        }

        Optional<Club> optionalClubEntity = clubEntityService.findClub(clubName);
        if (optionalClubEntity.isPresent()) {
            Club clubEntity = optionalClubEntity.get();
            List<ClubMatch> clubMatchEntities = clubMatchEntityService.findClubMatches(clubEntity);
            ClubIdentityDto identityDto = new ClubIdentityDto(clubEntity, clubMatchEntities, clubName);
            return Optional.of(identityDto);
        }

        return Optional.empty();
    }

    protected Optional<MatchIdentityDto> initMatchIdentity(String clubName, String matchName) {
        if ((clubName == null) || (clubName.isBlank()) || (matchName == null) || (matchName.isBlank())) {
            return Optional.empty();
        }

        Optional<Club> optionalClubEntity = clubEntityService.findClub(clubName);
        Optional<IpscMatch> optionalMatchEntity = matchEntityService.findMatch(matchName);
        if (optionalClubEntity.isPresent() && (optionalMatchEntity.isPresent())) {
            Club clubEntity = optionalClubEntity.get();
            IpscMatch matchEntity = optionalMatchEntity.get();
            MatchIdentityDto identityDto = new MatchIdentityDto(clubEntity, matchEntity, clubName, matchName);
            return Optional.of(identityDto);
        }

        return Optional.empty();
    }

    protected boolean isRefreshRequired(IdentityDto identityDto) {
        if (identityDto == null) {
            return false;
        }
        return identityDto.isRefreshRequired();
    }

    protected void refreshRankings(ClubIdentityDto clubIdentity) {
        if ((clubIdentity != null) && (clubIdentity.getClubMatchEntities() != null)) {
            clubIdentity.refreshRankings();
        }
    }

    protected void refreshRankings(MatchIdentityDto matchIdentity) {
        if (matchIdentity != null) {
            matchIdentity.refreshRankings();
        }
    }
}
