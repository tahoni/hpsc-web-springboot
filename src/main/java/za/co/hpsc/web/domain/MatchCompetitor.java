package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "match_competitor")
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

    @Column(name = "match_club")
    private String matchClub;

    @Column(name = "firearm_type")
    private String firearmType;

    private String division;

    @Column(name = "power_factor")
    private String powerFactor;

    @Column(name = "match_points", precision = 19, scale = 6)
    private BigDecimal matchPoints;

    @Column(name = "match_ranking", precision = 19, scale = 6)
    private BigDecimal matchRanking;

    @Column(name = "competitor_category")
    private String competitorCategory;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
