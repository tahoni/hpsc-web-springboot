package za.co.hpsc.web.models.match.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class StageRequestForXml {
    private Integer matchId;
    private Integer stageId;
    private String stageName;
    @JsonProperty(value = "Descriptn")
    private String description;

    private Integer courseId;
    private String location;
    private Integer firearmTypeId;
    private Integer scoreClassificationId;

    @JsonProperty(value = "TrgtTypeId")
    private Integer targetClassificationId;
    @JsonProperty(value = "IcsStageId")
    private Integer standardStageSetupId;

    @JsonProperty(value = "TrgtPaper")
    private Integer targetPaper;
    @JsonProperty(value = "TrgtPopper")
    private Integer targetPopper;
    @JsonProperty(value = "TrgtPlates")
    private Integer targetPlates;
    @JsonProperty(value = "TrgtVanish")
    private Integer targetDisappear;
    @JsonProperty(value = "TrgtPenalty")
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    @JsonProperty(value = "StartPos")
    private String startPosition;
    private String startOn;

    @JsonProperty(value = "StringCnt")
    private Integer stringsOfFire;

    @JsonProperty(required = true)
    private Boolean reportOn;
    @JsonProperty(value = "Remove", required = true)
    private Boolean removeFromScoring;
}
