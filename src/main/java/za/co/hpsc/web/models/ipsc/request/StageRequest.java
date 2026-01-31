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

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private Integer matchId;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private Integer stageId;
    @JacksonXmlProperty(isAttribute = true)
    private String stageName;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Descriptn")
    private String description;

    @JacksonXmlProperty(isAttribute = true)
    private Integer courseId;
    @JacksonXmlProperty(isAttribute = true)
    private String location;
    @JacksonXmlProperty(isAttribute = true)
    private Integer firearmTypeId;
    @JacksonXmlProperty(isAttribute = true)
    private Integer scoreClassificationId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtTypeId")
    private Integer targetClassificationId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "IcsStageId")
    private Integer standardStageSetupId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtPaper")
    private Integer targetPaper;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtPopper")
    private Integer targetPopper;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtPlates")
    private Integer targetPlates;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtVanish")
    private Integer targetDisappear;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "TrgtPenalty")
    private Integer targetPenalty;

    @JacksonXmlProperty(isAttribute = true)
    private Integer minRounds;
    @JacksonXmlProperty(isAttribute = true)
    private Integer maxPoints;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StartPos")
    private String startPosition;
    @JacksonXmlProperty(isAttribute = true)
    private String startOn;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StringCnt")
    private Integer stringsOfFire;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Boolean reportOn;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Remove", required = true)
    private Boolean removeFromScoring;


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
