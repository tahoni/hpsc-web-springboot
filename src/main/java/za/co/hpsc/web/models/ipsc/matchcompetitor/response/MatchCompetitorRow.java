package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;

import java.util.Objects;

/**
 * Every value a bulk import row supplied, exactly as given and all as text, with a value the row left out reported as
 * an empty string. Nothing is resolved or looked up, so it can describe a row that could not be imported, whatever is
 * missing or unknown in it.
 *
 * @see MatchCompetitorBulkResponse
 * @since 14.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchCompetitorRow {
    private String matchCompetitorId = "";
    private String competitorId = "";
    private String competitorName = "";
    private String competitorNumber = "";
    private String matchId = "";
    private String matchClub = "";
    private String competitorCategory = "";
    private String firearmType = "";
    private String division = "";
    private String powerFactor = "";
    private String points = "";
    private String percentage = "";
    private String time = "";
    private String percentageOfPossiblePoints = "";
    private String alpha = "";
    private String charlie = "";
    private String delta = "";
    private String misses = "";
    private String noPenaltyMisses = "";
    private String noShoots = "";
    private String proceduralErrors = "";
    private String additionalPenalties = "";
    private String overallRanking = "";
    private String clubRanking = "";
    private String isVisitor = "";

    /**
     * Describes a request by the values it supplied.
     *
     * @param request the request to describe; may be null, as for an empty row, in which case every value is empty.
     */
    public MatchCompetitorRow(MatchCompetitorRequest request) {
        if (request == null) {
            return;
        }
        matchCompetitorId = text(request.getMatchCompetitorId());
        competitorId = text(request.getCompetitorId());
        competitorName = text(request.getCompetitorName());
        competitorNumber = text(request.getCompetitorNumber());
        matchId = text(request.getMatchId());
        matchClub = text(request.getMatchClub());
        competitorCategory = text(request.getCompetitorCategory());
        firearmType = text(request.getFirearmType());
        division = text(request.getDivision());
        powerFactor = text(request.getPowerFactor());
        points = text(request.getPoints());
        percentage = text(request.getPercentage());
        time = text(request.getTime());
        percentageOfPossiblePoints = text(request.getPercentageOfPossiblePoints());
        alpha = text(request.getAlpha());
        charlie = text(request.getCharlie());
        delta = text(request.getDelta());
        misses = text(request.getMisses());
        noPenaltyMisses = text(request.getNoPenaltyMisses());
        noShoots = text(request.getNoShoots());
        proceduralErrors = text(request.getProceduralErrors());
        additionalPenalties = text(request.getAdditionalPenalties());
        overallRanking = text(request.getOverallRanking());
        clubRanking = text(request.getClubRanking());
        isVisitor = text(request.getIsVisitor());
    }

    private static String text(Object value) {
        return Objects.toString(value, "");
    }
}
