package za.co.hpsc.web.models.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class ScoreRequestForXml {
    private Integer matchId;
    private Integer stageId;
    private Integer memberId;

    private Integer scoreA;
    private Integer scoreB;
    private Integer scoreC;
    private Integer scoreD;

    private Integer misses;
    private Integer penalties;
    @JsonProperty(value = "ProcError")
    private Integer procedurals;
    @JsonProperty(value = "ShootTime")
    private BigDecimal time;

    @JsonProperty(required = true)
    private Integer deduction;
    @JsonProperty(value = "DedPctg")
    private Integer deductionPercentage;

    private Integer extraShot;
    private BigDecimal overTime;

    private BigDecimal hitFactor;
    private Integer finalScore;

    @JsonProperty(value = "IsDisq", required = true)
    private Boolean isDisqualified;

    @JsonProperty(value = "LastModify")
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT)
    private LocalDateTime lastModified;

    @JsonProperty(value = "NoVerify", required = true)
    private Boolean scoreNotVerified;
    @JsonProperty(value = "Remove", required = true)
    private Boolean removeFromScoring;
    @JsonProperty(value = "FlagDelete", required = true)
    private boolean flagForDeletion;
}
