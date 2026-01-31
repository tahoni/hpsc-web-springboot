package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private Integer matchId;
    @JacksonXmlProperty(isAttribute = true)
    @NotNull
    private Integer stageId;
    @JacksonXmlProperty(isAttribute = true)
    @NotNull
    private Integer memberId;

    @JacksonXmlProperty(isAttribute = true)
    private Integer scoreA;
    @JacksonXmlProperty(isAttribute = true)
    private Integer scoreB;
    @JacksonXmlProperty(isAttribute = true)
    private Integer scoreC;
    @JacksonXmlProperty(isAttribute = true)
    private Integer scoreD;

    @JacksonXmlProperty(isAttribute = true)
    private Integer misses;
    @JacksonXmlProperty(isAttribute = true)
    private Integer penalties;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ProcError")
    private Integer procedurals;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ShootTime")
    private BigDecimal time;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Integer deduction;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DedPctg")
    private Integer deductionPercentage;

    @JacksonXmlProperty(isAttribute = true)
    private Integer extraShot;
    @JacksonXmlProperty(isAttribute = true)
    private BigDecimal overTime;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private BigDecimal hitFactor;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private Integer finalScore;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "IsDisq", required = true)
    private Boolean isDisqualified;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "LastModify")
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT)
    private LocalDateTime lastModified;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "NoVerify", required = true)
    private Boolean scoreNotVerified;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Remove", required = true)
    private Boolean removeFromScoring;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "FlagDelete", required = true)
    private Boolean flagForDeletion;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ScoreRequest> row;
    }
}
