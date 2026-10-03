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
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "match_competitor_category", joinColumns = @JoinColumn(name = "match_competitor_id"))
    @Convert(converter = CompetitorCategoryConverter.class)
    @Column(name = "competitor_category", nullable = false)
    private List<CompetitorCategory> competitorCategories = new ArrayList<>();

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
