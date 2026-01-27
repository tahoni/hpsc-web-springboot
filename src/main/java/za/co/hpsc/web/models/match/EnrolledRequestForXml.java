package za.co.hpsc.web.models.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class EnrolledRequestForXml {
    private Integer memberId;
    @JsonProperty(value = "CompId")
    private Integer competitorId;

    private Integer matchId;
    private Integer squadId;
    private Integer teamId;
    private Integer divisionId;

    private String refNo;
    private Integer tagId;
    @JsonProperty(value = "CatId")
    private Integer nonTeamCategoryId;

    @JsonProperty(value = "MajorPF", required = true)
    private Boolean majorPowerFactor;
    @JsonProperty(value = "Classified", required = true)
    private Boolean scoreClassificationId;

    @JsonProperty(value = "IsDisq", required = true)
    private Boolean isDisqualified;
    @JsonProperty(value = "DisqRuleId")
    private Integer disqualificationRuleId;
    @JsonProperty(value = "StageDisq")
    private Boolean stageDisqualification;
    @JsonProperty(value = "DisqDt")
    private LocalDateTime disqualifiedDate;
    @JsonProperty(value = "DisqMemo")
    private String disqualifiedNote;
}
