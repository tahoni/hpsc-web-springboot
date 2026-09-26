package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.ClubIdentifierConverter;
import za.co.hpsc.web.converters.CompetitorCategoryConverter;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.PowerFactorConverter;
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
    @Column(name = "competitor_category")
    private CompetitorCategory competitorCategory;

    @Convert(converter = FirearmTypeConverter.class)
    @Column(name = "firearm_type", nullable = false)
    private FirearmType firearmType;

    private Division division;

    @Convert(converter = PowerFactorConverter.class)
    @Column(name = "power_factor")
    private PowerFactor powerFactor;

    @Column(name = "match_points", precision = 19, scale = 6)
    private BigDecimal matchPoints;

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
