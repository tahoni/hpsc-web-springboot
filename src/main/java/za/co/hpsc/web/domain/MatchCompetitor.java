package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubReference;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.dto.MatchCompetitorDto;

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
    private ClubReference club;
    @Enumerated(EnumType.STRING)
    private FirearmType firearmType;
    @Enumerated(EnumType.STRING)
    private Discipline discipline;
    @Enumerated(EnumType.STRING)
    private PowerFactor powerFactor;

    private BigDecimal matchPoints;
    private BigDecimal matchRanking;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    // TODO: Javadoc
    public void init(MatchCompetitorDto matchCompetitorDto, Match matchEntity, Competitor competitorEntity) {
        // Initialises the competitor details
        this.id = matchCompetitorDto.getId();
        this.match = matchEntity;
        this.competitor = competitorEntity;

        // Initialises the competitor attributes
        this.club = matchCompetitorDto.getClub();
        this.firearmType = matchCompetitorDto.getFirearmType();
        this.discipline = matchCompetitorDto.getDiscipline();
        this.powerFactor = matchCompetitorDto.getPowerFactor();

        // Initialises the match scoring attributes
        this.matchPoints = matchCompetitorDto.getMatchPoints();
        this.matchRanking = matchCompetitorDto.getMatchRanking();

        // Initialises the date fields
        this.dateCreated = matchCompetitorDto.getDateCreated();
        this.dateUpdated = matchCompetitorDto.getDateUpdated();
        this.dateEdited = matchCompetitorDto.getDateEdited();
    }

    public String toString() {
        return this.match.toString() + ": " + this.competitor.toString();
    }
}
