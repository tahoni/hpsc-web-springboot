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
@Table(name = "match_stage_competitor")
public class MatchStageCompetitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id", nullable = false)
    private Competitor competitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_stage_id", nullable = false)
    private IpscMatchStage matchStage;

    @Column(name = "match_club")
    private String matchClub;

    @Column(name = "firearm_type")
    private String firearmType;

    private String division;

    @Column(name = "power_factor")
    private String powerFactor;

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    @Column(name = "score_c")
    private Integer scoreC;

    @Column(name = "score_d")
    private Integer scoreD;

    private Integer points;
    private Integer misses;
    private Integer penalties;
    private Integer procedurals;

    @Column(name = "has_deduction")
    private Boolean hasDeduction;

    @Column(name = "deduction_percentage", precision = 19, scale = 6)
    private BigDecimal deductionPercentage;

    @Column(name = "time", precision = 19, scale = 6)
    private BigDecimal time;

    @Column(name = "hit_factor", precision = 19, scale = 6)
    private BigDecimal hitFactor;

    @Column(name = "stage_points", precision = 19, scale = 6)
    private BigDecimal stagePoints;

    @Column(name = "stage_percentage", precision = 19, scale = 6)
    private BigDecimal stagePercentage;

    @Column(name = "stage_ranking", precision = 19, scale = 6)
    private BigDecimal stageRanking;

    @Column(name = "is_disqualified")
    private Boolean isDisqualified;

    @Column(name = "competitor_category")
    private String competitorCategory;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}