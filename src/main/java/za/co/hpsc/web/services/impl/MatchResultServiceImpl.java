package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.models.match.*;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchResultServiceImpl implements MatchResultService {
    protected final ClubService clubService;
    protected final MatchService matchService;
    protected final CompetitorService competitorService;
    protected final MatchStageService matchStageService;

    public MatchResultServiceImpl(ClubService clubService, MatchService matchService,
                                  CompetitorService competitorService, MatchStageService matchStageService) {

        this.clubService = clubService;
        this.matchService = matchService;
        this.competitorService = competitorService;
        this.matchStageService = matchStageService;
    }

    @Override
    public Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse) {
        Optional<ClubDto> optionalClub = initClub(ipscResponse.getClub());
        Optional<MatchDto> optionalMatch = initMatch(ipscResponse, optionalClub.orElse(null));
        if (optionalMatch.isEmpty()) {
            return Optional.empty();
        }

        MatchDto match = optionalMatch.get();
        MatchResultsDto matchResultsDto = new MatchResultsDto(match);
        matchResultsDto.setStages(initStages(match, ipscResponse.getStages()));
        initScores(matchResultsDto, ipscResponse);

        match.setDateUpdated(LocalDateTime.now());

        return Optional.of(matchResultsDto);
    }

    protected Optional<ClubDto> initClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        Optional<Club> optionalClub = clubService.findClub(clubResponse);
        ClubDto clubDto = null;
        clubDto = optionalClub.map(ClubDto::new).orElseGet(ClubDto::new);
        return Optional.of(clubDto);
    }

    // TODO: Javadoc
    protected Optional<MatchDto> initMatch(@NotNull IpscResponse ipscResponse, ClubDto clubDto) {
        // Get the match from the database if it exists
        Optional<Match> optionalMatch = matchService.findMatch(ipscResponse.getMatch());
        boolean ipscMatchExists = optionalMatch.isPresent();
        boolean ipscResponseHasNewerScore = false;
        LocalDateTime matchLastUpdated = (optionalMatch.isPresent() ?
                optionalMatch.get().getDateUpdated() : LocalDateTime.now());

        // Skips update if no newer score; otherwise creates match
        if (ipscMatchExists) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.empty();
            }
        }


        MatchDto matchDto = optionalMatch.map(match -> new MatchDto(match, clubDto)).orElseGet(MatchDto::new);

        // Initialise match attributes
        matchDto.init(ipscResponse.getMatch(), clubDto);
        return Optional.of(matchDto);
    }

    // TODO: Javadoc
    protected List<MatchStageDto> initStages(@NotNull MatchDto matchDto, List<StageResponse> stageResponses) {
        if (stageResponses == null) {
            return new ArrayList<>();
        }

        List<MatchStageDto> matchStageDtoList = new ArrayList<>();
        // Initialises stage attributes
        stageResponses.forEach(stageResponse -> {
            // TODO: get match entity
            Optional<MatchStage> optionalMatchStage = matchStageService.findMatchStage(matchDto.getId(),
                    stageResponse.getStageId());
            MatchStageDto matchStageDto = optionalMatchStage.map(matchStage ->
                    new MatchStageDto(matchStage, matchDto)).orElseGet(MatchStageDto::new);
            matchStageDto.init(matchDto, stageResponse);
            matchStageDtoList.add(matchStageDto);
        });
        return matchStageDtoList;
    }

    // TODO: Javadoc
    protected void initScores(MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
        if ((matchResultsDto == null) || (ipscResponse == null) ||
                (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return;
        }

        List<ScoreResponse> scoreResponses = ipscResponse.getScores();
        List<MemberResponse> memberResponses = ipscResponse.getMembers();
        // Maps score responses to corresponding member responses
        Set<Integer> memberIdsWithScores = scoreResponses.stream()
                .map(ScoreResponse::getMemberId)
                .collect(Collectors.toSet());
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(memberResponse -> memberIdsWithScores.contains(memberResponse.getMemberId()))
                .toList();

        // Initialises competitor attributes
        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        scoreMembers.forEach(memberResponse -> {
            Optional<Competitor> optionalCompetitor = competitorService.findCompetitor(memberResponse);
            CompetitorDto competitorDto =
                    optionalCompetitor.map(CompetitorDto::new).orElseGet(CompetitorDto::new);
            competitorDto.init(memberResponse);
            competitorDtoMap.put(memberResponse.getMemberId(), competitorDto);
        });
        matchResultsDto.setCompetitors(new ArrayList<>(competitorDtoMap.values()));

        List<MatchCompetitorDto> matchCompetitorDtos = matchResultsDto.getMatchCompetitors();
        // Find the match overall result
        competitorDtoMap.keySet().forEach(memberId -> {
            CompetitorDto competitor = competitorDtoMap.get(memberId);
            List<ScoreResponse> scores = scoreResponses.stream()
                    .filter(sr -> sr.getMemberId().equals(memberId))
                    .toList();
            MatchCompetitorDto matchCompetitorDto =
                    new MatchCompetitorDto(competitor, matchResultsDto.getMatch());
            matchCompetitorDto.init(scores);
            matchCompetitorDtos.add(matchCompetitorDto);

            List<MatchStageCompetitorDto> matchStageCompetitorDtos = matchResultsDto.getMatchStageCompetitors();
            // Find the stage results
            matchResultsDto.getStages().forEach(stageDto -> {
                Optional<ScoreResponse> optionalStageScoreResponse = scores.stream()
                        .filter(scoreResponse -> stageDto.getStageNumber().equals(scoreResponse.getStageId()))
                        .findFirst();
                if (optionalStageScoreResponse.isPresent()) {
                    MatchStageCompetitorDto matchStageCompetitorDto =
                            new MatchStageCompetitorDto(matchCompetitorDto, stageDto);
                    matchStageCompetitorDto.init(optionalStageScoreResponse.get());
                    matchStageCompetitorDtos.add(matchStageCompetitorDto);
                }
            });
        });

        matchResultsDto.setCompetitors(new ArrayList<>(competitorDtoMap.values()));
    }
}
