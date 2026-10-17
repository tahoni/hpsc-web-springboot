package za.co.hpsc.web.models.ipsc.shooterresult.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.ShooterResult;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A persisted shooter result, as returned by {@code IpscShooterResultController}.
 *
 * <p>
 * Built from a {@link ShooterResult} via {@link #ShooterResultResponse(ShooterResult)}.
 * </p>
 *
 * @see za.co.hpsc.web.controllers.IpscShooterResultController
 * @since 15.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterResultResponse {
    /** The shooter result's own identifier. */
    private Long shooterResultId;
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

    /** The competitor's rank in the log, if calculated. */
    private Integer rankInLog;
    /** The competitor's percentage in the log, if calculated. */
    private BigDecimal percentageInLog;
    /** When the rank and percentage in the log were calculated, if they were. */
    private LocalDateTime dateCalculated;

    /**
     * Creates a response from a persisted shooter result.
     *
     * @param shooterResult the shooter result to convert. Must not be null and must have its match, competitor and
     *                      match competitor set. Those lazily loaded associations are read here, so call this while
     *                      the persistence session is still open, or after fetch-joining them.
     */
    public ShooterResultResponse(ShooterResult shooterResult) {
        this.shooterResultId = shooterResult.getId();
        this.matchId = shooterResult.getMatch().getId();
        this.competitorId = shooterResult.getCompetitor().getId();
        this.matchCompetitorId = shooterResult.getMatchCompetitor().getId();

        Competitor competitor = shooterResult.getCompetitor();
        // A set keeps each name once, in order, when the nickname matches the first name or is absent
        Set<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.add(competitor.getFirstName() + " " + competitor.getLastName());
        if (competitor.getNickName() != null) {
            uniqueNames.add(competitor.getNickName() + " " + competitor.getLastName());
        }
        this.competitorNames = new ArrayList<>(uniqueNames);
        this.competitorNumber = competitor.getCompetitorNumber();

        this.competitorCategory = shooterResult.getCompetitorCategory();
        this.division = shooterResult.getDivision();

        this.rankInLog = shooterResult.getRankInLog();
        this.percentageInLog = shooterResult.getPercentageInLog();
        this.dateCalculated = shooterResult.getDateCalculated();
    }
}
