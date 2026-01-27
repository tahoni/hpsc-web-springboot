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

import java.math.BigDecimal;

/**
 * The MatchCompetitor class represents the association between a competitor and a match,
 * along with specific details about the competitor's performance in the match.
 * <p>
 * This class serves as an entity in the persistence layer, linking the {@code Competitor}
 * and {@code Match} entities while storing additional data such as the competitor's
 * division, discipline, power factor, and performance metrics (e.g., match points and percentage).
 * <p>
 * Key attributes include:
 * - A unique identifier for the MatchCompetitor instance.
 * - The associated {@code Competitor} participating in the match.
 * - The associated {@code Match} in which the competitor is competing.
 * - The division, discipline, and power factor applicable to the competitor for the match.
 * - Performance metrics such as match points and match percentage.
 * <p>
 * Instances of this class are uniquely identified by their auto-generated identifier
 * and managed within the persistence layer. The toString method is overridden to provide a
 * concise string representation that includes the match and competitor details.
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

    public String toString() {
        return match.toString() + ": " + competitor.toString();
    }
}
