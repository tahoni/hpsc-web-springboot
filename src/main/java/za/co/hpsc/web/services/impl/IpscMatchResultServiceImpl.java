package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.MatchStageCompetitorRepository;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IpscMatchResultServiceImpl implements IpscMatchResultService {
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    protected final ClubEntityService clubEntityService;
    protected final MatchEntityService matchEntityService;
    protected final CompetitorEntityService competitorEntityService;
    protected final MatchStageEntityService matchStageEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    @Value("${hpsc-web.results.init-match-result.ignore-updated-date:false}")
    private Boolean ignoreDateUpdated;

    public IpscMatchResultServiceImpl(CompetitorRepository competitorRepository,
                                      IpscMatchStageRepository ipscMatchStageRepository,
                                      MatchCompetitorRepository matchCompetitorRepository,
                                      MatchStageCompetitorRepository matchStageCompetitorRepository,
                                      ClubEntityService clubEntityService,
                                      MatchEntityService matchEntityService,
                                      CompetitorEntityService competitorEntityService,
                                      MatchStageEntityService matchStageEntityService,
                                      MatchCompetitorEntityService matchCompetitorEntityService,
                                      MatchStageCompetitorEntityService matchStageCompetitorEntityService) {

        this.competitorRepository = competitorRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
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
        Optional<ClubDto> optionalClub = initClub(ipscResponse.getClub(), ClubIdentifier.HPSC);
        Optional<MatchDto> optionalMatch = initMatch(ipscResponse, optionalClub.orElse(null));
        if (optionalMatch.isEmpty()) {
            return Optional.empty();
        }

        // Initialises match details
        MatchDto match = optionalMatch.get();
        MatchResultsDto matchResultsDto = new MatchResultsDto(match);
        matchResultsDto.setStages(initStages(match, ipscResponse.getStages()));

        // Initialises match results
        initScores(matchResultsDto, ipscResponse);

        return Optional.of(matchResultsDto);
    }

    @Override
    public Optional<MatchEntityHolder> initMatchEntities(MatchResultsDto matchResults) {
        if (matchResults.getMatch() == null) {
            return Optional.empty();
        }

        AtomicReference<Optional<MatchEntityHolder>> optionalMatchEntityHolder =
                new AtomicReference<>(Optional.empty());

        Club club = initClubEntity(matchResults.getClub()).orElse(null);
        Optional<IpscMatch> optionalMatch = initMatchEntity(matchResults.getMatch(), club);

        optionalMatch.ifPresent(match -> {
            Map<UUID, Competitor> competitorMap = initCompetitorEntities(matchResults.getCompetitors());
            Map<UUID, IpscMatchStage> matchStageMap = initMatchStageEntities(matchResults.getStages(), match);

            Map<UUID, MatchCompetitor> matchCompetitorMap =
                    initMatchCompetitorEntities(matchResults.getMatchCompetitors(),
                            match, competitorMap, ClubIdentifier.HPSC);
            Map<UUID, MatchStageCompetitor> matchStageCompetitorMap =
                    initMatchStageCompetitorEntities(matchResults.getMatchStageCompetitors(),
                            matchStageMap, competitorMap, ClubIdentifier.HPSC);

            optionalMatchEntityHolder.set(Optional.of(new MatchEntityHolder(match, club,
                    matchStageMap.values().stream().toList(),
                    competitorMap.values().stream().toList(),
                    matchCompetitorMap.values().stream().toList(),
                    matchStageCompetitorMap.values().stream().toList())));
        });

        return optionalMatchEntityHolder.get();
    }

    /**
     * Initialises a club based on the given club information.
     *
     * @param clubResponse   the {@link ClubResponse} object containing club information.
     * @param clubIdentifier
     * @return an {@code Optional} containing the initialized {@link ClubDto},
     * or an empty {@code Optional} if the club response is null.
     */
    // TODO: Javadoc
    protected Optional<ClubDto> initClub(ClubResponse clubResponse, ClubIdentifier clubIdentifier) {
        if ((clubResponse == null) && ((clubIdentifier == null))) {
            return Optional.empty();
        }

        // Attempts to find the club by name or abbreviation in the database
        Optional<Club> optionalClub = Optional.empty();
        if ((clubResponse != null) && ((clubResponse.getClubName() != null) || (clubResponse.getClubCode() != null))) {
            optionalClub = clubEntityService.findClubByNameOrAbbreviation(clubResponse.getClubName(),
                    clubResponse.getClubCode());
        } else if ((clubIdentifier != null) && (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
            String clubIdentifierName = clubIdentifier.getName();
            optionalClub = clubEntityService.findClubByNameOrAbbreviation(clubIdentifierName,
                    clubIdentifierName);
        }

        // Creates a new club DTO, from either the found entity or the match response
        Club club = optionalClub.orElse(null);
        ClubDto clubDto = null;
        if (club != null) {
            clubDto = new ClubDto(club);
        } else if ((clubResponse != null) && ((clubResponse.getClubName() != null) || (clubResponse.getClubCode() != null))) {
            clubDto = new ClubDto(clubResponse);
        } else if ((clubIdentifier != null) && (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
            clubDto = new ClubDto(clubIdentifier);
        }

        if (clubDto != null) {
            clubDto.init(clubResponse);
        }
        return Optional.ofNullable(clubDto);
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

        // Attempts to find the match by name in the database
        Optional<IpscMatch> optionalMatch =
                matchEntityService.findMatchByName(ipscResponse.getMatch().getMatchName());
        boolean ipscMatchExists = optionalMatch.isPresent();
        boolean ipscResponseHasNewerScore = false;

        // Determines the last updated date of the match
        LocalDateTime matchLastUpdated = (optionalMatch.isPresent() ?
                optionalMatch.get().getDateUpdated() : LocalDateTime.now());

        // Skips update if there are no newer scores in the IPSC response
        boolean ignoreMatchDateUpdated = ((ignoreDateUpdated != null ? ignoreDateUpdated : false));
        if ((!ignoreMatchDateUpdated) && (ipscMatchExists)) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.empty();
            }
        }

        // Creates a new match DTO, from either the found entity or the match response
        MatchDto matchDto = optionalMatch.map(match -> new MatchDto(match, clubDto))
                .orElseGet(() -> new MatchDto(ipscResponse.getMatch(), clubDto));
        matchDto.init(ipscResponse.getMatch(), clubDto, new ArrayList<>());

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
        // Maps score responses to corresponding member responses, excluding members who didn't participate
        Set<Integer> memberIdsWithScores = scoreResponses.stream()
                .filter(scoreResponse -> scoreResponse.getFinalScore() != null)
                .filter(scoreResponse -> scoreResponse.getFinalScore() != 0)
                .map(ScoreResponse::getMemberId)
                .collect(Collectors.toSet());
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(memberResponse -> memberIdsWithScores
                        .contains(memberResponse.getMemberId()))
                .toList();

        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        Map<Integer, EnrolledResponse> enrolledResponseMap = new HashMap<>();
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
            enrolledResponseMap.put(memberResponse.getMemberId(), enrolledResponse);
        });
//        List<CompetitorDto> competitorDtoList = enrolledResponseMap.keySet().stream()
//                .map(competitorDtoMap::get)
//                .toList();
//        matchResultsDto.setCompetitors(competitorDtoList);


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
//            MatchCompetitorDto matchCompetitorDto = optionalMatchCompetitor
//                    .map(MatchCompetitorDto::new)
//                    .orElse(new MatchCompetitorDto(competitorDto, matchResultsDto.getMatch()));
//            matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
//            matchCompetitorDto.setMatchIndex(matchResultsDto.getMatch().getIndex());

            // Initialises match competitor attributes
//            matchCompetitorDto.init(scores, enrolledResponseMap.get(memberId));
//            matchCompetitorDtoList.add(matchCompetitorDto);

            // Gets the match stage competitors from the match results DTO
            // Iterates through each stage
            matchResultsDto.getStages().forEach(stageDto -> {
                // Filters scores by stage ID (already filtered by member ID)
                Optional<ScoreResponse> optionalStageScoreResponse = scores.stream()
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
//                    matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
//                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                    // Initialises the match stage attributes
                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                            enrolledResponseMap.get(memberId), stageDto);
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
//                    matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
//                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                    // Initialises the match stage attributes
                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                            enrolledResponseMap.get(memberId), stageDto);
                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);

                }
            });
        });

        // Collects all competitors in the match results DTO
//        matchResultsDto.setCompetitors(competitorDtoMap.values().stream().toList());
        // Collects all match competitors in the match results DTO
//        matchResultsDto.getMatchCompetitors().addAll(matchCompetitorDtoList);
        // Collects all stage competitors in the match results DTO
//        matchResultsDto.getMatchStageCompetitors().addAll(matchStageCompetitorDtoList);
    }

    // TODO: Javadoc
    protected Optional<Club> initClubEntity(ClubDto clubDto) {
        if (clubDto != null) {
            // Find the club entity if present
            Optional<Club> optionalClubEntity = clubEntityService.findClubById(clubDto.getId());

            // Initialise the club entity from DTO or create a new entity
            Club clubEntity = optionalClubEntity.orElse(new Club());

            // Add attributes to the club
            clubEntity.init(clubDto);
            return Optional.of(clubEntity);
        }

        return Optional.empty();
    }

    // TODO: Javadoc
    protected Optional<IpscMatch> initMatchEntity(@NotNull MatchDto matchDto, Club clubEntity) {
        // Find the match entity if present
        Optional<IpscMatch> optionalIpscMatchEntity = matchEntityService.findMatchById(matchDto.getId());

        // Initialise the match entity from DTO or create a new entity
        IpscMatch matchEntity = optionalIpscMatchEntity.orElse(new IpscMatch());

        // Add attributes to the match
        matchEntity.init(matchDto, clubEntity);

        // Link the match to the club
        matchEntity.setClub(clubEntity);

        return Optional.of(matchEntity);
    }

    // TODO: Javadoc
    protected Map<UUID, Competitor> initCompetitorEntities(List<CompetitorDto> competitorDtoList) {
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        if (competitorDtoList != null) {

            // Initialise and accumulate competitor entities from DTOs
            competitorDtoList.forEach(competitorDto -> {
                // Find the competitor entity if present
                Optional<Competitor> optionalCompetitorEntity = Optional.empty();
                if (competitorDto.getId() != null) {
                    optionalCompetitorEntity = competitorRepository.findById(competitorDto.getId());
                }

                // Initialise the competitor entity from DTO or create a new entity
                Competitor competitorEntity = optionalCompetitorEntity.orElse(new Competitor());
                // Add attributes to the competitor
                competitorEntity.init(competitorDto);

                // Update the map of competitors
                competitorMap.put(competitorDto.getUuid(), competitorEntity);
            });
        }

        return competitorMap;
    }

    // TODO: Javadoc
    protected Map<UUID, IpscMatchStage> initMatchStageEntities(List<MatchStageDto> matchStageDtoList,
                                                               @NotNull IpscMatch matchEntity) {
        Map<UUID, IpscMatchStage> matchStageMap = new HashMap<>();
        if (matchStageDtoList != null) {

            // Initialise and accumulate match stages from DTOs
            matchStageDtoList.forEach(stage -> {
                // Find the match stage entity if present
                Optional<IpscMatchStage> optionalIpscMatchStageEntity = Optional.empty();
                if (stage.getId() != null) {
                    optionalIpscMatchStageEntity = ipscMatchStageRepository.findById(stage.getId());
                }

                // Initialise the match stage entity from DTO or create a new entity
                IpscMatchStage matchStageEntity = optionalIpscMatchStageEntity.orElse(new IpscMatchStage());
                // Add attributes to the match stage
                matchStageEntity.init(stage, matchEntity);
                // Link the match stage to the match
                matchStageEntity.setMatch(matchEntity);

                // Update the map of match stages
                matchStageMap.put(stage.getUuid(), matchStageEntity);
            });
        }

        return matchStageMap;
    }

    // TODO: Javadoc
    protected Map<UUID, MatchCompetitor> initMatchCompetitorEntities(List<MatchCompetitorDto> matchCompetitors,
                                                                     IpscMatch matchEntity,
                                                                     Map<UUID, Competitor> competitorMap,
                                                                     ClubIdentifier clubIdentifier) {

        Map<UUID, MatchCompetitor> matchCompetitorMap = new HashMap<>();
        if (matchCompetitors != null) {

            // Initialise and accumulate match competitors from DTOs
            for (MatchCompetitorDto matchCompetitorDto : matchCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match competitor entity if present
                Optional<MatchCompetitor> optionalMatchCompetitorEntity = Optional.empty();
                if (matchCompetitorDto.getId() != null) {
                    optionalMatchCompetitorEntity =
                            matchCompetitorRepository.findById(matchCompetitorDto.getId());
                }

                // Initialise the match competitor entity from DTO or create a new entity
                MatchCompetitor matchCompetitorEntity = optionalMatchCompetitorEntity
                        .orElse(new MatchCompetitor());

                // Filter by club reference if specified
                if ((clubIdentifier != null) && (!clubIdentifier.equals(ClubIdentifier.UNKNOWN))) {
                    if (!clubIdentifier.equals(matchCompetitorDto.getClubName())) {
                        continue;
                    }
                }

                // Add attributes to the match competitor
//                matchCompetitorEntity.init(matchCompetitorDto, matchEntity, competitorEntity);
                // Link the match competitor to the match and competitor
//                matchCompetitorEntity.setMatch(matchEntity);
//                matchCompetitorEntity.setCompetitor(competitorEntity);

                // Update the map of match competitors
//                matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorEntity);
            }
        }

        return matchCompetitorMap;
    }

    // TODO: Javadoc
    protected Map<UUID, MatchStageCompetitor> initMatchStageCompetitorEntities(List<MatchStageCompetitorDto> matchStageCompetitors,
                                                                               Map<UUID, IpscMatchStage> matchStageMap, Map<UUID, Competitor> competitorMap,
                                                                               ClubIdentifier clubIdentifier) {

        Map<UUID, MatchStageCompetitor> matchStageCompetitorMap = new HashMap<>();
        if (matchStageCompetitors != null) {
            // Initialises and accumulates match stage competitors from DTOs
            for (MatchStageCompetitorDto matchStageCompetitorDto : matchStageCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchStageCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match stage entity
                IpscMatchStage matchStageEntity =
                        matchStageMap.get(matchStageCompetitorDto.getMatchStage().getUuid());

                // Find the match stage competitor entity if present
                Optional<MatchStageCompetitor> optionalMatchStageEntity = Optional.empty();
                if (matchStageCompetitorDto.getId() != null) {
                    optionalMatchStageEntity =
                            matchStageCompetitorRepository.findById(matchStageCompetitorDto.getId());
                }

                // Initialises the match stage competitor entity from DTO or create a new entity
                MatchStageCompetitor matchStageCompetitorEntity = optionalMatchStageEntity
                        .orElse(new MatchStageCompetitor());

                // Filter by club reference if specified
                if ((clubIdentifier != null) && (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier))) {
                    if (!clubIdentifier.equals(matchStageCompetitorDto.getClubName())) {
                        continue;
                    }
                }

                // Add attributes to the match stage competitor
//                matchStageCompetitorEntity.init(matchStageCompetitorDto, matchStageEntity, competitorEntity);
                // Link the match stage competitor to the match stage and competitor
//                matchStageCompetitorEntity.setMatchStage(matchStageEntity);
//                matchStageCompetitorEntity.setCompetitor(competitorEntity);

                // Update the map of match stage competitors
                matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorEntity);
            }
        }

        return matchStageCompetitorMap;
    }
}
