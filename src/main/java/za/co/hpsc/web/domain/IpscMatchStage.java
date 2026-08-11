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
@Table(name = "ipsc_match_stage")
public class IpscMatchStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private IpscMatch match;

    @Column(name = "stage_number", nullable = false)
    private Integer stageNumber;

    @Column(name = "stage_name")
    private String stageName;

    @Column(name = "range_number")
    private Integer rangeNumber;

    @Column(name = "target_paper")
    private Integer targetPaper;

    @Column(name = "target_popper")
    private Integer targetPopper;

    @Column(name = "target_plates")
    private Integer targetPlates;

    @Column(name = "target_disappear")
    private Integer targetDisappear;

    @Column(name = "target_penalty")
    private Integer targetPenalty;

    @Column(name = "min_rounds")
    private Integer minRounds;

    @Column(name = "max_points")
    private Integer maxPoints;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
