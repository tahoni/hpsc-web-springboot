package za.co.hpsc.web.models.ipsc.matchresult.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.MatchResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.utils.NumberUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A persisted match result, as returned by {@code IpscMatchResultController}.
 *
 * <p>
 * Built from a {@link MatchResult} via {@link #MatchResultResponse(MatchResult)}.
 * </p>
 *
 * @see za.co.hpsc.web.controllers.IpscMatchResultController
 * @since 18.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultResponse {
    /** The match result's own identifier. */
    private Long matchResultId;
    /** The identifier of the match the result was shot in. */
    private Long matchId;
    /** The identifier of the competitor who shot the match. */
    private Long competitorId;
    /** The identifier of the competitor's entry in the match that the result belongs to. */
    private Long matchCompetitorId;

    /**
     * The names the competitor is known by, each once: their first and last name, then their nickname and last name
     * if they have a nickname that differs.
     */
    private List<String> competitorNames = new ArrayList<>();
    /** The competitor's number, as assigned for competition, may be null. */
    private Integer competitorNumber;

    /** The competitor's category. */
    private CompetitorCategory competitorCategory;
    /** The division the competitor shot. */
    private Division division;

    /** The competitor's rank in the match overall, if calculated. */
    private Integer rankInLog;
    /**
     * The competitor's percentage in the match overall, if calculated, formatted to
     * {@link IpscConstants#PERCENTAGE_SCALE} decimal places for display.
     */
    private String percentageInLog;
    /** When the rank and percentage were calculated, if they were. */
    private LocalDateTime dateCalculated;

    /**
     * Creates a response from a persisted match result.
     *
     * @param matchResult the match result to convert. Must not be null and must have its match, competitor and
     *                      match competitor set. Those lazily loaded associations are read here, so call this while
     *                      the persistence session is still open, or after fetch-joining them.
     * @since 18.1.0
     */
    public MatchResultResponse(MatchResult matchResult) {
        this.matchResultId = matchResult.getId();
        this.matchId = matchResult.getMatch().getId();
        this.competitorId = matchResult.getCompetitor().getId();
        this.matchCompetitorId = matchResult.getMatchCompetitor().getId();

        Competitor competitor = matchResult.getCompetitor();
        // A set keeps each name once, in order, when the nickname matches the first name or is absent
        Set<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.add(competitor.getFirstName() + " " + competitor.getLastName());
        if (competitor.getNickName() != null) {
            uniqueNames.add(competitor.getNickName() + " " + competitor.getLastName());
        }
        this.competitorNames = new ArrayList<>(uniqueNames);
        this.competitorNumber = competitor.getCompetitorNumber();

        this.competitorCategory = matchResult.getCompetitorCategory();
        this.division = matchResult.getDivision();

        this.rankInLog = matchResult.getRankInLog();
        // Formatted for display only; a result with no percentage yet stays null rather than reading 0.00
        this.percentageInLog = (matchResult.getPercentageInLog() == null) ? null
                : NumberUtil.formatBigDecimal(matchResult.getPercentageInLog(), IpscConstants.PERCENTAGE_SCALE);
        this.dateCalculated = matchResult.getDateCalculated();
    }
}
