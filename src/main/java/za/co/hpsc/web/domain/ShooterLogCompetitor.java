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
@Table(name = "shooter_log_competitor",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shooter_log_id", "match_competitor_id"}))
public class ShooterLogCompetitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shooter_log_id", nullable = false)
    private ShooterLog shooterLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_competitor_id", nullable = false)
    private MatchCompetitor matchCompetitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private IpscMatch match;

    @Column(name = "rank_in_log")
    private Integer rankInLog;

    @Column(name = "points", precision = 19, scale = 6)
    private BigDecimal points;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
