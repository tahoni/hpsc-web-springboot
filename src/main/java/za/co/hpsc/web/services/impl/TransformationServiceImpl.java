package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;
import za.co.hpsc.web.utils.DateUtil;
import za.co.hpsc.web.utils.NumberUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransformationServiceImpl implements TransformationService {
    protected final ClubEntityService clubEntityService;
    protected final MatchEntityService matchEntityService;
    protected final MatchStageEntityService matchStageEntityService;
    protected final CompetitorEntityService competitorEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    public TransformationServiceImpl(ClubEntityService clubEntityService,
                                     MatchEntityService matchEntityService,
                                     MatchStageEntityService matchStageEntityService,
                                     CompetitorEntityService competitorEntityService,
                                     MatchCompetitorEntityService matchCompetitorEntityService,
                                     MatchStageCompetitorEntityService matchStageCompetitorEntityService) {
        this.clubEntityService = clubEntityService;
        this.matchEntityService = matchEntityService;
        this.matchStageEntityService = matchStageEntityService;
        this.competitorEntityService = competitorEntityService;
        this.matchCompetitorEntityService = matchCompetitorEntityService;
        this.matchStageCompetitorEntityService = matchStageCompetitorEntityService;
    }

    @Override
    public IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder)
            throws ValidationException {

        // Validate input
        if (ipscRequestHolder == null) {
            log.error("IPSC request holder is null while mapping match results.");
            throw new ValidationException("IPSC request holder can not be null while mapping match results.");
        }

        if (ipscRequestHolder.getMatches() == null) {
            return new IpscResponseHolder(new ArrayList<>());
        }

        List<IpscResponse> ipscResponses = new ArrayList<>();
        // Maps IPSC requests to responses by match ID
        ipscRequestHolder.getMatches().stream().filter(Objects::nonNull)
                .forEach(match -> {
                    Optional<IpscResponse> response = createBasicMatch(ipscRequestHolder, match);
                    response.ifPresent(ipscResponses::add);
                });

        // Add members to each match
        ipscResponses.stream().filter(Objects::nonNull)
                .forEach(ipscResponse ->
                        addMembersToMatch(ipscResponse, ipscRequestHolder));
        // Add a club to each match
        ipscResponses.stream().filter(Objects::nonNull)
                .forEach(ipscResponse ->
                        addClubToMatch(ipscResponse, ipscRequestHolder));

        return new IpscResponseHolder(ipscResponses);
    }

    // TODO: comment
    @Override
    public IpscMatchRecordHolder generateIpscMatchRecordHolder(List<MatchHolder> ipscMatchHolderList) {
        if (ipscMatchHolderList == null) {
            return new IpscMatchRecordHolder(new ArrayList<>());
        }

        List<IpscMatchRecord> ipscMatchRecordList = new ArrayList<>();
        for (MatchHolder matchHolder : ipscMatchHolderList.stream().filter(Objects::nonNull).toList()) {
            // Get the match name
            matchHolder.getMatch().setName(ValueUtil.nullAsEmptyString(matchHolder.getMatch().getName()));

            // Get the match and match stage competitors
            Set<MatchStageCompetitor> matchStageCompetitorSet =
                    getMatchStageCompetitorSet(matchHolder.getMatchStageCompetitors());
            Set<MatchCompetitor> matchCompetitorSet =
                    getMatchCompetitorSet(matchHolder.getMatchCompetitors());

            // Get the competitors
            Set<Competitor> competitorSet = new HashSet<>(getCompetitorSet(matchHolder.getCompetitors()));

            // Initialise the competitor list
            Set<CompetitorRecord> competitorRecordSet = new HashSet<>();
            for (Competitor competitor : competitorSet.stream().filter(Objects::nonNull).toList()) {
                List<MatchCompetitor> matchCompetitorList = matchCompetitorSet.stream()
                        .filter(Objects::nonNull)
                        .filter(mc -> competitor.equals(mc.getCompetitor()))
                        .toList();
                List<MatchStageCompetitor> matchStageCompetitorList = matchStageCompetitorSet.stream()
                        .filter(Objects::nonNull)
                        .filter(msc -> competitor.equals(msc.getCompetitor()))
                        .toList();

                MatchCompetitorOverallResultsRecord competitorOverallResult =
                        initMatchCompetitorOverallResult(competitor, matchCompetitorList)
                                .orElse(null);
                List<MatchCompetitorStageResultRecord> competitorStageRecordList =
                        initMatchCompetitorStageResults(competitor, matchStageCompetitorList);

                if ((competitorOverallResult != null) && (competitorStageRecordList != null) &&
                        (!competitorStageRecordList.isEmpty())) {

                    Optional<CompetitorResultRecord> optionalCompetitorResult =
                            initCompetitorResult(competitor, matchCompetitorList.getFirst(),
                                    competitorOverallResult, competitorStageRecordList);
                    optionalCompetitorResult.flatMap(competitorResultRecord ->
                                    initCompetitorRecord(competitor, matchCompetitorSet.stream()
                                            .filter(Objects::nonNull)
                                            .filter(mc -> competitor.equals(mc.getCompetitor()))
                                            .findFirst().orElse(null), competitorResultRecord))
                            .ifPresent(competitorRecordSet::add);
                }
            }

            Optional<IpscMatchRecord> ipscResponse = initIpscMatchRecord(matchHolder.getMatch(),
                    matchHolder.getClub(), new ArrayList<>(competitorRecordSet));
            ipscResponse.ifPresent(ipscMatchRecordList::add);
        }

        return new IpscMatchRecordHolder(ipscMatchRecordList);
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

        // Initialises competitors
        matchResultsDto.setCompetitors(initCompetitors(matchResultsDto, ipscResponse));

        // Initialises match results
        initScores(matchResultsDto, ipscResponse);

        return Optional.of(matchResultsDto);
    }

    /**
     * Creates a basic match representation by filtering relevant data from the provided
     * request holder based on the given match details.
     *
     * @param ipscRequestHolder the container for all IPSC-related input requests, including
     *                          stages, tags, scores, enrolled members, and more.
     * @param match             the match data used to identify and extract the corresponding subsets
     *                          of stages, tags, scores, and enrolled members.
     * @return an {@link IpscResponse} object containing the filtered data associated with the
     * provided match.
     */
    protected Optional<IpscResponse> createBasicMatch(IpscRequestHolder ipscRequestHolder, MatchRequest match) {
        if (match == null) {
            return Optional.empty();
        }

        // Create the match response
        MatchResponse matchResponse = new MatchResponse(match);
        Integer matchId = matchResponse.getMatchId();
        if (matchId == null) {
            return Optional.empty();
        }

        // Add all tags to the response
        List<TagRequest> tagRequests = ipscRequestHolder.getTags();

        if ((ipscRequestHolder.getStages() == null) || (ipscRequestHolder.getEnrolledMembers() == null) ||
                (ipscRequestHolder.getScores() == null)) {
            return Optional.empty();
        }

        // Add stages to the response, filtered by match ID
        List<StageRequest> stageRequests = ipscRequestHolder.getStages().stream()
                .filter(Objects::nonNull)
                .filter(stage -> matchId.equals(stage.getMatchId()))
                .toList();
        // Add enrolled members to the response, filtered by match ID
        List<EnrolledRequest> enrolledRequests = ipscRequestHolder.getEnrolledMembers().stream()
                .filter(Objects::nonNull)
                .filter(enrolled -> matchId.equals(enrolled.getMatchId()))
                .toList();
        // Add scores to the response, filtered by match ID
        List<ScoreRequest> scoreRequests = ipscRequestHolder.getScores().stream()
                .filter(Objects::nonNull)
                .filter(score -> matchId.equals(score.getMatchId()))
                .toList();

        IpscResponse response = new IpscResponse(tagRequests, matchResponse, stageRequests,
                enrolledRequests, scoreRequests);
        return Optional.of(response);
    }

    /**
     * Filters and assigns members with corresponding scores to the given IPSC response.
     *
     * <p>
     * The method iterates through the list of scores in the request holder, filters members
     * matching the score's member ID, and sets these members in the provided IPSC response.
     * </p>
     *
     * @param ipscResponse      the IPSC response object to which the filtered members
     *                          will be assigned.
     * @param ipscRequestHolder the request holder containing scores and members to be
     *                          filtered and matched.
     */
    protected void addMembersToMatch(IpscResponse ipscResponse, IpscRequestHolder ipscRequestHolder) {
        if (ipscResponse == null) {
            return;
        }

        List<MemberRequest> responseMembers = new ArrayList<>();
        // Filters members by members with scores
        ipscRequestHolder.getScores().stream().filter(Objects::nonNull)
                .forEach(scoreRequest -> {
                    List<MemberRequest> memberRequests = ipscRequestHolder.getMembers().stream()
                            .filter(Objects::nonNull)
                            .filter(memberRequest ->
                                    memberRequest.getMemberId().equals(scoreRequest.getMemberId()))
                            .toList();
                    // Collects members with scores
                    responseMembers.addAll(memberRequests);
                });
        // Sets members on the response
        ipscResponse.setMembers(responseMembers.stream().filter(Objects::nonNull).map(MemberResponse::new).toList());
    }

    /**
     * Associates a club with the given match by matching the club ID from the provided
     * IPSC response with the list of clubs in the request holder.
     *
     * <p>
     * If a matching club is found, it is set on the response. Otherwise, a default
     * club response is created using the club ID from the match.
     * </p>
     *
     * @param ipscResponse      the IPSC response containing the match details, including the club ID
     *                          to match against the provided club data.
     * @param ipscRequestHolder the request holder that contains the list of clubs to be searched
     *                          for a match.
     */
    protected void addClubToMatch(IpscResponse ipscResponse, IpscRequestHolder ipscRequestHolder) {
        if ((ipscResponse == null) || (ipscRequestHolder == null)) {
            return;
        }

        if ((ipscResponse.getMatch() != null) && (ipscResponse.getMatch().getClubId() != null) &&
                (ipscRequestHolder.getClubs() != null)) {
            Integer clubId = ipscResponse.getMatch().getClubId();
            // Finds the club matching ID or provides default
            ClubRequest club = ipscRequestHolder.getClubs().stream()
                    .filter(Objects::nonNull)
                    .filter(clubRequest -> clubId.equals(clubRequest.getClubId()))
                    .findFirst().orElse(null);
            ipscResponse.setClub((club != null) ? new ClubResponse(club) : new ClubResponse(clubId));
        }
    }

    /**
     * Initialises a {@link IpscMatchRecord} based on the provided match details and competitors list.
     *
     * @param match       The IPSC match object containing match details.
     *                    If null, an empty Optional is returned.
     * @param club        The club object associated with the match.
     * @param competitors A list of competitor match records associated with the match.
     *                    If null, an empty Optional is returned.
     * @return An Optional containing the initialized {@link IpscMatchRecord} if both inputs
     * are non-null, otherwise an empty Optional.
     */
    protected Optional<IpscMatchRecord> initIpscMatchRecord(IpscMatch match, Club club,
                                                            List<CompetitorRecord> competitors) {
        if ((match == null) || (competitors == null)) {
            return Optional.empty();
        }

        // Get the date the match was last edited
        String dateEdited = DateUtil.formatDateTime(match.getDateEdited(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        // Initialises match details
        String clubName = ((club != null) ? club.toString() : "");

        String scheduledDate = DateUtil.formatDateTime(match.getScheduledDate(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        String matchFirearmType = ValueUtil.nullAsEmptyString(match.getMatchFirearmType());
        String matchCategory = ValueUtil.nullAsEmptyString(match.getMatchCategory());

        // Creates match record from match details
        IpscMatchRecord ipscMatchRecord = new IpscMatchRecord(match.getName(), scheduledDate,
                clubName, matchFirearmType, matchCategory, competitors, dateEdited);
        return Optional.of(ipscMatchRecord);
    }

    // TODO: Javadoc
    protected Optional<CompetitorRecord> initCompetitorRecord(Competitor competitor,
                                                              MatchCompetitor matchCompetitor,
                                                              CompetitorResultRecord competitorResultRecord) {
        if ((competitor == null) || (matchCompetitor == null) || (competitorResultRecord == null)) {
            return Optional.empty();
        }

        // Get the club details for the competitor in the match
        String clubName = "";
        if ((matchCompetitor.getMatchClub() != null) && (matchCompetitor.getMatchClub().getName() != null)) {
            clubName = ValueUtil.nullAsEmptyString(matchCompetitor.getMatchClub().getName());
        }
        if (clubName.isEmpty()) {
            if (matchCompetitor.getMatch() != null && matchCompetitor.getMatch().getClub() != null) {
                clubName = ValueUtil.nullAsEmptyString(matchCompetitor.getMatch().getClub().getName());
            }
        }

        // Get the competitor details
        String competitorCategory = ValueUtil.nullAsEmptyString(matchCompetitor.getCompetitorCategory().toString());

        String dateOfBirth = DateUtil.formatDate(competitor.getDateOfBirth(),
                IpscConstants.IPSC_OUTPUT_DATE_FORMAT);

        // Creates competitor record from competitor details
        CompetitorRecord competitorRecord = new CompetitorRecord(competitor.getFirstName(),
                competitor.getLastName(), competitor.getMiddleNames(), dateOfBirth,
                competitor.getSapsaNumber(), competitor.getCompetitorNumber(),
                clubName, competitorCategory, competitorResultRecord);
        return Optional.of(competitorRecord);
    }

    // TODO: Javadoc
    protected Optional<CompetitorResultRecord> initCompetitorResult(
            Competitor competitor, MatchCompetitor matchCompetitor, MatchCompetitorOverallResultsRecord matchCompetitorOverallResult,
            List<MatchCompetitorStageResultRecord> matchCompetitorStageResults) {
        if ((competitor == null) || (matchCompetitorOverallResult == null) ||
                (matchCompetitorStageResults == null)) {
            return Optional.empty();
        }

        // Initialises match competitor details
        String firearmType = ValueUtil.nullAsEmptyString(matchCompetitor.getFirearmType());
        String division = ValueUtil.nullAsEmptyString(matchCompetitor.getDivision());
        String powerFactor = ValueUtil.nullAsEmptyString(matchCompetitor.getPowerFactor());

        // Creates competitor record from match competitor details
        return Optional.of(new CompetitorResultRecord(firearmType, division, powerFactor,
                matchCompetitorOverallResult, matchCompetitorStageResults));
    }

    // TODO: Javadoc
    protected Optional<MatchCompetitorOverallResultsRecord> initMatchCompetitorOverallResult(
            Competitor competitor, List<MatchCompetitor> matchCompetitorList) {

        if ((competitor == null) || (matchCompetitorList == null)) {
            return Optional.empty();
        }


        // Filters and maps match data for the competitor in this division
        Optional<MatchCompetitor> optionalMatchCompetitor = matchCompetitorList.stream()
                .filter(Objects::nonNull)
                .filter(mc -> competitor.equals(mc.getCompetitor()))
                .findFirst();

        if (optionalMatchCompetitor.isEmpty()) {
            return Optional.empty();
        }

        MatchCompetitor matchCompetitor = optionalMatchCompetitor.get();
        // Initialises the overall results for the competitor in this match
        String matchPoints = NumberUtil.formatBigDecimal(matchCompetitor.getMatchPoints(),
                IpscConstants.MATCH_POINTS_SCALE);
        String matchRanking = NumberUtil.formatBigDecimal(matchCompetitor.getMatchRanking(),
                IpscConstants.PERCENTAGE_SCALE);

        // Initialise the date the competitor was last edited
        String dateEdited = DateUtil.formatDateTime(matchCompetitor.getDateEdited(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        // Creates and returns a new overall results record for the competitor in this match
        return Optional.of(new MatchCompetitorOverallResultsRecord(
                matchPoints, matchRanking, dateEdited));
    }

    // TODO: Javadoc
    protected List<MatchCompetitorStageResultRecord> initMatchCompetitorStageResults(
            Competitor competitor, List<MatchStageCompetitor> matchStageCompetitorList) {

        if ((competitor == null) || (matchStageCompetitorList == null)) {
            return new ArrayList<>();
        }

        List<MatchCompetitorStageResultRecord> thisCompetitorStages = new ArrayList<>();
        // Filters and maps stage data for the competitor in this division
        matchStageCompetitorList.stream()
                .filter(Objects::nonNull)
                .filter(msc -> competitor.equals(msc.getCompetitor()))
                .forEach(msc -> {

                    // Gets the stage details
                    String stageName = msc.getMatchStage().getStageName();

                    // Initialises the overall results for the competitor in this stage
                    String time = NumberUtil.formatBigDecimal(msc.getTime(),
                            IpscConstants.TIME_SCALE);
                    String hitFactor = NumberUtil.formatBigDecimal(msc.getHitFactor(),
                            IpscConstants.HIT_FACTOR_SCALE);

                    // Initialises the stage points and percentage for the competitor in this stage
                    String stagePoints = NumberUtil.formatBigDecimal(msc.getStagePoints(),
                            IpscConstants.STAGE_POINTS_SCALE);
                    String stagePercentage = NumberUtil.formatBigDecimal(msc.getStagePercentage(),
                            IpscConstants.PERCENTAGE_SCALE);
                    String stageRanking = NumberUtil.formatBigDecimal(msc.getStageRanking(),
                            IpscConstants.PERCENTAGE_SCALE);

                    // Initialise the date the competitor was last edited
                    String dateEdited = DateUtil.formatDateTime(msc.getDateEdited(),
                            IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

                    // Creates a new stage result record for the competitor in this stage
                    MatchCompetitorStageResultRecord thisCompetitorStage = new MatchCompetitorStageResultRecord(
                            stageName, msc.getScoreA(), msc.getScoreB(), msc.getScoreC(), msc.getScoreD(),
                            msc.getPoints(), msc.getMisses(), msc.getPenalties(), msc.getProcedurals(),
                            time, hitFactor, stagePoints, stagePercentage, stageRanking,
                            dateEdited);
                    // Add the stage result to the list of stage results for the competitor in this division
                    thisCompetitorStages.add(thisCompetitorStage);
                });

        // Returns the list of stage results for the competitor in this division
        return thisCompetitorStages;
    }

    /**
     * Retrieves a set of unique competitors from the provided list of competitors.
     * If the input list is null, an empty set is returned.
     *
     * @param competitorList the list of competitors from which to extract a unique set;
     *                       may contain null values
     * @return a set of unique, non-null competitors derived from the input list;
     * returns an empty set if the input list is null
     */
    protected Set<Competitor> getCompetitorSet(List<Competitor> competitorList) {
        if (competitorList == null) {
            return new HashSet<>();
        }

        // Gets competitors from the match competitor list
        return competitorList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of unique, non-null MatchCompetitor objects from the input list.
     *
     * @param matchCompetitors the list of MatchCompetitor objects, which may include null values
     * @return a Set containing unique, non-null MatchCompetitor objects;
     * if the input list is null, an empty Set is returned
     */
    protected Set<MatchCompetitor> getMatchCompetitorSet(List<MatchCompetitor> matchCompetitors) {
        if (matchCompetitors == null) {
            return new HashSet<>();
        }

        // Gets competitors from the match
        return matchCompetitors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of unique {@code MatchStageCompetitor} objects from the provided list.
     *
     * @param matchStageCompetitors the list of {@code MatchStageCompetitor} objects to be processed;
     *                              may be {@code null}.
     * @return a {@code Set} containing unique {@code MatchStageCompetitor} objects from the input list.
     * If the input list is {@code null}, returns an empty {@code Set}.
     */
    protected Set<MatchStageCompetitor> getMatchStageCompetitorSet(List<MatchStageCompetitor> matchStageCompetitors) {
        if (matchStageCompetitors == null) {
            return new HashSet<>();
        }

        // Gets competitors from the match stage list
        return matchStageCompetitors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
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
     * @param clubResponse the response containing club information, which may be used to
     *                     initialise the DTO.
     *                     If null, the method will return an empty optional.
     * @return an {@code Optional} containing the initialized {@link ClubDto},
     * or {@code Optional.empty} if the provided clubResponse is null.
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
     * @param ipscResponse The response object containing match details and scores from
     *                     the IPSC system.
     * @param clubDto      The club data transfer object containing information about the club
     *                     associated with the match.
     * @return An {@code Optional} containing the initialised {@link MatchDto} if the match is valid
     * and meets the update criteria, or {@code Optional.empty()} if no match is initialised.
     */
    protected Optional<MatchDto> initMatch(IpscResponse ipscResponse, ClubDto clubDto) {
        if ((ipscResponse == null) || (ipscResponse.getMatch() == null)) {
            return Optional.empty();
        }

        // Attempts to find the match by name in the database
        Optional<IpscMatch> optionalMatch =
                matchEntityService.findMatchByNameAndScheduledDate(ipscResponse.getMatch().getMatchName(),
                        ipscResponse.getMatch().getMatchDate());

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
                    Optional<IpscMatchStage> optionalMatchStage =
                            matchStageEntityService.findMatchStage(matchDto.getId(),
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
     * Initialises a list of competitors based on the provided match results and IPSC response data.
     * Filters and maps scores and member responses to construct a list of valid competitor DTOs.
     * Ignores null data and members without valid scores.
     *
     * @param matchResultsDto the match results data transfer object containing details of the match.
     * @param ipscResponse    the response object containing scores and member information from the IPSC system.
     * @return a list of {@code CompetitorDto} objects representing the competitors with valid scores.
     */
    protected List<CompetitorDto> initCompetitors(@NotNull MatchResultsDto matchResultsDto,
                                                  IpscResponse ipscResponse) {
        if ((ipscResponse == null) || (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return new ArrayList<>();
        }

        if ((matchResultsDto == null) || (matchResultsDto.getMatch() == null)) {
            return new ArrayList<>();
        }

        // Get the scores for the current match
        List<ScoreResponse> scoreResponses = ipscResponse.getScores().stream()
                .filter(Objects::nonNull)
                .filter(scoreResponse -> scoreResponse.getMatchId() != null)
                .filter(scoreResponse -> scoreResponse.getMatchId().equals(ipscResponse.getMatch().getMatchId()))
                .toList();
        List<MemberResponse> memberResponses = ipscResponse.getMembers();

        // Maps score responses to corresponding competitor responses,
        // excluding competitors who didn't participate
        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        scoreResponses.forEach(scoreResponse -> {
            if ((scoreResponse.getFinalScore() != null) && (scoreResponse.getFinalScore() != 0)) {
                Optional<MemberResponse> optionalMemberResponse = memberResponses.stream()
                        .filter(Objects::nonNull)
                        .filter(memberResponse -> memberResponse.getMemberId() != null)
                        .filter(memberResponse -> memberResponse.getMemberId().equals(scoreResponse.getMemberId()))
                        .findFirst();
                optionalMemberResponse.ifPresent(memberResponse -> {
                    // Attempts to find the competitor by its ICS alias or first name and last name
                    Optional<Competitor> optionalCompetitor = competitorEntityService.findCompetitor(
                            memberResponse.getIcsAlias(),
                            memberResponse.getFirstName(),
                            memberResponse.getLastName(),
                            memberResponse.getDateOfBirth());
                    // Creates a new competitor DTO, from either the found entity or the competitor response
                    CompetitorDto competitorDto = optionalCompetitor
                            .map(CompetitorDto::new)
                            .orElseGet(CompetitorDto::new);

                    // Initialises competitor attributes
                    competitorDto.init(memberResponse);
                    competitorDtoMap.put(memberResponse.getMemberId(), competitorDto);
                });
            }
        });

        // Filter out duplicates based on sapsaNumber and id
        Map<Integer, CompetitorDto> filteredCompetitorDtoMap = deDuplicateCompetitorDtoList(competitorDtoMap);

        return filteredCompetitorDtoMap.values().stream().filter(Objects::nonNull).toList();
    }

    // TODO: Javadoc
    // TODO: tests
    protected void initScores(@NotNull MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
        // Checks for null or missing data in the IPSC response
        if ((ipscResponse == null) || (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return;
        }
        // Checks for null match results DTO
        if (matchResultsDto.getMatch() == null) {
            return;
        }

        // Filters score responses to those relevant to the current match
        List<ScoreResponse> scoreResponses = ipscResponse.getScores().stream()
                .filter(Objects::nonNull)
                .filter(scoreResponse -> scoreResponse.getMatchId() != null)
                .filter(scoreResponse -> scoreResponse.getMatchId()
                        .equals(matchResultsDto.getMatch().getIndex()))
                .toList();
        List<MemberResponse> memberResponses = ipscResponse.getMembers();
        List<EnrolledResponse> enrolledResponses = ((ipscResponse.getEnrolledMembers() != null) ?
                ipscResponse.getEnrolledMembers() : new ArrayList<>());

        // Ensure the match and stage competitor list fields are not null
        List<MatchCompetitorDto> matchCompetitorDtoList = ((matchResultsDto.getMatchCompetitors() != null) ?
                new ArrayList<>(matchResultsDto.getMatchCompetitors()) : new ArrayList<>());
        List<MatchStageCompetitorDto> matchStageCompetitorDtoList = ((matchResultsDto.getMatchStageCompetitors() != null) ?
                new ArrayList<>(matchResultsDto.getMatchStageCompetitors()) : new ArrayList<>());

        // Seed competitors when not already initialised
        if ((matchResultsDto.getCompetitors() == null) || (matchResultsDto.getCompetitors().isEmpty())) {
            matchResultsDto.setCompetitors(initCompetitors(matchResultsDto, ipscResponse));
        }

        // Maps score responses to corresponding member responses,
        // excluding members who didn't participate
        List<MemberResponse> scoreMembers = memberResponses.stream()
                .filter(Objects::nonNull)
                .toList();

        Map<Integer, CompetitorDto> competitorDtoMap = new HashMap<>();
        Map<Integer, EnrolledResponse> enrolledResponseMap = new HashMap<>();
        scoreMembers.stream().filter(Objects::nonNull)
                .forEach(memberResponse -> {
                    // Initialises the enrolled response to use to initialise the scores for each
                    // competitor per match and stage
                    List<EnrolledResponse> enrolledResponseList = enrolledResponses.stream()
                            .filter(Objects::nonNull)
                            .filter(er -> er.getMemberId() != null)
                            .filter(er -> er.getMemberId()
                                    .equals(memberResponse.getMemberId()))
                            .toList();

                    // Get the member response from the map
                    Optional<MemberResponse> optionalMemberResponse = memberResponses.stream()
                            .filter(Objects::nonNull)
                            .filter(mr -> mr.getMemberId() != null)
                            .filter(mr -> mr.getMemberId()
                                    .equals(memberResponse.getMemberId()))
                            .findFirst();
                    // Get the competitor from the map
                    Optional<CompetitorDto> optionalCompetitorDto = matchResultsDto.getCompetitors().stream()
                            .filter(Objects::nonNull)
                            .filter(cd -> cd.getIndexes() != null)
                            .filter(cd -> !cd.getIndexes().isEmpty())
                            .filter(cd -> cd.getIndexes()
                                    .contains(memberResponse.getMemberId()))
                            .findFirst();

                    // Caches the competitor and enrolled response for later use
                    if (optionalCompetitorDto.isPresent() && optionalMemberResponse.isPresent()) {
                        competitorDtoMap.put(optionalMemberResponse.get().getMemberId(), optionalCompetitorDto.get());
                        enrolledResponseMap.put(optionalMemberResponse.get().getMemberId(), enrolledResponseList.getFirst());
                    }
                });

        // Iterates through each competitor
        competitorDtoMap.keySet().stream().filter(Objects::nonNull)
                .forEach(memberIndex -> {
                    // Gets the competitor DTO from the map
                    CompetitorDto competitorDto = competitorDtoMap.get(memberIndex);

                    // Attempts to find the match competitor by competitor ID and match ID
                    // in the database
                    Optional<MatchCompetitor> optionalMatchCompetitor = Optional.empty();
                    if (matchResultsDto.getMatch().getId() != null) {
                        optionalMatchCompetitor =
                                matchCompetitorEntityService.findMatchCompetitor(matchResultsDto.getMatch().getId(),
                                        competitorDto.getId());
                    }

                    // Creates a new match competitor DTO, from either the found entity or the
                    // competitor DTO
                    MatchCompetitorDto matchCompetitorDto = optionalMatchCompetitor
                            .map(MatchCompetitorDto::new)
                            .orElse(new MatchCompetitorDto(competitorDto, matchResultsDto.getMatch(), memberIndex));
                    matchCompetitorDto.setCompetitorIndex(memberIndex);
                    matchCompetitorDto.setMatchIndex(matchResultsDto.getMatch().getIndex());

                    // Filters scores by member ID
                    List<ScoreResponse> scores = scoreResponses.stream()
                            .filter(Objects::nonNull)
                            .filter(sr -> Objects.equals(sr.getMemberId(), memberIndex))
                            .filter(sr -> Objects.equals(sr.getMatchId(),
                                    matchResultsDto.getMatch().getIndex()))
                            .toList();
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
                                        .filter(scoreResponse -> scoreResponse.getStageId() != null)
                                        .filter(scoreResponse -> scoreResponse.getStageId().equals(stageDto.getStageNumber()))
                                        .findFirst();
                                optionalStageScoreResponse.ifPresent(stageScoreResponse -> {
                                    // Attempts to find the match stage competitor by competitor ID,
                                    // stage ID, and match ID
                                    Optional<MatchStageCompetitor> optionalMatchStageCompetitor =
                                            matchStageCompetitorEntityService
                                                    .findMatchStageCompetitor(stageDto.getId(),
                                                            competitorDto.getId());
                                    // Creates a new match stage competitor DTO, from either
                                    // the found entity or the competitor DTO
                                    MatchStageCompetitorDto matchStageCompetitorDto =
                                            optionalMatchStageCompetitor
                                                    .map(MatchStageCompetitorDto::new)
                                                    .orElse(new MatchStageCompetitorDto(competitorDto, stageDto));
                                    matchStageCompetitorDto.setCompetitorIndex(stageScoreResponse.getMemberId());
                                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                                    // Initialises the match stage attributes
                                    matchStageCompetitorDto.init(stageScoreResponse,
                                            enrolledResponseMap.get(memberIndex), stageDto);
                                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);
                                });
                                if (optionalStageScoreResponse.isPresent()) {
                                    // Attempts to find the match stage competitor by competitor ID,
                                    // stage ID, and match ID
                                    Optional<MatchStageCompetitor> optionalMatchStageCompetitor =
                                            matchStageCompetitorEntityService
                                                    .findMatchStageCompetitor(stageDto.getId(),
                                                            competitorDto.getId());
                                    // Creates a new match stage competitor DTO, from either
                                    // the found entity or the competitor DTO
                                    MatchStageCompetitorDto matchStageCompetitorDto =
                                            optionalMatchStageCompetitor
                                                    .map(MatchStageCompetitorDto::new)
                                                    .orElse(new MatchStageCompetitorDto(competitorDto, stageDto));
                                    matchStageCompetitorDto.setCompetitorIndex(optionalStageScoreResponse.get().getMemberId());
                                    matchStageCompetitorDto.setMatchStageIndex(stageDto.getIndex());

                                    // Initialises the match stage attributes
                                    matchStageCompetitorDto.init(optionalStageScoreResponse.get(),
                                            enrolledResponseMap.get(memberIndex), stageDto);
                                    matchStageCompetitorDtoList.add(matchStageCompetitorDto);
                                }
                            });
                });

        // Collects all match competitors in the match results DTO
        matchResultsDto.setMatchCompetitors(matchCompetitorDtoList);
        // Collects all stage competitors in the match results DTO
        matchResultsDto.setMatchStageCompetitors(matchStageCompetitorDtoList);
    }

    // TODO: Javadoc
    // TODO: tests
    protected Map<Integer, CompetitorDto> deDuplicateCompetitorDtoList(Map<Integer, CompetitorDto> competitorDtoMap) {
        if (competitorDtoMap == null) {
            return null;
        }

        // Prepare sets to track seen SAPSA numbers and IDs
        Set<Integer> seenSapsaNumbers = new HashSet<>();
        Set<Long> seenIds = new HashSet<>();

        // Sets maps to hold the original and filtered competitor DTOs
        Map<Integer, CompetitorDto> unfilteredCompetitorMap = competitorDtoMap;
        Map<Integer, CompetitorDto> filteredCompetitorDtoMap = new LinkedHashMap<>();

        // Filter out duplicates based on the member's SAPSA number, if available
        Map<Integer, CompetitorDto> intermediateFilteredCompetitorDtoMap = filteredCompetitorDtoMap;
        unfilteredCompetitorMap.forEach((key, competitorDto) -> {
            boolean isDuplicate = false;

            if (competitorDto.getSapsaNumber() != null) {
                if (seenSapsaNumbers.contains(competitorDto.getSapsaNumber())) {
                    isDuplicate = true;
                } else {
                    seenSapsaNumbers.add(competitorDto.getSapsaNumber());
                }
            }

            // Is this SAPSA number duplicated?
            if (!isDuplicate) {
                intermediateFilteredCompetitorDtoMap.put(key, competitorDto);
            } else {
                Optional<CompetitorDto> duplicateCompetitorDto = competitorDtoMap.values().stream()
                        .filter(Objects::nonNull)
                        .filter(cd -> cd.getSapsaNumber() != null)
                        .filter(cd -> cd.getSapsaNumber().equals(competitorDto.getSapsaNumber()))
                        .findFirst();
                duplicateCompetitorDto.ifPresent(originalCompetitorDto ->
                        originalCompetitorDto.getIndexes().addAll(competitorDto.getIndexes()));
            }
        });

        // Resets maps to hold the original and filtered competitor DTOs
        unfilteredCompetitorMap = filteredCompetitorDtoMap;
        filteredCompetitorDtoMap = new LinkedHashMap<>();

        // Filter out duplicates based on the competitor's ID, if available
        Map<Integer, CompetitorDto> finalFilteredCompetitorDtoMap = filteredCompetitorDtoMap;
        unfilteredCompetitorMap.forEach((key, competitorDto) -> {
            boolean isDuplicate = false;

            if (competitorDto.getId() != null) {
                if (seenIds.contains(competitorDto.getId())) {
                    isDuplicate = true;
                } else {
                    seenIds.add(competitorDto.getId());
                }
            }

            // Is this ID duplicated?
            if (!isDuplicate) {
                finalFilteredCompetitorDtoMap.put(key, competitorDto);
            } else {
                Optional<CompetitorDto> duplicateCompetitorDto = competitorDtoMap.values().stream()
                        .filter(Objects::nonNull)
                        .filter(cd -> cd.getId() != null)
                        .filter(cd -> cd.getId().equals(competitorDto.getId()))
                        .findFirst();
                duplicateCompetitorDto.ifPresent(originalCompetitorDto ->
                        originalCompetitorDto.getIndexes().addAll(competitorDto.getIndexes()));
            }
        });

        return finalFilteredCompetitorDtoMap;
    }
}
