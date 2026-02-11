package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.records.*;
import za.co.hpsc.web.services.IpscMatchResponseService;
import za.co.hpsc.web.utils.NumberUtil;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IpscMatchResponseServiceImpl implements IpscMatchResponseService {
    @Override
    public IpscMatchResponseHolder generateIpscMatchResultResponse(List<IpscMatch> ipscMatchEntityList) {
        List<IpscMatchResponse> ipscMatchResponseList = new ArrayList<>();

        ipscMatchEntityList.forEach(match -> {
            // Get the match stages and competitors
            List<IpscMatchStage> matchStageList = match.getMatchStages();
            List<MatchCompetitor> matchCompetitorList = match.getMatchCompetitors();

            // Get the competitors
            List<MatchStageCompetitor> matchStageCompetitorList = getMatchStageCompetitorList(matchStageList);
            List<Competitor> competitorList = getCompetitorList(matchCompetitorList);

            List<CompetitorResponse> competitors = new ArrayList<>();
            competitorList.forEach(competitor -> {
                MatchCompetitorResponse thisCompetitorOverall =
                        initMatchCompetitor(competitor, matchCompetitorList);
                List<MatchStageCompetitorResponse> thisCompetitorStages =
                        initMatchStageCompetitor(competitor, matchStageCompetitorList);

                // Creates competitor response from competitor details
                CompetitorResponse competitorResponse = initCompetitor(competitor,
                        thisCompetitorOverall, thisCompetitorStages);
                competitors.add(competitorResponse);
            });

            IpscMatchResponse ipscMatchResponse = initIpscMatchResponse(match, competitors);
            ipscMatchResponseList.add(ipscMatchResponse);
        });

        return new IpscMatchResponseHolder(ipscMatchResponseList);
    }

    protected IpscMatchResponse initIpscMatchResponse(IpscMatch match, List<CompetitorResponse> competitors) {
        String scheduledDate = DateTimeFormatter.ofPattern(IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT)
                .format(match.getScheduledDate());

        String dateEdited = DateTimeFormatter.ofPattern(IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT)
                .format(match.getDateEdited());

        // Creates match response from match details
        return new IpscMatchResponse(match.getName(), scheduledDate, match.getClubName().toString(),
                match.getMatchFirearmType().toString(), match.getMatchCategory().toString(), dateEdited, competitors);
    }

    protected CompetitorResponse initCompetitor(Competitor competitor,
                                                MatchCompetitorResponse thisCompetitorOverall,
                                                List<MatchStageCompetitorResponse> thisCompetitorStages) {

        // Creates competitor response from competitor details
        String dateOfBirth = DateTimeFormatter.ofPattern(IpscConstants.IPSC_OUTPUT_DATE_FORMAT)
                .format(competitor.getDateOfBirth());
        return new CompetitorResponse(competitor.getFirstName(),
                competitor.getLastName(), competitor.getMiddleNames(), dateOfBirth,
                competitor.getSapsaNumber(), competitor.getCompetitorNumber(),
                thisCompetitorOverall, thisCompetitorStages);
    }

    /**
     * Initializes match competitor response from competitor details
     */
    protected MatchCompetitorResponse initMatchCompetitor(Competitor competitor,
                                                          List<MatchCompetitor> matchCompetitorList) {

        MatchCompetitorResponse thisCompetitorOverall = null;
        MatchCompetitor matchCompetitor = matchCompetitorList.stream()
                .filter(mc -> competitor.equals(mc.getCompetitor()))
                .findFirst().orElse(null);

        if (matchCompetitor == null) {
            return null;
        }

        String matchPoints = NumberUtil.formatBigDecimal(matchCompetitor.getMatchPoints(),
                IpscConstants.MATCH_POINTS_SCALE);
        String matchRanking = NumberUtil.formatBigDecimal(matchCompetitor.getMatchRanking(),
                IpscConstants.PERCENTAGE_SCALE);

        String dateEdited = DateTimeFormatter.ofPattern(IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT)
                .format(matchCompetitor.getDateEdited());

        thisCompetitorOverall =
                // Formats competitor details for match competitor response
                new MatchCompetitorResponse(matchCompetitor.getClub().toString(),
                        matchCompetitor.getFirearmType().toString(),
                        matchCompetitor.getDivision().toString(),
                        matchCompetitor.getPowerFactor().toString(),
                        matchCompetitor.getCompetitorCategory().toString(),
                        matchPoints, matchRanking,
                        dateEdited);
        return thisCompetitorOverall;
    }

    protected List<MatchStageCompetitorResponse> initMatchStageCompetitor(Competitor competitor,
                                                                          List<MatchStageCompetitor> matchStageCompetitorList) {

        List<MatchStageCompetitorResponse> thisCompetitorStages = new ArrayList<>();
        // Filters and maps stage data to response objects
        matchStageCompetitorList.stream()
                .filter(msc -> competitor.equals(msc.getCompetitor()))
                .forEach(msc -> {

                    String time = NumberUtil.formatBigDecimal(msc.getTime(), IpscConstants.TIME_SCALE);
                    String hitFactor = NumberUtil.formatBigDecimal(msc.getHitFactor(), IpscConstants.HIT_FACTOR_SCALE);

                    String stagePoints = NumberUtil.formatBigDecimal(msc.getStagePoints(), IpscConstants.STAGE_POINTS_SCALE);
                    String stagePercentage = NumberUtil.formatBigDecimal(msc.getStagePercentage(), IpscConstants.PERCENTAGE_SCALE);
                    String stageRanking = NumberUtil.formatBigDecimal(msc.getStageRanking(), IpscConstants.PERCENTAGE_SCALE);

                    String dateEdited = DateTimeFormatter.ofPattern(IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT)
                            .format(msc.getDateEdited());

                    MatchStageCompetitorResponse thisCompetitorStage =
                            // Formats competitor details for match stage competitor response
                            new MatchStageCompetitorResponse(msc.getFirearmType().toString(),
                                    msc.getDivision().toString(), msc.getPowerFactor().toString(),
                                    msc.getCompetitorCategory().toString(),
                                    msc.getScoreA(), msc.getScoreB(), msc.getScoreC(), msc.getScoreD(),
                                    msc.getPoints(), msc.getMisses(), msc.getPenalties(), msc.getProcedurals(),
                                    time, hitFactor, stagePoints, stagePercentage, stageRanking,
                                    dateEdited);
                    thisCompetitorStages.add(thisCompetitorStage);
                });
        return thisCompetitorStages;
    }

    protected List<Competitor> getCompetitorList(List<MatchCompetitor> matchCompetitorList) {
        return matchCompetitorList.stream()
                .map(MatchCompetitor::getCompetitor)
                .toList();
    }

    protected List<MatchStageCompetitor> getMatchStageCompetitorList(List<IpscMatchStage> matchStageList) {
        return matchStageList.stream()
                .map(IpscMatchStage::getMatchStageCompetitors)
                .flatMap(List::stream)
                .toList();
    }
}
