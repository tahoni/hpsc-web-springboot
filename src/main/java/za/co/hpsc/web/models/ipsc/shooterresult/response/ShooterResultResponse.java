package za.co.hpsc.web.models.ipsc.shooterresult.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * One shooter's result in one match, in one firearm type, as returned by {@code IpscShooterResultController}.
 *
 * <p>
 * A slimmer view of a {@link MatchCompetitor} than {@code MatchCompetitorResponse}: it names the shooter and carries
 * the headline score and rankings, but not the hit and penalty breakdown. The match is described by
 * {@link ShooterMatchResultResponse}, which {@link ShooterResponseHolder} links to its results.
 * </p>
 *
 * @since 15.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterResultResponse {
    /** The identifier of the competitor who shot the match. */
    private Long competitorId;
    /**
     * The names the competitor is known by, each once: their first and last name, then their nickname and last name
     * if they have a nickname that differs.
     */
    private List<String> competitorNames = new ArrayList<>();
    /** The competitor's number, as assigned for competition, may be null. */
    private Integer competitorNumber;

    /** The club the competitor represented at the match, if any. */
    private ClubIdentifier matchClub;
    /** The competitor's category at the match. */
    private CompetitorCategory competitorCategory;
    /** The firearm type the competitor shot. */
    private FirearmType firearmType;
    /** The division the competitor shot. */
    private Division division;
    /** The competitor's power factor. */
    private PowerFactor powerFactor;

    /** The competitor's match points, if any. */
    private BigDecimal points;
    /** The competitor's overall match score as a percentage of the match winner's score, if any. */
    private BigDecimal percentage;
    /** The competitor's total time, in seconds, taken across the match's stages, if any. */
    private BigDecimal time;
    /** The competitor's overall ranking in the match, if any. */
    private BigDecimal overallRanking;
    /** The competitor's ranking among their club's competitors in the match, if any. */
    private BigDecimal clubRanking;
    /** Whether the competitor was a visitor at the match, if recorded. */
    private Boolean isVisitor;

    /**
     * Creates a shooter result from a persisted match competitor.
     *
     * @param matchCompetitor the match competitor to convert. Must not be null and must have its competitor set.
     *                        That lazily loaded association is read here, so call this while the persistence
     *                        session is still open, or after fetch-joining it.
     */
    public ShooterResultResponse(MatchCompetitor matchCompetitor) {
        Competitor competitor = matchCompetitor.getCompetitor();
        this.competitorId = competitor.getId();
        // A set keeps each name once, in order, when the nickname matches the first name or is absent
        Set<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.add(competitor.getFirstName() + " " + competitor.getLastName());
        if (competitor.getNickName() != null) {
            uniqueNames.add(competitor.getNickName() + " " + competitor.getLastName());
        }
        this.competitorNames = new ArrayList<>(uniqueNames);
        this.competitorNumber = competitor.getCompetitorNumber();

        this.matchClub = matchCompetitor.getMatchClub();
        this.competitorCategory = matchCompetitor.getCompetitorCategory();
        this.firearmType = matchCompetitor.getFirearmType();
        this.division = matchCompetitor.getDivision();
        this.powerFactor = matchCompetitor.getPowerFactor();

        this.points = matchCompetitor.getPoints();
        this.percentage = matchCompetitor.getPercentage();
        this.time = matchCompetitor.getTime();
        this.overallRanking = matchCompetitor.getOverallRanking();
        this.clubRanking = matchCompetitor.getClubRanking();
        this.isVisitor = matchCompetitor.getIsVisitor();
    }
}
