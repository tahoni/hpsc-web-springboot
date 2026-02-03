package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the relationship between a competitor and a specific match,
 * along with specific details about the competitor's performance in the match.
 *
 * <p>
 * The {@code MatchCompetitor} class serves as an entity in the persistence layer, linking the
 * {@link Competitor} and {@link Match} entities while storing additional data such as the competitor's
 * division, discipline, power factor, and performance metrics (e.g., match points and percentage).
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to provide a concise string representation
 * that includes the match and competitor details.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MatchCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id")
    private Match match;

    @Enumerated(EnumType.STRING)
    private Division division;
    @Enumerated(EnumType.STRING)
    private Discipline discipline;
    @Enumerated(EnumType.STRING)
    private PowerFactor powerFactor;

    private BigDecimal matchPoints;
    private BigDecimal matchPercentage;

    private LocalDateTime dateUpdated;

    /**
     * Constructs a new instance of the MatchCompetitor class, linking a competitor
     * to a specific match.
     *
     * @param competitor the competitor participating in the match. Must not be null
     * @param match      the match in which the competitor is participating. Must not be null
     */
    public MatchCompetitor(@NotNull Competitor competitor, @NotNull Match match) {
        this.competitor = competitor;
        this.match = match;
    }

    public void init(ScoreResponse scoreResponse) {
        this.matchPoints = BigDecimal.valueOf(scoreResponse.getFinalScore());
        this.dateUpdated = scoreResponse.getLastModified();

        // TODO: populate category, division, discipline, power factor
    }

    public String toString() {
        return match.toString() + ": " + competitor.toString();
    }
}
