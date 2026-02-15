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
    private ClubIdentifier clubIdentifier;
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

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    /**
     * Initialises the current {@code MatchStageCompetitor} entity with data from a DTO
     * and associated entities.
     *
     * <p>
     * This method sets the relevant fields in the entity, including association with a match,
     * competitor information, performance metrics, and date attributes.
     * </p>
     *
     * @param matchCompetitorDto the DTO containing data needed to populate the entity fields.
     * @param matchEntity        the associated match entity.
     * @param competitorEntity   the associated competitor entity.
     */
    public void init(MatchCompetitorDto matchCompetitorDto, IpscMatch matchEntity, Competitor competitorEntity) {
        // Initialises the competitor details
        this.match = matchEntity;
        this.competitor = competitorEntity;

        // Initialises the competitor attributes
        this.clubIdentifier = matchCompetitorDto.getClubIdentifier();
        this.competitorCategory = matchCompetitorDto.getCompetitorCategory();
        this.firearmType = matchCompetitorDto.getFirearmType();
        this.division = matchCompetitorDto.getDivision();
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
