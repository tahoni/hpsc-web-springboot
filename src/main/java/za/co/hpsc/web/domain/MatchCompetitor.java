package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.*;
import za.co.hpsc.web.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "match_competitor",
        uniqueConstraints = @UniqueConstraint(columnNames = {"competitor_id", "match_id", "firearm_type"}))
public class MatchCompetitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id", nullable = false)
    private Competitor competitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private IpscMatch match;

    @Convert(converter = ClubIdentifierConverter.class)
    @Column(name = "match_club")
    private ClubIdentifier matchClub;

    @Convert(converter = CompetitorCategoryConverter.class)
    @Column(name = "competitor_category", nullable = false)
    private CompetitorCategory competitorCategory;

    @Convert(converter = FirearmTypeConverter.class)
    @Column(name = "firearm_type")
    private FirearmType firearmType;

    @Convert(converter = DivisionConverter.class)
    @Column(name = "division", nullable = false)
    private Division division;

    @Convert(converter = PowerFactorConverter.class)
    @Column(name = "power_factor")
    private PowerFactor powerFactor;

    @Column(name = "points", precision = 19, scale = 6)
    private BigDecimal points;

    @Column(name = "percentage", precision = 19, scale = 6)
    private BigDecimal percentage;

    @Column(name = "time", precision = 19, scale = 6)
    private BigDecimal time;

    @Column(name = "percentage_of_possible_points", precision = 19, scale = 6)
    private BigDecimal percentageOfPossiblePoints;

    @Column(name = "hit_factor", precision = 19, scale = 6)
    private BigDecimal hitFactor;

    @Column(name = "alpha")
    private Integer alpha;

    @Column(name = "charlie")
    private Integer charlie;

    @Column(name = "delta")
    private Integer delta;

    @Column(name = "misses")
    private Integer misses;

    @Column(name = "no_penalty_misses")
    private Integer noPenaltyMisses;

    @Column(name = "no_shoots")
    private Integer noShoots;

    @Column(name = "procedural_errors")
    private Integer proceduralErrors;

    @Column(name = "additional_penalties")
    private Integer additionalPenalties;

    @Column(name = "overall_ranking", precision = 19, scale = 6)
    private BigDecimal overallRanking;

    @Column(name = "club_ranking", precision = 19, scale = 6)
    private BigDecimal clubRanking;

    @Column(name = "is_visitor")
    private Boolean isVisitor;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
