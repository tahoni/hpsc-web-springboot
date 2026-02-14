package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IpscMatchResultServiceImpl implements IpscMatchResultService {
    protected final ClubEntityService clubEntityService;
    protected final MatchEntityService matchEntityService;
    protected final CompetitorEntityService competitorEntityService;
    protected final MatchStageEntityService matchStageEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    public IpscMatchResultServiceImpl(ClubEntityService clubEntityService, MatchEntityService matchEntityService,
                                      CompetitorEntityService competitorEntityService, MatchStageEntityService matchStageEntityService,
                                      MatchCompetitorEntityService matchCompetitorEntityService,
                                      MatchStageCompetitorEntityService matchStageCompetitorEntityService) {

        this.clubEntityService = clubEntityService;
        this.matchEntityService = matchEntityService;
        this.competitorEntityService = competitorEntityService;
        this.matchStageEntityService = matchStageEntityService;
        this.matchCompetitorEntityService = matchCompetitorEntityService;
        this.matchStageCompetitorEntityService = matchStageCompetitorEntityService;
    }

    @Override
    public Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse) {
        if (ipscResponse == null) {
            return Optional.empty();
        }

        // Initialises club and match
        Optional<ClubDto> optionalClub = initClub(ipscResponse.getClub());
        Optional<MatchDto> optionalMatch = initMatch(ipscResponse, optionalClub.orElse(null));
        if (optionalMatch.isEmpty()) {
            return Optional.empty();
        }

        // Initialises match results
        MatchDto match = optionalMatch.get();
        MatchResultsDto matchResultsDto = new MatchResultsDto(match);
        matchResultsDto.setStages(initStages(match, ipscResponse.getStages()));
        initScores(matchResultsDto, ipscResponse);

        // Sets the date updated
        match.setDateUpdated(LocalDateTime.now());

        return Optional.of(matchResultsDto);
    }

    /**
     * Initialises a club based on the given club information.
     *
     * @param clubResponse the {@link ClubResponse} object containing club information.
     * @return an {@code Optional} containing the initialized {@link ClubDto},
     * or an empty {@code Optional} if the club response is null.
     */
    protected Optional<ClubDto> initClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        // Attempts to find the club by name and abbreviation in the database
        Optional<Club> optionalClub =
                clubEntityService.findClub(clubResponse.getClubName(), clubResponse.getClubCode());

        // Creates a new club DTO, from either the found entity or the club response
        ClubDto clubDto = optionalClub
                .map(ClubDto::new)
                .orElseGet(() -> new ClubDto(clubResponse));
        clubDto.setIndex(clubResponse.getClubId());
        clubDto.init(clubResponse);

        return Optional.of(clubDto);
    }

    /**
     * Initialises or updates a match based on the provided IPSC response and club information.
     * If the match already exists in the database and no newer scores are present, the method
     * skips the update and returns an empty {@code Optional}. Otherwise, it creates or updates
     * the match data based on the IPSC response.
     *
     * @param ipscResponse the {@link IpscResponse} object containing match and score information.
     *                     Must not be null.
     * @param clubDto      the club details associated with the match.
     * @return an {@code Optional} containing the initialised or updated {@link MatchDto},
     * or an empty {@code Optional} if the match exists but no newer scores are present.
     */
    protected Optional<MatchDto> initMatch(IpscResponse ipscResponse, ClubDto clubDto) {
        if ((ipscResponse == null) || (ipscResponse.getMatch() == null)) {
            return Optional.empty();
        }

        // Attempts to find the match by name and date in the database
        Optional<IpscMatch> optionalMatch =
                matchEntityService.findMatch(ipscResponse.getMatch().getMatchName());
        boolean ipscMatchExists = optionalMatch.isPresent();
        boolean ipscResponseHasNewerScore = false;
        // Determines the last updated date of the match
        LocalDateTime matchLastUpdated = (optionalMatch.isPresent() ?
                optionalMatch.get().getDateUpdated() : LocalDateTime.now());

        // Skips update if there are no newer scores in the IPSC response
        if (ipscMatchExists) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.empty();
            }
        }

        // Creates a new match DTO, from either the found entity or the match response
        MatchDto matchDto = optionalMatch.map(match -> new MatchDto(match, clubDto))
                .orElseGet(() -> new MatchDto(ipscResponse.getMatch(), clubDto));
        matchDto.init(ipscResponse.getMatch(), null, new ArrayList<>());

        // Initialises match attributes
        matchDto.init(ipscResponse.getMatch(), clubDto, ipscResponse.getScores());
        return Optional.of(matchDto);
    }

    /**
     * Initialises a list of {@link MatchStageDto} objects based on the given match details
     * and stage response data. If no stage responses are provided, an empty list is returned.
     *
     * @param matchDto       the match details.
     *                       Must not be null.
     * @param stageResponses a list of stage response objects.
     *                       Can be null.
     * @return a list of initialized {@link MatchStageDto} objects based on the input parameters.
     */
    protected List<MatchStageDto> initStages(@NotNull MatchDto matchDto, List<StageResponse> stageResponses) {
        if (stageResponses == null) {
            return new ArrayList<>();
        }

        List<MatchStageDto> matchStageDtoList = new ArrayList<>();
        // Iterates through each stage response
        stageResponses.forEach(stageResponse -> {
            // Attempts to find the match stage by match ID and stage ID in the database
            Optional<IpscMatchStage> optionalMatchStage = matchStageEntityService.findMatchStage(matchDto.getId(),
                    stageResponse.getStageId());
            // Creates a new stage DTO, from either the found entity or the stage response
            MatchStageDto matchStageDto = optionalMatchStage
                    .map(ms -> new MatchStageDto(ms, matchDto))
                    .orElseGet(MatchStageDto::new);

            // Initialises stage attributes
            matchStageDto.init(matchDto, stageResponse);
            matchStageDtoList.add(matchStageDto);
        });

        return matchStageDtoList;
    }

    /**
     * Initialises the scores and competitors information for a given match results DTO
     * based on the data from the IPSC response. It processes score and member responses,
     * matches them to existing competitors or creates new ones, and updates the match results DTO
     * with the complete competitor and scoring information.
     *
     * @param matchResultsDto the data transfer object for match results to be initialised with
     *                        scores and competitors.
     * @param ipscResponse    the response containing score and member data from an IPSC source.
     */
    protected void initScores(@NotNull MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
        if ((ipscResponse == null) || (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return;
        }

        List<ScoreResponse> scoreResponses = ipscResponse.getScores();
        List<MemberResponse> memberResponses = ipscResponse.getMembers();
        // Maps score responses to corresponding member responses
        Set<Integer> memberIdsWithScores = scoreResponses.stream()
                .map(ScoreResponse::getMemberId)
                .collect(Collectors.toSet());
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(memberResponse -> memberIdsWithScores
                        .contains(memberResponse.getMemberId()))
                .toList();

        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        Map<Integer, EnrolledResponse> enrolledMap = new HashMap<>();
        // Iterates through each member response
        scoreMembers.forEach(memberResponse -> {
            // Attempts to find the competitor by ICS alias, first name, last name, and
            // date of birth in the database
            Optional<Competitor> optionalCompetitor =
                    competitorEntityService.findCompetitor(memberResponse.getIcsAlias(),
                            memberResponse.getFirstName(), memberResponse.getLastName(),
                            memberResponse.getDateOfBirth());
            // Creates a new competitor DTO, from either the found entity or the member response
            CompetitorDto competitorDto = optionalCompetitor
                    .map(CompetitorDto::new)
                    .orElseGet(CompetitorDto::new);
            competitorDto.setIndex(memberResponse.getMemberId());

            // Initialises the enrolled response to use to initialise the scores for each
            // competitor per match and stage
            EnrolledResponse enrolledResponse = ipscResponse.getEnrolledMembers().stream()
                    .filter(er -> er.getMemberId().equals(memberResponse.getMemberId()))
                    .findFirst()
                    .orElse(null);

            // Initialises competitor attributes
            competitorDto.init(memberResponse, enrolledResponse);

            // Caches the competitor and enrolled response for later use
            competitorDtoMap.put(memberResponse.getMemberId(), competitorDto);
            enrolledMap.put(memberResponse.getMemberId(), enrolledResponse);
        });
        // Collects all competitors in the match results DTO
        matchResultsDto.setCompetitors(new ArrayList<>(competitorDtoMap.values()));

        List<MatchCompetitorDto> matchCompetitorDtoList = matchResultsDto.getMatchCompetitors();
        // Iterates through each competitor
        competitorDtoMap.keySet().forEach(memberId -> {
            // Gets the competitor DTO from the map
            CompetitorDto competitorDto = competitorDtoMap.get(memberId);
            // Filters scores by member ID
            List<ScoreResponse> scores = scoreResponses.stream()
                    .filter(sr -> sr.getMemberId().equals(memberId))
                    .toList();

            // Attempts to find the match competitor by competitor ID and match ID in the database
            Optional<MatchCompetitor> optionalMatchCompetitor = Optional.empty();
            if (matchResultsDto.getMatch().getId() != null) {
                optionalMatchCompetitor =
                        matchCompetitorEntityService.findMatchCompetitor(competitorDto.getId(),
                                matchResultsDto.getMatch().getId());
            }

            // Creates a new match competitor DTO, from either the found entity or the
            // competitor DTO
            MatchCompetitorDto matchCompetitorDto = optionalMatchCompetitor
                    .map(MatchCompetitorDto::new)
                    .orElse(new MatchCompetitorDto(competitorDto, matchResultsDto.getMatch()));
            matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
            matchCompetitorDto.setMatchIndex(matchResultsDto.getMatch().getIndex());

            // Initialises match competitor attributes
            matchCompetitorDto.init(scores, enrolledMap.get(memberId));
            matchCompetitorDtoList.add(matchCompetitorDto);

            // Gets the match stage competitors from the match results DTO
            List<MatchStageCompetitorDto> matchStageCompetitorDtoList =
                    matchResultsDto.getMatchStageCompetitors();
            // Iterates through each stage
            matchResultsDto.getStages().forEach(stageDto -> {
                // Filters scores by stage ID (already filtered by member ID)
                Optional<ScoreResponse> optionalStageScoreResponse = scores.stream()
                        .filter(scoreResponse -> stageDto.getStageNumber()
                                .equals(scoreResponse.getStageId()))
                        .findFirst();
                if (optionalStageScoreResponse.isPresent()) {
                    // Attempts to find the match stage competitor by competitor ID, stage ID,
                    // and match ID
                    Optional<MatchStageCompetitor> optionalMatchStageCompetitor =
                            matchStageCompetitorEntityService.findMatchStageCompetitor(stageDto, competitorDto);
                    // Creates a new match stage competitor DTO, from either the found entity
                    // or the competitor DTO
                    MatchStageCompetitorDto matchStageCompetitorDto = optionalMatchStageCompetitor
                            .map(MatchStageCompetitorDto::new)
                            .orElse(new MatchStageCompetitorDto(competitorDto, stageDto));
                    matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                    // Initialises the match stage attributes
                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                            enrolledMap.get(memberId), stageDto);
                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);
                }
            });
        });
    }
}
