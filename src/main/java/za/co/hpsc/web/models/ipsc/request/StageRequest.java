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

/**
 * Represents a stage request within the system.
 *
 * <p>
 * The class maps to the `STAGE.XML` content in the `WinMSS.cab` file.
 * It stores extensive data about all stages of a match.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageRequest {
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
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StageName")
    private String stageName;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Descriptn")
    private String description;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CourseId")
    private Integer courseId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Location")
    private String location;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "FirearmId")
    private Integer firearmId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ScoringId")
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
    @JsonProperty(value = "TrgtPenlty")
    private Integer targetPenalty;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MinRounds")
    private Integer minRounds;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "MaxPoints")
    private Integer maxPoints;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StartPos")
    private Integer startPosition;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StartOn")
    private Integer startOn;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "StringCnt")
    private Integer stringsOfFire;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ReportOn")
    private Boolean reportOn;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Remove")
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
