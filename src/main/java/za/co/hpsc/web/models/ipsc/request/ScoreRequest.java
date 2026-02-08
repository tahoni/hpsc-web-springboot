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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a score request within the system.
 *
 * <p>
 * The class maps to the `SCORE.XML` content in the `WinMSS.cab` file.
 * It stores meta-data about members in the system, namely their scores for each stage
 * of a match, including detailed and summarised performance data.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchId", required = true)
    private Integer matchId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StageId", required = true)
    private Integer stageId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MemberId", required = true)
    private Integer memberId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ScoreA")
    private Integer scoreA;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ScoreB")
    private Integer scoreB;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ScoreC")
    private Integer scoreC;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ScoreD")
    private Integer scoreD;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Misses")
    private Integer misses;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Penalties")
    private Integer penalties;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ProcError")
    private Integer procedurals;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ShootTime")
    private String time;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Deduction", required = true)
    private Boolean deduction;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DedPctg")
    private String deductionPercentage;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ExtraShot")
    private Integer extraShot;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "OverTime")
    private String overTime;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "HitFactor", required = true)
    private String hitFactor;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "FinalScore", required = true)
    private Integer finalScore;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "IsDisq")
    private Boolean isDisqualified;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "LastModify")
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT)
    private LocalDateTime lastModified;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "NoVerify")
    private Boolean scoreNotVerified;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Remove")
    private Boolean removeFromScoring;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "FlagDelete")
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
