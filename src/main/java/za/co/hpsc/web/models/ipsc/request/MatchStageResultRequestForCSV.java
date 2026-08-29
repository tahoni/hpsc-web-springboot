package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MatchStageResultRequestForCSV {
    private Long matchId;
    private Integer stageNumber;
    private String name;
    @JsonProperty("%")
    private BigDecimal stagePercentage;
    @JsonProperty("Stg Pts")
    private BigDecimal stagePoints;
    @JsonProperty("Pts")
    private Integer points;
    @JsonProperty("HF")
    private BigDecimal hitFactor;
    private BigDecimal time;
    @JsonProperty("Div")
    private String division;
    @JsonProperty("Class")
    private String club;
    @JsonProperty("Cats")
    private String categories;
    @JsonProperty("PF")
    private String powerFactor;
    @JsonProperty("Mem#")
    private String membershipNumber;
    @JsonProperty("A")
    private Integer alpha;
    @JsonProperty("C")
    private Integer charlie;
    @JsonProperty("D")
    private Integer delta;
    @JsonProperty("M")
    private Integer misses;
    @JsonProperty("NPM")
    private Integer noPenaltyMisses;
    @JsonProperty("NS")
    private Integer noShoots;
    @JsonProperty("Proc")
    private Integer proceduralErrors;
    @JsonProperty("Apen")
    private Integer additionalPenalties;
}
