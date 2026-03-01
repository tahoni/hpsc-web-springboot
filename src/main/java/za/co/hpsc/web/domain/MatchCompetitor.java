package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.dto.MatchCompetitorDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the relationship between a competitor and a specific match,
 * along with specific details about the competitor's performance in the match.
 *
 * <p>
 * The {@code MatchCompetitor} class serves as an entity in the persistence layer, linking the
 * {@link Competitor} and {@link IpscMatch} entities while storing additional data such as the competitor's
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id")
    private IpscMatch match;

    @Enumerated(EnumType.STRING)
    private ClubIdentifier matchClub;
    @Enumerated(EnumType.STRING)
    private FirearmType firearmType;
    @Enumerated(EnumType.STRING)
    private Division division;
    @Enumerated(EnumType.STRING)
    private PowerFactor powerFactor;

    private BigDecimal matchPoints;
    private BigDecimal matchRanking;

    @Enumerated(EnumType.STRING)
    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    /**
     * Initialises the attributes of the MatchCompetitor instance using data from the
     * provided MatchCompetitorDto.
     *
     * @param matchCompetitorDto an instance of MatchCompetitorDto containing the
     *                           competitor's category, firearm type, division,
     *                           power factor, match points, and match ranking to be assigned to
     *                           this MatchCompetitor.
     */
    public void init(MatchCompetitorDto matchCompetitorDto) {
        // Initialises the competitor attributes
        this.competitorCategory = matchCompetitorDto.getCompetitorCategory();
        this.firearmType = matchCompetitorDto.getFirearmType();
        this.division = matchCompetitorDto.getDivision();
        this.powerFactor = matchCompetitorDto.getPowerFactor();

        // Initialises the match scoring attributes
        this.matchPoints = matchCompetitorDto.getMatchPoints();
        this.matchRanking = matchCompetitorDto.getMatchRanking();
    }

    public String toString() {
        return this.match.toString() + ": " + this.competitor.toString();
    }

    @PrePersist
    void onInsert() {
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
