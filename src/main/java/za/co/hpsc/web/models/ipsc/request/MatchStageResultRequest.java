package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchStageResultRequest {
    private Long matchId;
    private Integer stageNumber;
    private String name;
    private BigDecimal stagePercentage;
    private BigDecimal stagePoints;
    private Integer points;
    private BigDecimal hitFactor;
    private BigDecimal time;
    private String division;
    private String club;
    private String categories;
    private String powerFactor;
    private String membershipNumber;
    private Integer alpha;
    private Integer charlie;
    private Integer delta;
    private Integer misses;
    private Integer noPenaltyMisses;
    private Integer noShoots;
    private Integer proceduralErrors;
    private Integer additionalPenalties;
}
