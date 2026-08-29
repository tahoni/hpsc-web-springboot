package za.co.hpsc.web.models.ipsc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchOverallResultRequest {
    private Long matchId;
    private String competitorName;
    private BigDecimal percentage;
    private BigDecimal points;
    private BigDecimal percentageOfPossiblePoints;
    private Division division;
    private List<CompetitorCategory> competitorCategories;
    private ClubIdentifier clubIdentifier;
    private PowerFactor powerFactor;
    private String membershipNumber;
    private Integer alpha;
    private Integer charlie;
    private Integer delta;
    private Integer noShoots;
    private Integer misses;
    private Integer noPenaltyMisses;
    private Integer proceduralErrors;
    private Integer additionalPenalties;
}
