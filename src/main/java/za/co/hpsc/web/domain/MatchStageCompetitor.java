package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MatchStageCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @ManyToOne
    @JoinColumn(name = "match_stage_id")
    private MatchStage matchStage;
}
