package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shooter_log_entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shooter_log_id", "match_competitor_id"}))
public class ShooterLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shooter_log_id", nullable = false)
    private ShooterLog shooterLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_competitor_id", nullable = false)
    private MatchCompetitor matchCompetitor;

    @Column(name = "rank_in_log")
    private Integer rankInLog;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
