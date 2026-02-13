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
import java.util.Optional;

// TODO: Javadoc
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

        List<IpscResponse> ipscResponses = new ArrayList<>();
        // Maps IPSC requests to responses by match ID
        ipscRequestHolder.getMatches().forEach(match -> {
            Optional<IpscResponse> response = createBasicMatch(ipscRequestHolder, match);
            response.ifPresent(ipscResponses::add);
        });

        // Add members to each match
        ipscResponses.forEach(ipscResponse -> addMembersToMatch(ipscResponse, ipscRequestHolder));
        // Add a club to each match
        ipscResponses.forEach(ipscResponse -> addClubToMatch(ipscResponse, ipscRequestHolder));

        return new IpscResponseHolder(ipscResponses);
    }

    @Override
    public IpscMatchRecordHolder generateIpscMatchRecordHolder(List<IpscMatch> ipscMatchEntityList) {
        List<IpscMatchRecord> ipscMatchRecordList = new ArrayList<>();

        ipscMatchEntityList.forEach(match -> {
            // Get the match stages and competitors
            match.setName(ValueUtil.nullAsEmptyString(match.getName()));
            List<IpscMatchStage> matchStageList = match.getMatchStages();
            List<MatchCompetitor> matchCompetitorList = match.getMatchCompetitors();

            // Get the competitors
            List<MatchStageCompetitor> matchStageCompetitorList = getMatchStageCompetitorList(matchStageList);
            List<Competitor> competitorList = getCompetitorList(matchCompetitorList);

            List<CompetitorRecord> competitors = new ArrayList<>();
            competitorList.forEach(c -> initMatchCompetitor(c, matchCompetitorList)
                    .ifPresent((mcr) -> {
                        List<MatchStageCompetitorRecord> thisCompetitorStages =
                                initMatchStageCompetitor(c, matchStageCompetitorList);

                        // Creates competitor response from competitor details
                        initCompetitor(c, mcr, thisCompetitorStages).ifPresent(competitors::add);
                    }));

            Optional<IpscMatchRecord> ipscResponse = initIpscMatchResponse(match, competitors);
            ipscResponse.ifPresent(ipscMatchRecordList::add);
        });

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

        // Add stages to the response, filtered by match ID
        List<StageRequest> stageRequests = ipscRequestHolder.getStages().stream()
                .filter(stage -> matchId.equals(stage.getMatchId()))
                .toList();
        // Add enrolled members to the response, filtered by match ID
        List<EnrolledRequest> enrolledRequests = ipscRequestHolder.getEnrolledMembers().stream()
                .filter(enrolled -> matchId.equals(enrolled.getMatchId()))
                .toList();
        // Add scores to the response, filtered by match ID
        List<ScoreRequest> scoreRequests = ipscRequestHolder.getScores().stream()
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
        ipscRequestHolder.getScores().forEach(scoreRequest -> {
            List<MemberRequest> memberRequests = ipscRequestHolder.getMembers().stream()
                    .filter(memberRequest ->
                            memberRequest.getMemberId().equals(scoreRequest.getMemberId()))
                    .toList();
            // Collects members with scores
            responseMembers.addAll(memberRequests);
        });
        // Sets members on the response
        ipscResponse.setMembers(responseMembers.stream().map(MemberResponse::new).toList());
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

        if ((ipscResponse.getMatch() != null) && (ipscResponse.getMatch().getClubId() != null)) {
            Integer clubId = ipscResponse.getMatch().getClubId();
            // Finds club matching ID or provides default
            ClubRequest club = ipscRequestHolder.getClubs().stream()
                    .filter(clubRequest -> clubId.equals(clubRequest.getClubId()))
                    .findFirst().orElse(null);
            ipscResponse.setClub((club != null) ? new ClubResponse(club) : new ClubResponse(clubId));
        }
    }

    /**
     *
     * @param match
     * @param competitors
     * @return
     */
    protected Optional<IpscMatchRecord> initIpscMatchResponse(IpscMatch match, List<CompetitorRecord> competitors) {
        if ((match == null) || (competitors == null)) {
            return Optional.empty();
        }

        String scheduledDate = DateUtil.formatDateTime(match.getScheduledDate(),
                IpscConstants.IPSC_OUTPUT_DATE_FORMAT);

        String matchFirearmType = ValueUtil.nullAsEmptyString(match.getMatchFirearmType());
        String matchCategory = ValueUtil.nullAsEmptyString(match.getMatchCategory());

        String dateEdited = DateUtil.formatDateTime(match.getDateEdited(),
                IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);

        // Creates match response from match details
        String clubName = ValueUtil.nullAsEmptyString(match.getClubName());
        IpscMatchRecord ipscMatchRecord = new IpscMatchRecord(match.getName(), scheduledDate,
                clubName, matchFirearmType, matchCategory, competitors, dateEdited);
        return Optional.of(ipscMatchRecord);
    }

    /**
     *
     * @param competitor
     * @param thisCompetitorOverall
     * @param thisCompetitorStages
     * @return
     */
    protected Optional<CompetitorRecord> initCompetitor(Competitor competitor,
                                                        MatchCompetitorRecord thisCompetitorOverall,
                                                        List<MatchStageCompetitorRecord> thisCompetitorStages) {
        if ((competitor == null) || (thisCompetitorOverall == null) || (thisCompetitorStages == null)) {
            return Optional.empty();
        }

        // Creates competitor response from competitor details
        String dateOfBirth = DateUtil.formatDate(competitor.getDateOfBirth(),
                IpscConstants.IPSC_OUTPUT_DATE_FORMAT);

        CompetitorRecord competitorRecord = new CompetitorRecord(competitor.getFirstName(),
                competitor.getLastName(), competitor.getMiddleNames(), dateOfBirth,
                competitor.getSapsaNumber(), competitor.getCompetitorNumber(),
                thisCompetitorOverall, thisCompetitorStages);
        return Optional.of(competitorRecord);
    }

    /**
     *
     * @param competitor
     * @param matchCompetitorList
     * @return
     */
    protected Optional<MatchCompetitorRecord> initMatchCompetitor(Competitor competitor,
                                                                  List<MatchCompetitor> matchCompetitorList) {

        if ((competitor == null) || (matchCompetitorList == null)) {
            return Optional.empty();
        }

        MatchCompetitorRecord thisCompetitorOverall = null;
        MatchCompetitor matchCompetitor = matchCompetitorList.stream()
                .filter(mc -> competitor.equals(mc.getCompetitor()))
                .findFirst().orElse(null);

        if (matchCompetitor == null) {
            return Optional.empty();
        }

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
        thisCompetitorOverall = new MatchCompetitorRecord(matchCompetitor.getClub().toString(),
                firearmType, division, powerFactor, competitorCategory, matchPoints, matchRanking, dateEdited);
        return Optional.of(thisCompetitorOverall);
    }

    /**
     *
     * @param competitor
     * @param matchStageCompetitorList
     * @return
     */
    protected List<MatchStageCompetitorRecord> initMatchStageCompetitor(Competitor competitor,
                                                                        List<MatchStageCompetitor> matchStageCompetitorList) {

        if ((competitor == null) || (matchStageCompetitorList == null)) {
            return new ArrayList<>();
        }

        List<MatchStageCompetitorRecord> thisCompetitorStages = new ArrayList<>();
        // Filters and maps stage data to response objects
        matchStageCompetitorList.stream()
                .filter(msc -> competitor.equals(msc.getCompetitor()))
                .forEach(msc -> {

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
     *
     * @param matchCompetitorList
     * @return
     */
    protected List<Competitor> getCompetitorList(List<MatchCompetitor> matchCompetitorList) {
        if (matchCompetitorList == null) {
            return new ArrayList<>();
        }

        return matchCompetitorList.stream()
                .map(MatchCompetitor::getCompetitor)
                .toList();
    }

    /**
     *
     * @param matchStageList
     * @return
     */
    protected List<MatchStageCompetitor> getMatchStageCompetitorList(List<IpscMatchStage> matchStageList) {
        if (matchStageList == null) {
            return new ArrayList<>();
        }

        return matchStageList.stream()
                .map(IpscMatchStage::getMatchStageCompetitors)
                .flatMap(List::stream)
                .toList();
    }
}
