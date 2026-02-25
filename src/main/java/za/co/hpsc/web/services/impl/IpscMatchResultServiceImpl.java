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

    public IpscMatchResultServiceImpl(ClubEntityService clubEntityService,
                                      MatchEntityService matchEntityService,
                                      CompetitorEntityService competitorEntityService,
                                      MatchStageEntityService matchStageEntityService,
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

        // Initialises match details
        MatchDto match = optionalMatch.get();
        MatchResultsDto matchResultsDto = new MatchResultsDto(match);
        matchResultsDto.setClub(optionalClub.orElse(null));
        matchResultsDto.setStages(initStages(match, ipscResponse.getStages()));

        // Initialises match results
        initScores(matchResultsDto, ipscResponse);

        return Optional.of(matchResultsDto);
    }

    /**
     * Initialises a {@link ClubDto} object based on the provided club response.
     *
     * <p>
     * If a matching club entity with the same name and abbreviation is found in the database,`
     * it is used to populate the DTO; otherwise, a new DTO is created and initialised
     * using the club response.
     * </p>
     *
     * @param clubResponse the response containing club information, which may be used to initialise the DTO.
     *                     If null, the method will return an empty optional.
     * @return an {@code Optional} containing the initialized {@link ClubDto}, or {@code Optional.empty}
     * if the provided clubResponse is null.
     */
    protected Optional<ClubDto> initClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        // Attempts to find the club by name or abbreviation in the database
        Optional<Club> optionalClub =
                clubEntityService.findClubByNameOrAbbreviation(clubResponse.getClubName(),
                        clubResponse.getClubCode());

        // Creates a new club DTO, from either the found entity or the match response
        Club club = optionalClub.orElse(null);
        ClubDto clubDto = ((club != null) ? new ClubDto(club) : new ClubDto());
        clubDto.init(clubResponse);

        return Optional.of(clubDto);
    }

    /**
     * Initialises a {@link MatchDto} object based on the provided IPSC response data
     * and club information.
     *
     * <p>The method checks if the match exists in the database, determines whether the response contains
     * newer scores, and skips updates if applicable. If the match is valid, it creates or updates the
     * match DTO and initialises its attributes.
     * </p>
     *
     * @param ipscResponse The response object containing match details and scores from the IPSC system.
     * @param clubDto      The club data transfer object containing information about the club associated with the match.
     * @return An {@code Optional} containing the initialised {@link MatchDto} if the match is valid
     * and meets the update criteria, or {@code Optional.empty()} if no match is initialised.
     */
    protected Optional<MatchDto> initMatch(IpscResponse ipscResponse, ClubDto clubDto) {
        if ((ipscResponse == null) || (ipscResponse.getMatch() == null)) {
            return Optional.empty();
        }

        // Attempts to find the match by name in the database
        Optional<IpscMatch> optionalMatch =
                matchEntityService.findMatchByName(ipscResponse.getMatch().getMatchName());
        boolean ipscMatchExists = optionalMatch.isPresent();
        boolean ipscResponseHasNewerScore = false;

        // Determines the last updated date of the match
        LocalDateTime matchLastUpdated = (optionalMatch.isPresent() ?
                optionalMatch.get().getDateUpdated() : LocalDateTime.MIN);
        if (matchLastUpdated == null) {
            matchLastUpdated = LocalDateTime.MIN;
        }

        // Skips update if there are no newer scores in the IPSC response
        if ((ipscMatchExists) && (ipscResponse.getScores() != null) && (!ipscResponse.getScores().isEmpty())) {
            LocalDateTime finalMatchLastUpdated = matchLastUpdated;
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .filter(Objects::nonNull)
                    .allMatch(sr -> finalMatchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.empty();
            }
        }

        // Creates a new match DTO, from either the found entity or the match response
        MatchDto matchDto = optionalMatch.map(MatchDto::new).orElseGet(MatchDto::new);

        // Initialises match attributes
        matchDto.init(ipscResponse.getMatch(), clubDto, ipscResponse.getScores());

        return Optional.of(matchDto);
    }

    /**
     * Initialises a list of {@link MatchStageDto} objects based on the given match details
     * and stage response data. If no stage responses are provided, an empty list is returned.
     *
     * @param matchDto       the match details.
     * @param stageResponses a list of stage response objects.
     * @return a list of initialised {@link MatchStageDto} objects based on the input parameters.
     */
    protected List<MatchStageDto> initStages(MatchDto matchDto, List<StageResponse> stageResponses) {
        if ((matchDto == null) || (stageResponses == null)) {
            return new ArrayList<>();
        }

        List<MatchStageDto> matchStageDtoList = new ArrayList<>();
        // Iterates through each stage response
        stageResponses.stream().filter(Objects::nonNull)
                .forEach(stageResponse -> {
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
     * Initialises the scores and competitors for a match based on the provided match results and IPSC response data.
     *
     * @param matchResultsDto The data transfer object containing information about the current match,
     *                        including scores, competitors, and stages, to be updated.
     * @param ipscResponse    The response containing the scores, members, and other related data fetched
     *                        from the IPSC system. May include enrolled members and finalised scores.
     */
    // TODO: update tests
    protected void initScores(@NotNull MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
        if ((ipscResponse == null) || (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return;
        }

        // Filters score responses to those relevant to the current match
        List<ScoreResponse> scoreResponses = ipscResponse.getScores().stream()
                .filter(Objects::nonNull)
                .filter(scoreResponse -> Objects.equals(scoreResponse.getMatchId(), matchResultsDto.getMatch().getIndex()))
                .toList();
        List<ScoreDto> scoreDtos = scoreResponses.stream()
                .filter(Objects::nonNull)
                .map(ScoreDto::new)
                .toList();
        List<MemberResponse> memberResponses = ipscResponse.getMembers();

        // Maps score responses to corresponding member responses, excluding members who didn't participate
        Set<Integer> memberIdsWithScores = scoreResponses.stream()
                .filter(Objects::nonNull)
                .filter(scoreResponse -> scoreResponse.getFinalScore() != null)
                .filter(scoreResponse -> scoreResponse.getFinalScore() != 0)
                .map(ScoreResponse::getMemberId)
                .collect(Collectors.toSet());
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(Objects::nonNull)
                .filter(memberResponse -> memberIdsWithScores
                        .contains(memberResponse.getMemberId()))
                .toList();

        matchResultsDto.setScores(scoreDtos);

        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        Map<Integer, EnrolledResponse> enrolledResponseMap = new HashMap<>();
        // Iterates through each member response
        scoreMembers.stream().filter(Objects::nonNull)
                .forEach(memberResponse -> {
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
                            .filter(Objects::nonNull)
                            .filter(er -> er.getMemberId().equals(memberResponse.getMemberId()))
                            .findFirst()
                            .orElse(null);

                    // Initialises competitor attributes
                    competitorDto.init(memberResponse, enrolledResponse);

                    // Caches the competitor and enrolled response for later use
                    competitorDtoMap.put(memberResponse.getMemberId(), competitorDto);
                    enrolledResponseMap.put(memberResponse.getMemberId(), enrolledResponse);
                });

        List<CompetitorDto> competitorDtoList = enrolledResponseMap.keySet().stream()
                .map(competitorDtoMap::get)
                .toList();
        matchResultsDto.setCompetitors(competitorDtoList);

        // Ensure the match and stage competitor list fields are not null
        List<MatchCompetitorDto> matchCompetitorDtoList = new ArrayList<>();
        List<MatchStageCompetitorDto> matchStageCompetitorDtoList = new ArrayList<>();
        if (matchResultsDto.getMatchCompetitors() == null) {
            matchResultsDto.setMatchCompetitors(new ArrayList<>());
        }
        if (matchResultsDto.getMatchStageCompetitors() == null) {
            matchResultsDto.setMatchStageCompetitors(new ArrayList<>());
        }

        // Iterates through each competitor
        competitorDtoMap.keySet().stream().filter(Objects::nonNull)
                .forEach(memberIndex -> {
                    // Gets the competitor DTO from the map
                    CompetitorDto competitorDto = competitorDtoMap.get(memberIndex);
                    // Filters scores by member ID
                    List<ScoreResponse> scores = scoreResponses.stream()
                            .filter(Objects::nonNull)
                            .filter(sr -> Objects.equals(sr.getMemberId(), memberIndex))
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
                    matchCompetitorDto.init(scores, enrolledResponseMap.get(memberIndex));
                    matchCompetitorDtoList.add(matchCompetitorDto);

                    // Gets the match stage competitors from the match results DTO
                    // Iterates through each stage
                    matchResultsDto.getStages().stream().filter(Objects::nonNull)
                            .forEach(stageDto -> {
                                // Filters scores by stage ID (already filtered by member ID)
                                Optional<ScoreResponse> optionalStageScoreResponse = scores.stream()
                                        .filter(Objects::nonNull)
                                        .filter(scoreResponse -> stageDto.getStageNumber()
                                                .equals(scoreResponse.getStageId()))
                                        .findFirst();
                                optionalStageScoreResponse.ifPresent(stageScoreResponse -> {
                                    // Attempts to find the match stage competitor by competitor ID, stage ID,
                                    // and match ID
                                    Optional<MatchStageCompetitor> optionalMatchStageCompetitor =
                                            matchStageCompetitorEntityService.findMatchStageCompetitor(stageDto.getId(),
                                                    competitorDto.getId());
                                    // Creates a new match stage competitor DTO, from either the found entity
                                    // or the competitor DTO
                                    MatchStageCompetitorDto matchStageCompetitorDto = optionalMatchStageCompetitor
                                            .map(MatchStageCompetitorDto::new)
                                            .orElse(new MatchStageCompetitorDto(competitorDto, stageDto));
                                    matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
                                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                                    // Initialises the match stage attributes
                                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                                            enrolledResponseMap.get(memberIndex), stageDto);
                                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);
                                });
                                if (optionalStageScoreResponse.isPresent()) {
                                    // Attempts to find the match stage competitor by competitor ID, stage ID,
                                    // and match ID
                                    Optional<MatchStageCompetitor> optionalMatchStageCompetitor =
                                            matchStageCompetitorEntityService.findMatchStageCompetitor(stageDto.getId(),
                                                    competitorDto.getId());
                                    // Creates a new match stage competitor DTO, from either the found entity
                                    // or the competitor DTO
                                    MatchStageCompetitorDto matchStageCompetitorDto = optionalMatchStageCompetitor
                                            .map(MatchStageCompetitorDto::new)
                                            .orElse(new MatchStageCompetitorDto(competitorDto, stageDto));
                                    matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
                                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                                    // Initialises the match stage attributes
                                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                                            enrolledResponseMap.get(memberIndex), stageDto);
                                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);

                                }
                            });
                });

        // Collects all competitors in the match results DTO
        matchResultsDto.setCompetitors(competitorDtoMap.values().stream().filter(Objects::nonNull).toList());
        // Collects all match competitors in the match results DTO
        matchResultsDto.getMatchCompetitors().addAll(matchCompetitorDtoList);
        // Collects all stage competitors in the match results DTO
        matchResultsDto.getMatchStageCompetitors().addAll(matchStageCompetitorDtoList);
    }
}
