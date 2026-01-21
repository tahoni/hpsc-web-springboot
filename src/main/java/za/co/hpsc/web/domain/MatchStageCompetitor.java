package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class MatchStageCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_competitor_id")
    private MatchCompetitor matchCompetitor;
    @ManyToOne
    @JoinColumn(name = "match_stage_id")
    private MatchStage matchStage;

    private Integer points;
    private Integer penalties;
    private BigDecimal time;
    private BigDecimal hitFactor;
    private BigDecimal stagePoints;
    private BigDecimal stagePercentage;
}
