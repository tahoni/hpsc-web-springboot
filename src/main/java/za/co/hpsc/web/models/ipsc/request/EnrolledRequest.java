package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a classification request within the system.
 *
 * <p>
 * The class maps to the `ENROLLED.XML` content in the `WinMSS.cab` file.
 * It stores meta-data about members in the system, namely the members enrolled in a match.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MemberId", required = true)
    private Integer memberId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CompId")
    private Integer competitorId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MatchId")
    private Integer matchId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "SquadId")
    private Integer squadId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TeamId")
    private Integer teamId;
    // 29
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DivId")
    private Integer divisionId;

    // Club
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "RefNo")
    private String refNo;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TagId")
    private Integer tagId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CatId")
    private Integer competitorCategoryId;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MajorPF", required = true)
    private Boolean majorPowerFactor;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Classified", required = true)
    private Boolean scoreClassificationId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "IsDisq", required = true)
    private Boolean isDisqualified;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DisqRuleId")
    private Integer disqualificationRuleId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StageDisq")
    private Boolean stageDisqualification;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DisqDt")
    private LocalDateTime disqualifiedDate;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DisqMemo")
    private String disqualifiedNote;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<EnrolledRequest> row;
    }
}
