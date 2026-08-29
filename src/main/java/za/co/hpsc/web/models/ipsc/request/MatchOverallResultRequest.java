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
public class MatchOverallResultRequest {
    private Long matchId;
    private String name;
    private BigDecimal percentage;
    private BigDecimal points;
    private BigDecimal time;
    private BigDecimal percentageOfPossiblePoints;
    private BigDecimal hitFactor;
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
