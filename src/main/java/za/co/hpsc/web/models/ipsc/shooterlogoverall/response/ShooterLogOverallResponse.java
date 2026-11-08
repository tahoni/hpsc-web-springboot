package za.co.hpsc.web.models.ipsc.shooterlogoverall.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.utils.NumberUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A persisted shooter log overall, as returned by {@code IpscShooterLogOverallController}.
 *
 * <p>
 * Built from a {@link ShooterLogOverall} via {@link #ShooterLogOverallResponse(ShooterLogOverall)}.
 * </p>
 *
 * @see za.co.hpsc.web.controllers.IpscShooterLogOverallController
 * @since 16.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterLogOverallResponse {
    /** The shooter log overall's own identifier. */
    private Long shooterLogOverallId;
    /** The identifier of the shooter log the overall belongs to. */
    private Long shooterLogId;
    /** The identifier of the competitor the overall is for. */
    private Long competitorId;

    /**
     * The names the competitor is known by, each once: their first and last name, then their nickname and last name
     * if they have a nickname that differs.
     */
    private List<String> competitorNames = new ArrayList<>();
    /** The competitor's number, as assigned for competition, may be null. */
    private Integer competitorNumber;

    /** The competitor's category. */
    private CompetitorCategory competitorCategory;
    /** The division the competitor is ranked in. */
    private Division division;

    /** The competitor's overall rank in the log, if calculated. */
    private Integer rankInLog;
    /**
     * The competitor's overall percentage in the log, if calculated, formatted to
     * {@link IpscConstants#PERCENTAGE_SCALE} decimal places for display.
     */
    private String percentageInLog;
    /** When the overall rank and percentage in the log were calculated, if they were. */
    private LocalDateTime dateCalculated;

    /**
     * Creates a response from a persisted shooter log overall.
     *
     * @param shooterLogOverall the overall to convert. Must not be null and must have its shooter log and
     *                          competitor set. Those lazily loaded associations are read here, so call this while
     *                          the persistence session is still open, or after fetch-joining them.
     */
    public ShooterLogOverallResponse(ShooterLogOverall shooterLogOverall) {
        this.shooterLogOverallId = shooterLogOverall.getId();
        this.shooterLogId = shooterLogOverall.getShooterLog().getId();
        this.competitorId = shooterLogOverall.getCompetitor().getId();

        Competitor competitor = shooterLogOverall.getCompetitor();
        // A set keeps each name once, in order, when the nickname matches the first name or is absent
        Set<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.add(competitor.getFirstName() + " " + competitor.getLastName());
        if (competitor.getNickName() != null) {
            uniqueNames.add(competitor.getNickName() + " " + competitor.getLastName());
        }
        this.competitorNames = new ArrayList<>(uniqueNames);
        this.competitorNumber = competitor.getCompetitorNumber();

        this.competitorCategory = shooterLogOverall.getCompetitorCategory();
        this.division = shooterLogOverall.getDivision();

        this.rankInLog = shooterLogOverall.getRankInLog();
        // Formatted for display only; an overall with no percentage yet stays null rather than reading 0.00
        this.percentageInLog = (shooterLogOverall.getPercentageInLog() == null) ? null
                : NumberUtil.formatBigDecimal(shooterLogOverall.getPercentageInLog(),
                IpscConstants.PERCENTAGE_SCALE);
        this.dateCalculated = shooterLogOverall.getDateCalculated();
    }
}
