package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.common.dto.*;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.common.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.common.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.common.holders.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.common.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.common.records.*;
import za.co.hpsc.web.models.ipsc.common.request.*;
import za.co.hpsc.web.models.ipsc.common.response.*;
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
    public IpscResponseHolder mapMatchResults(IpscRequestHolder ipscRequestHolder) {

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

    @Override
    public Optional<MatchResultsDto> mapMatchOnly(MatchResponse matchResponse) {
        if (matchResponse == null) {
            return Optional.empty();
        }

        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setMatch(matchResponse);
        ipscResponse.setClub(new ClubResponse(matchResponse.getClubId()));

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        MatchDto matchDto = new MatchDto();
        matchDto.init(matchResponse, null, null);
        matchResultsDto.setMatch(matchDto);

        return Optional.of(matchResultsDto);
    }

    @Override
    public IpscMatchRecordHolder generateIpscMatchRecordHolder(List<MatchHolder> ipscMatchHolderList) {
        if (ipscMatchHolderList == null) {
            return new IpscMatchRecordHolder(new ArrayList<>());
        }

        List<MatchRecord> matchRecordList = new ArrayList<>();
        for (MatchHolder matchHolder : ipscMatchHolderList.stream().filter(Objects::nonNull).toList()) {
            // Get the match name
            matchHolder.getMatch().setName(ValueUtil.nullAsEmptyString(matchHolder.getMatch().getName()));

            // Get the match and match stage competitors
            Set<MatchStageCompetitor> matchStageCompetitorSet =
                    getMatchStageCompetitorSet(matchHolder.getMatchStageCompetitors());
            Set<MatchCompetitor> matchCompetitorSet =
                    getMatchCompetitorSet(matchHolder.getMatchCompetitors());

            // Initialise one CompetitorRecord per enrollment (MatchCompetitor)
            Set<CompetitorRecord> competitorRecordSet = new HashSet<>();
            for (MatchCompetitor matchCompetitor : matchCompetitorSet.stream().filter(Objects::nonNull).toList()) {
                Competitor competitor = matchCompetitor.getCompetitor();
                if (competitor == null) continue;

                // Stage competitors for this enrollment, matched by division
                List<MatchStageCompetitor> matchStageCompetitorList = matchStageCompetitorSet.stream()
                        .filter(Objects::nonNull)
                        .filter(msc -> competitor.equals(msc.getCompetitor()))
                        .filter(msc -> Objects.equals(matchCompetitor.getDivision(), msc.getDivision()))
                        .toList();

                MatchCompetitorOverallResultsRecord competitorOverallResult =
                        initMatchCompetitorOverallResult(competitor, List.of(matchCompetitor))
                                .orElse(null);
                List<MatchCompetitorStageResultRecord> competitorStageRecordList =
                        initMatchCompetitorStageResults(competitor, matchStageCompetitorList);

                if ((competitorOverallResult != null) && (competitorStageRecordList != null) &&
                        (!competitorStageRecordList.isEmpty())) {

                    Optional<CompetitorResultRecord> optionalCompetitorResult =
                            initCompetitorResult(competitor, matchCompetitor,
                                    competitorOverallResult, competitorStageRecordList);
                    optionalCompetitorResult.flatMap(competitorResultRecord ->
                                    initCompetitorRecord(competitor, matchCompetitor, competitorResultRecord))
                            .ifPresent(competitorRecordSet::add);
                }
            }

            Optional<MatchRecord> ipscResponse = initIpscMatchRecord(matchHolder.getMatch(),
                    matchHolder.getClub(), new ArrayList<>(competitorRecordSet));
            ipscResponse.ifPresent(matchRecordList::add);
        }

        return new IpscMatchRecordHolder(matchRecordList);
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

        // Check if there are competitors
        if (ipscResponse.getMembers() == null) {
            return Optional.of(matchResultsDto);
        }

        // Initialises competitors
        matchResultsDto.setCompetitors(initCompetitors(matchResultsDto, ipscResponse));

        // Initialises match results
        initEnrolledCompetitors(matchResultsDto, ipscResponse);

        return Optional.of(matchResultsDto);
    }

    /**
     * Creates a minimal match-scoped {@link IpscResponse} from request collections.
     *
     * @param ipscRequestHolder full request holder
     * @param match             match request used as the grouping anchor
     * @return optional response containing tags plus stage/enrollment/score lists filtered by match ID
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
     * Populates {@link IpscResponse#setMembers(List)} with members referenced by scores.
     *
     * @param ipscResponse      response being enriched
     * @param ipscRequestHolder source holder with members and scores
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
        ipscResponse.setMembers(responseMembers.stream().filter(Objects::nonNull)
                .map(MemberResponse::new).toList());
    }

    /**
     * Resolves and assigns a club for the response match.
     * <p>
     * If a matching club request exists by club ID, it is mapped; otherwise a fallback
     * {@link ClubResponse} is created from the match club ID only.
     * </p>
     *
     * @param ipscResponse      response containing match data
     * @param ipscRequestHolder source holder containing available clubs
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
     * Creates a {@link MatchRecord} from domain match, club, and competitor export records.
     *
     * @param match       match entity (required)
     * @param club        optional club entity for display naming
     * @param competitors competitor export records (required, may be empty)
     * @return optional containing created record, or empty if required inputs are missing
     */
    protected Optional<MatchRecord> initIpscMatchRecord(IpscMatch match, Club club,
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
        MatchRecord matchRecord = new MatchRecord(match.getName(), scheduledDate,
                clubName, matchFirearmType, matchCategory, competitors, dateEdited);
        return Optional.of(matchRecord);
    }

    /**
     * Creates a {@link CompetitorRecord} combining personal details and result record data.
     *
     * @param competitor             competitor entity (required)
     * @param matchCompetitor        enrollment/match-competitor entity (required)
     * @param competitorResultRecord precomputed result record (required)
     * @return optional containing competitor record, or empty when any required argument is null
     */
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
        String competitorCategory =
                ValueUtil.nullAsEmptyString(matchCompetitor.getCompetitorCategory().toString());

        String dateOfBirth = DateUtil.formatDate(competitor.getDateOfBirth(),
                IpscConstants.IPSC_OUTPUT_DATE_FORMAT);

        // Creates competitor record from competitor details
        CompetitorRecord competitorRecord = new CompetitorRecord(competitor.getFirstName(),
                competitor.getLastName(), competitor.getMiddleNames(), dateOfBirth,
                competitor.getSapsaNumber(), competitor.getCompetitorNumber(),
                clubName, competitorCategory, competitorResultRecord);
        return Optional.of(competitorRecord);
    }

    /**
     * Creates a {@link CompetitorResultRecord} containing firearm/division/power-factor metadata
     * and precomputed overall + stage results.
     *
     * @param competitor                   competitor entity (required for null-guard consistency)
     * @param matchCompetitor              match-competitor source for enum metadata
     * @param matchCompetitorOverallResult precomputed overall result (required)
     * @param matchCompetitorStageResults  precomputed stage results (required)
     * @return optional containing result record when required args are present
     */
    protected Optional<CompetitorResultRecord> initCompetitorResult(
            Competitor competitor, MatchCompetitor matchCompetitor,
            MatchCompetitorOverallResultsRecord matchCompetitorOverallResult,
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

    /**
     * Computes overall match results for one competitor from match-competitor entities.
     *
     * @param competitor          competitor to resolve
     * @param matchCompetitorList candidate match-competitor rows
     * @return optional overall-result record, or empty when input is null or no row matches
     */
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

    /**
     * Computes per-stage result records for one competitor.
     *
     * @param competitor               competitor to resolve
     * @param matchStageCompetitorList candidate stage rows
     * @return non-null list of stage records (possibly empty)
     */
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
     * Returns unique non-null competitors from a list.
     *
     * @param competitorList input competitors
     * @return deduplicated set; empty when input is null
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
     * Returns unique non-null match-competitor entities from a list.
     *
     * @param matchCompetitors input list
     * @return deduplicated set; empty when input is null
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
     * Returns unique non-null match-stage-competitor entities from a list.
     *
     * @param matchStageCompetitors input list
     * @return deduplicated set; empty when input is null
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
     * Initialises a {@link ClubDto} from a response model, reusing an existing club when matched.
     *
     * @param clubResponse response club payload
     * @return initialized DTO or empty when input is null
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
     * Initialises a {@link MatchDto} from IPSC response and club context, reusing existing match
     * by name and scheduled date when found.
     *
     * @param ipscResponse source response containing match metadata
     * @param clubDto      optional club DTO association
     * @return initialised match DTO or empty when input match data is missing
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
     * Initialises match stage DTOs from stage response payloads for a given match.
     *
     * @param matchDto       parent match DTO
     * @param stageResponses stage responses to map
     * @return non-null list of initialised stage DTOs
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
     * Initialises competitor DTOs for participating members based on score/member data.
     * <p>
     * Competitors with null/zero final score are excluded, then deduplicated by SAPSA number
     * and entity ID while merging source indexes.
     * </p>
     *
     * @param matchResultsDto match-results context
     * @param ipscResponse    response containing scores and members
     * @return non-null list of competitor DTOs
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

    /**
     * Initialises match-competitor and match-stage-competitor DTO collections on {@code matchResultsDto}.
     *
     * @param matchResultsDto target aggregate to update
     * @param ipscResponse    response providing members/scores/enrollments
     */
    protected void initEnrolledCompetitors(@NotNull MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
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
        Map<Integer, List<EnrolledResponse>> enrolledResponseMap = new HashMap<>();
        scoreMembers.stream().filter(Objects::nonNull)
                .forEach(memberResponse -> {
                    // Collects all enrolled responses for this member (one per enrollment/division)
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

                    // Caches the competitor and all enrolled responses for later use
                    if (optionalCompetitorDto.isPresent() && optionalMemberResponse.isPresent()
                            && !enrolledResponseList.isEmpty()) {
                        competitorDtoMap.put(optionalMemberResponse.get().getMemberId(),
                                optionalCompetitorDto.get());
                        enrolledResponseMap.put(optionalMemberResponse.get().getMemberId(),
                                enrolledResponseList);
                    }
                });

        // Initialises the scores for each competitor per match and stage and collects the enrolled competitors in a list
        List<EnrolledCompetitorDto> enrolledCompetitorDtoList = new ArrayList<>();
        competitorDtoMap.keySet().stream().filter(Objects::nonNull)
                .forEach(memberIndex -> {
                    EnrolledCompetitorDto enrolledCompetitorDto = initCompetitorScores(memberIndex,
                            matchResultsDto, competitorDtoMap, scoreResponses, enrolledResponseMap);
                    enrolledCompetitorDtoList.add(enrolledCompetitorDto);
                });

        // Gets all the match competitors
        List<MatchCompetitorDto> matchCompetitorDtoListFinal = enrolledCompetitorDtoList.stream()
                .filter(Objects::nonNull)
                .flatMap(enrolledCompetitorDto -> enrolledCompetitorDto.getCompetitorMatches().stream())
                .toList();
        // Gets all the match stage competitors
        List<MatchStageCompetitorDto> matchStageCompetitorDtoListFinal = enrolledCompetitorDtoList.stream()
                .filter(Objects::nonNull)
                .flatMap(enrolledCompetitorDto -> enrolledCompetitorDto.getCompetitorMatchStages().stream())
                .toList();

        // Collects all match competitors in the match results DTO
        matchResultsDto.setMatchCompetitors(matchCompetitorDtoListFinal);
        // Collects all stage competitors in the match results DTO
        matchResultsDto.setMatchStageCompetitors(matchStageCompetitorDtoListFinal);
    }

    /**
     * Initialises all score-derived competitor DTOs for one member index.
     *
     * @param memberIndex         member ID/index in the request domain
     * @param matchResultsDto     aggregate context with match/stage metadata
     * @param competitorDtoMap    member-index to competitor mapping
     * @param scoreResponses      match-filtered score responses
     * @param enrolledResponseMap member-index to enrollment rows (division/classification context)
     * @return initialised enrolled competitor DTO
     */
    protected EnrolledCompetitorDto initCompetitorScores(int memberIndex, MatchResultsDto matchResultsDto,
                                                         Map<Integer, CompetitorDto> competitorDtoMap,
                                                         List<ScoreResponse> scoreResponses,
                                                         Map<Integer, List<EnrolledResponse>> enrolledResponseMap) {
        // Gets the competitor DTO and all enrolled responses for this member
        CompetitorDto competitorDto = competitorDtoMap.get(memberIndex);
        List<EnrolledResponse> enrolledResponsesForMember = enrolledResponseMap.get(memberIndex);

        // Attempts to find the match competitor by competitor ID and match ID in the database
        List<MatchCompetitor> matchCompetitorList = new ArrayList<>();
        if (matchResultsDto.getMatch().getId() != null) {
            matchCompetitorList =
                    matchCompetitorEntityService.findMatchCompetitors(matchResultsDto.getMatch().getId(),
                            competitorDto.getId());
        }

        // Creates one match competitor DTO per enrollment (division/firearm type)
        List<MatchCompetitorDto> matchCompetitorDtoList = new ArrayList<>();
        if (matchCompetitorList.isEmpty()) {
            enrolledResponsesForMember.forEach(enrolledResponse ->
                    matchCompetitorDtoList.add(new MatchCompetitorDto(competitorDto,
                            matchResultsDto.getMatch(), memberIndex)));
        } else {
            matchCompetitorList.forEach(matchCompetitor -> {
                MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto(matchCompetitor);
                matchCompetitorDtoList.add(matchCompetitorDto);
            });
        }

        EnrolledCompetitorDto enrolledCompetitorDto = new EnrolledCompetitorDto(matchCompetitorDtoList.getFirst());

        // Filters scores by member ID (deduplicated by stage so matchPoints is not inflated)
        List<ScoreResponse> scores = scoreResponses.stream()
                .filter(Objects::nonNull)
                .filter(sr -> Objects.equals(sr.getMemberId(), memberIndex))
                .filter(sr -> Objects.equals(sr.getMatchId(),
                        matchResultsDto.getMatch().getIndex()))
                .toList();

        // Initialises each match competitor DTO with its corresponding enrolled response
        for (int i = 0; i < matchCompetitorDtoList.size(); i++) {
            MatchCompetitorDto matchCompetitorDto = matchCompetitorDtoList.get(i);
            matchCompetitorDto.setCompetitorIndex(memberIndex);
            matchCompetitorDto.setMatchIndex(matchResultsDto.getMatch().getIndex());
            EnrolledResponse enrolledResponse = (i < enrolledResponsesForMember.size())
                    ? enrolledResponsesForMember.get(i)
                    : enrolledResponsesForMember.getFirst();
            matchCompetitorDto.init(scores, enrolledResponse);
        }
        enrolledCompetitorDto.setCompetitorMatches(matchCompetitorDtoList);

        // Gets the match stage competitors from the match results DTO
        matchResultsDto.getStages().stream().filter(Objects::nonNull)
                .forEach(stageDto -> {
                    // Filters scores by stage ID (already filtered by member ID)
                    Optional<ScoreResponse> optionalStageScoreResponse = scores.stream()
                            .filter(Objects::nonNull)
                            .filter(scoreResponse -> scoreResponse.getStageId() != null)
                            .filter(scoreResponse -> scoreResponse.getStageId().equals(stageDto.getStageNumber()))
                            .findFirst();

                    optionalStageScoreResponse.ifPresent(stageScoreResponse -> {
                        // Attempts to find the match stage competitor by competitor ID and stage ID
                        List<MatchStageCompetitor> matchStageCompetitors =
                                matchStageCompetitorEntityService.findMatchStageCompetitors(stageDto.getId(),
                                        competitorDto.getId());

                        // Creates one match stage competitor DTO per enrollment
                        List<MatchStageCompetitorDto> matchStageCompetitorDtoList = new ArrayList<>();
                        if (matchStageCompetitors.isEmpty()) {
                            enrolledResponsesForMember.forEach(enrolledResponse -> {
                                MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, stageDto);
                                dto.setCompetitorIndex(memberIndex);
                                dto.setMatchStageIndex(stageDto.getIndex());
                                dto.init(stageScoreResponse, enrolledResponse, stageDto);
                                matchStageCompetitorDtoList.add(dto);
                            });
                        } else {
                            matchStageCompetitors.forEach(matchStageCompetitor -> {
                                MatchStageCompetitorDto dto = new MatchStageCompetitorDto(matchStageCompetitor);
                                dto.setCompetitorIndex(memberIndex);
                                dto.setMatchStageIndex(stageDto.getIndex());
                                matchStageCompetitorDtoList.add(dto);
                            });
                        }

                        enrolledCompetitorDto.getCompetitorMatchStages().addAll(matchStageCompetitorDtoList);
                    });
                });

        return enrolledCompetitorDto;
    }

    /**
     * Deduplicates competitor DTO map entries in two passes:
     * <ol>
     *   <li>By non-null SAPSA number.</li>
     *   <li>By non-null persisted competitor ID.</li>
     * </ol>
     * <p>
     * Duplicate entries are merged by appending index references to the first occurrence.
     * Insertion order is preserved via {@link LinkedHashMap}.
     * </p>
     *
     * @param competitorDtoMap input map keyed by member index
     * @return deduplicated map with merged indexes, or {@code null} when input is null
     */
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