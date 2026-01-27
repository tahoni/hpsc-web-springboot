package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageRequest {
    @NotNull
    private Integer matchId;
    @NotNull
    private Integer stageId;
    private String stageName;
    private String description;

    private Integer courseId;
    private String location;
    private Integer firearmTypeId;
    private Integer scoreClassificationId;

    private Integer targetClassificationId;
    private Integer standardStageSetupId;

    private Integer targetPaper;
    private Integer targetPopper;
    private Integer targetPlates;
    private Integer targetDisappear;
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    private String startPosition;
    private String startOn;

    private Integer stringsOfFire;

    private Boolean reportOn;
    private Boolean removeFromScoring;
}
