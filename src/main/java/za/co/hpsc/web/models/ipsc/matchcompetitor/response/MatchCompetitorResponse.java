package za.co.hpsc.web.models.ipsc.matchcompetitor.response;

import jakarta.validation.constraints.NotNull;
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
 * A persisted IPSC match competitor, as returned by {@code MatchCompetitorController}'s CRUD endpoints.
 *
 * <p>
 * Built from a {@link MatchCompetitor} via {@link #MatchCompetitorResponse(MatchCompetitor)}, in which case every
 * field is set from the persisted data.
 * </p>
 *
 * <p>
 * A bulk import row that failed has no response: {@link MatchCompetitorBulkResponse#getMatchCompetitor()} is
 * {@code null} for it, and the row's values are in {@link MatchCompetitorBulkResponse#getMatchCompetitorRow()} instead.
 * </p>
 *
 * @since 9.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCompetitorResponse {
    /** The match competitor's own identifier. */
    private Long matchCompetitorId;
    /** The identifier of the competitor who shot the match. */
    @NotNull
    private Long competitorId;
    /** The identifier of the match the competitor shot. */
    @NotNull
    private Long matchId;

    /**
     * The names the competitor is known by, each once: their first and last name, then their nickname and last name
     * if they have a nickname that differs. Empty if none is known, as for a failed bulk import row with no name.
     */
    private List<String> competitorNames = new ArrayList<>();
    /** The competitor's number, as assigned for competition, may be null. */
    private Integer competitorNumber;

    /** The club the competitor represented at the match, which is distinct from their home club. */
    private ClubIdentifier matchClub;
    /** The competitor's category at the match. */
    private CompetitorCategory competitorCategory;
    /**
     * The firearm type the competitor shot.
     */
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
    /** The competitor's total hits as a percentage of the maximum points available in the match, if any. */
    private BigDecimal percentageOfPossiblePoints;
    /** The competitor's total A-zone (alpha) hits across the match, if any. */
    private Integer alpha;
    /** The competitor's total C-zone (charlie) hits across the match, if any. */
    private Integer charlie;
    /** The competitor's total D-zone (delta) hits across the match, if any. */
    private Integer delta;
    /** The competitor's total required hits not scored (misses) across the match, if any. */
    private Integer misses;
    /** The competitor's total misses that did not attract the usual miss penalty, if any. */
    private Integer noPenaltyMisses;
    /** The competitor's total no-shoot penalty hits across the match, if any. */
    private Integer noShoots;
    /** The competitor's total procedural penalties applied across the match, if any. */
    private Integer proceduralErrors;
    /** The competitor's total additional penalties applied across the match, if any. */
    private Integer additionalPenalties;
    /** Whether the competitor was a visitor at the match, if recorded. */
    private Boolean isVisitor;

    /**
     * Creates a response from a persisted match competitor.
     *
     * <p>
     * The match and competitor identifiers, the competitor's names and the competitor's number come from the match
     * competitor's {@link MatchCompetitor#getMatch() match} and {@link MatchCompetitor#getCompetitor() competitor};
     * every other field is copied from the match competitor itself.
     * </p>
     *
     * @param matchCompetitor the match competitor to convert.
     *                        Must not be null and must have its match and competitor set.
     *                        Those lazily loaded associations are read here, so call this while the persistence
     *                        session is still open.
     */
    public MatchCompetitorResponse(MatchCompetitor matchCompetitor) {
        this.matchCompetitorId = matchCompetitor.getId();
        this.matchId = matchCompetitor.getMatch().getId();
        this.competitorId = matchCompetitor.getCompetitor().getId();

        Competitor competitor = matchCompetitor.getCompetitor();
        // A set keeps each name once, in order, when the nickname matches the first name or is absent
        Set<String> uniqueNames = new LinkedHashSet<>();
        uniqueNames.add(competitor.getFirstName() + " " + competitor.getLastName());
        if (competitor.getNickName() != null) {
            uniqueNames.add(competitor.getNickName() + " " + competitor.getLastName());
        }
        this.competitorNames = new ArrayList<>(uniqueNames);
        this.competitorNumber = matchCompetitor.getCompetitor().getCompetitorNumber();

        this.matchClub = matchCompetitor.getMatchClub();
        this.competitorCategory = matchCompetitor.getCompetitorCategory();
        this.firearmType = matchCompetitor.getFirearmType();
        this.division = matchCompetitor.getDivision();
        this.powerFactor = matchCompetitor.getPowerFactor();

        this.points = matchCompetitor.getPoints();
        this.percentage = matchCompetitor.getPercentage();
        this.percentageOfPossiblePoints = matchCompetitor.getPercentageOfPossiblePoints();
        this.time = matchCompetitor.getTime();

        this.alpha = matchCompetitor.getAlpha();
        this.charlie = matchCompetitor.getCharlie();
        this.delta = matchCompetitor.getDelta();

        this.misses = matchCompetitor.getMisses();
        this.noPenaltyMisses = matchCompetitor.getNoPenaltyMisses();
        this.noShoots = matchCompetitor.getNoShoots();
        this.proceduralErrors = matchCompetitor.getProceduralErrors();
        this.additionalPenalties = matchCompetitor.getAdditionalPenalties();

        this.isVisitor = matchCompetitor.getIsVisitor();
    }
}
