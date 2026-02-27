package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.utils.DateUtil;
import za.co.hpsc.web.utils.NumberUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    protected final TransactionService transactionService;

    public IpscMatchServiceImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
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

    /**
     * Generates an IPSC match record holder containing match records for the provided list of IPSC matches.
     *
     * @param ipscMatchEntityList A list of IPSC match entities to be processed.
     * @return An IPSC match record holder containing the processed match records.
     */
    @Override
    public IpscMatchRecordHolder generateIpscMatchRecordHolder(List<IpscMatch> ipscMatchEntityList) {
        if (ipscMatchEntityList == null) {
            return new IpscMatchRecordHolder(new ArrayList<>());
        }

        List<IpscMatchRecord> ipscMatchRecordList = new ArrayList<>();
        for (IpscMatch match : ipscMatchEntityList.stream().filter(Objects::nonNull).toList()) {
            // Get the match stages and competitors
            match.setName(ValueUtil.nullAsEmptyString(match.getName()));
            List<IpscMatchStage> matchStageList = new ArrayList<>();
            List<MatchCompetitor> matchCompetitorList = new ArrayList<>();

            // Get the competitors
            List<MatchStageCompetitor> matchStageCompetitorList = getMatchStageCompetitorList(matchStageList);
            List<Competitor> competitorList = getCompetitorList(matchCompetitorList);

            List<CompetitorMatchRecord> competitors = new ArrayList<>();
            competitorList.stream().filter(Objects::nonNull)
                    .forEach(c -> initMatchCompetitor(c, matchCompetitorList)
                            .ifPresent((mcr) -> {
                                List<MatchStageCompetitorRecord> thisCompetitorStages =
                                        initMatchStageCompetitor(c, matchStageCompetitorList);

                                // Creates competitor response from competitor details
                                initCompetitor(c, mcr, thisCompetitorStages).ifPresent(competitors::add);
                            }));

            Optional<IpscMatchRecord> ipscResponse = initIpscMatchResponse(match, competitors);
            ipscResponse.ifPresent(ipscMatchRecordList::add);
        }

        return new IpscMatchRecordHolder(ipscMatchRecordList);
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
            // Finds club matching ID or provides default
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
     * @param match       The IPSC match object containing match details. If null, an empty Optional is returned.
     * @param competitors A list of competitor match records associated with the match. If null, an empty Optional is returned.
     * @return An Optional containing the initialized {@link IpscMatchRecord} if both inputs are non-null,
     * otherwise an empty Optional.
     */
    protected Optional<IpscMatchRecord> initIpscMatchResponse(IpscMatch match, List<CompetitorMatchRecord> competitors) {
        if ((match == null) || (competitors == null)) {
            return Optional.empty();
        }

        // Initialises match details
        String clubName = ((match.getClub() != null) ? match.getClub().toString() : "");

        String scheduledDate = DateUtil.formatDateTime(match.getScheduledDate(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        String matchFirearmType = ValueUtil.nullAsEmptyString(match.getMatchFirearmType());
        String matchCategory = ValueUtil.nullAsEmptyString(match.getMatchCategory());

        String dateEdited = DateUtil.formatDateTime(match.getDateEdited(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        // Creates match response from match details
        IpscMatchRecord ipscMatchRecord = new IpscMatchRecord(match.getName(), scheduledDate,
                clubName, matchFirearmType, matchCategory, competitors, dateEdited);
        return Optional.of(ipscMatchRecord);
    }

    /**
     * Initialises a {@link CompetitorMatchRecord} based on the given Competitor and match records.
     *
     * @param competitor            the Competitor object containing personal and identification details.
     * @param thisCompetitorOverall the overall match record of the competitor.
     * @param thisCompetitorStages  a list of stage-wise match records of the competitor.
     * @return an Optional containing the initialized {@link CompetitorMatchRecord} if all inputs are
     * non-null; otherwise an empty Optional.
     */
    protected Optional<CompetitorMatchRecord> initCompetitor(Competitor competitor,
                                                             MatchCompetitorRecord thisCompetitorOverall,
                                                             List<MatchStageCompetitorRecord> thisCompetitorStages) {
        if ((competitor == null) || (thisCompetitorOverall == null) || (thisCompetitorStages == null)) {
            return Optional.empty();
        }

        // Creates competitor response from competitor details
        String dateOfBirth = DateUtil.formatDate(competitor.getDateOfBirth(),
                IpscConstants.IPSC_OUTPUT_DATE_FORMAT);

        CompetitorMatchRecord competitorRecord = new CompetitorMatchRecord(competitor.getFirstName(),
                competitor.getLastName(), competitor.getMiddleNames(), dateOfBirth,
                competitor.getSapsaNumber(), competitor.getCompetitorNumber(),
                thisCompetitorOverall, thisCompetitorStages);
        return Optional.of(competitorRecord);
    }

    /**
     * Initialises a {@link MatchCompetitorRecord} object for the specified competitor based on the provided list of
     * {@link MatchCompetitor} objects.
     *
     * <p>
     * The method finds the relevant {@link MatchCompetitor} for the given competitor, extracts the necessary
     * details, formats them, and creates a {@link MatchCompetitorRecord}. If the input {@code competitor}
     * is null, the list {@code matchCompetitorList} is null, or no matching competitor is found in the
     * list, the method returns an empty {@code Optional}.
     * </p>
     *
     * @param competitor          the {@link Competitor} object for which a match competitor record needs to be initialised.
     *                            If it is null, the method will return {@code Optional.empty()}.
     * @param matchCompetitorList the list of {@link MatchCompetitor} objects from which the relevant match competitor
     *                            will be identified.
     *                            If it is null, the method will return {@code Optional.empty()}.
     * @return an {@code Optional} containing the initialized {@link MatchCompetitorRecord} if successful, or an empty
     * {@code Optional} if the input is invalid or no matching competitor is found.
     */
    protected Optional<MatchCompetitorRecord> initMatchCompetitor(Competitor competitor,
                                                                  List<MatchCompetitor> matchCompetitorList) {

        if ((competitor == null) || (matchCompetitorList == null)) {
            return Optional.empty();
        }

        MatchCompetitorRecord thisCompetitorOverall = null;
        // Filters and maps overall data to the response object
        MatchCompetitor matchCompetitor = matchCompetitorList.stream()
                .filter(Objects::nonNull)
                .filter(mc -> competitor.equals(mc.getCompetitor()))
                .findFirst().orElse(null);

        if (matchCompetitor == null) {
            return Optional.empty();
        }

        // Initialises match competitor details
        String clubName = ((matchCompetitor.getMatch() != null) &&
                (matchCompetitor.getMatch().getClub() != null)) ?
                ValueUtil.nullAsEmptyString(matchCompetitor.getMatch().getClub().getName()) : "";

        String firearmType = ValueUtil.nullAsEmptyString(matchCompetitor.getFirearmType());
        String division = ValueUtil.nullAsEmptyString(matchCompetitor.getDivision());
        String powerFactor = ValueUtil.nullAsEmptyString(matchCompetitor.getPowerFactor());
        String competitorCategory = ValueUtil.nullAsEmptyString(matchCompetitor.getCompetitorCategory());

        String matchPoints = NumberUtil.formatBigDecimal(matchCompetitor.getMatchPoints(),
                IpscConstants.MATCH_POINTS_SCALE);
        String matchRanking = NumberUtil.formatBigDecimal(matchCompetitor.getMatchRanking(),
                IpscConstants.PERCENTAGE_SCALE);

        String dateEdited = DateUtil.formatDateTime(matchCompetitor.getDateEdited(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        // Formats competitor details for match competitor response
        thisCompetitorOverall = new MatchCompetitorRecord(clubName, firearmType, division,
                powerFactor, competitorCategory, matchPoints, matchRanking, dateEdited);
        return Optional.of(thisCompetitorOverall);
    }

    /**
     * Initialises and retrieves a list of {@link MatchStageCompetitorRecord} for the given competitor,
     * based on the provided list of {@link MatchStageCompetitor}.
     *
     * <p>
     * The method filters the match stage competitor list to include only entities matching the provided
     * competitor, processes the data, and maps it to a structured response format.
     * </p>
     *
     * @param competitor               The {@link Competitor} object for which the match stage competitor records
     *                                 are to be initialised. If null, an empty list is returned.
     * @param matchStageCompetitorList A list of {@link MatchStageCompetitor} objects representing
     *                                 stage details. If null, an empty list is returned.
     * @return A list of {@link MatchStageCompetitorRecord} objects containing the initialised
     * details for the specified competitor. If no match stage competitors belong to the
     * provided competitor or if any input is null, an empty list is returned.
     */
    protected List<MatchStageCompetitorRecord> initMatchStageCompetitor(Competitor competitor,
                                                                        List<MatchStageCompetitor> matchStageCompetitorList) {

        if ((competitor == null) || (matchStageCompetitorList == null)) {
            return new ArrayList<>();
        }

        List<MatchStageCompetitorRecord> thisCompetitorStages = new ArrayList<>();
        // Filters and maps stage data to response objects
        matchStageCompetitorList.stream()
                .filter(Objects::nonNull)
                .filter(msc -> competitor.equals(msc.getCompetitor()))
                .forEach(msc -> {

                    // Initialises match stage competitor details
                    String firearmType = ValueUtil.nullAsEmptyString(msc.getFirearmType());
                    String division = ValueUtil.nullAsEmptyString(msc.getDivision());
                    String powerFactor = ValueUtil.nullAsEmptyString(msc.getPowerFactor());
                    String competitorCategory = ValueUtil.nullAsEmptyString(msc.getCompetitorCategory());

                    String time = NumberUtil.formatBigDecimal(msc.getTime(),
                            IpscConstants.TIME_SCALE);
                    String hitFactor = NumberUtil.formatBigDecimal(msc.getHitFactor(),
                            IpscConstants.HIT_FACTOR_SCALE);

                    String stagePoints = NumberUtil.formatBigDecimal(msc.getStagePoints(),
                            IpscConstants.STAGE_POINTS_SCALE);
                    String stagePercentage = NumberUtil.formatBigDecimal(msc.getStagePercentage(),
                            IpscConstants.PERCENTAGE_SCALE);
                    String stageRanking = NumberUtil.formatBigDecimal(msc.getStageRanking(),
                            IpscConstants.PERCENTAGE_SCALE);

                    String dateEdited = DateUtil.formatDateTime(msc.getDateEdited(),
                            IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

                    // Formats competitor details for match stage competitor response
                    MatchStageCompetitorRecord thisCompetitorStage = new MatchStageCompetitorRecord(
                            firearmType, division, powerFactor, competitorCategory,
                            msc.getScoreA(), msc.getScoreB(), msc.getScoreC(), msc.getScoreD(),
                            msc.getPoints(), msc.getMisses(), msc.getPenalties(), msc.getProcedurals(),
                            time, hitFactor, stagePoints, stagePercentage, stageRanking,
                            dateEdited);
                    thisCompetitorStages.add(thisCompetitorStage);
                });
        return thisCompetitorStages;
    }

    /**
     * Retrieves a list of competitors from the provided match competitor list.
     *
     * @param matchCompetitorList the list of MatchCompetitor objects to the process.
     *                            If null, an empty list will be returned.
     * @return a list of Competitor objects extracted from the match competitor list.
     * If the input list is null or contains null elements, these are handled appropriately.
     */
    protected List<Competitor> getCompetitorList(List<MatchCompetitor> matchCompetitorList) {
        if (matchCompetitorList == null) {
            return new ArrayList<>();
        }

        // Gets competitors from the match competitor list
        return matchCompetitorList.stream()
                .filter(Objects::nonNull)
                .map(MatchCompetitor::getCompetitor)
                .toList();
    }

    /**
     * Retrieves a list of competitors from the provided match stage list.
     *
     * @param matchStageList the list of {@link IpscMatchStage} objects. If null, an empty list is returned.
     * @return a list of {@link MatchStageCompetitor} objects aggregated from all non-null stages in the input list.
     * If the input list is null or contains no valid stages, the returned list will be empty.
     */
    protected List<MatchStageCompetitor> getMatchStageCompetitorList(List<IpscMatchStage> matchStageList) {
        if (matchStageList == null) {
            return new ArrayList<>();
        }

        // Gets competitors from the match stage list
        return matchStageList.stream()
                .filter(Objects::nonNull)
                .map(IpscMatchStage::getMatchStageCompetitors)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }
}
