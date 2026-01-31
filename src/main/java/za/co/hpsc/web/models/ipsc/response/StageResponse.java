package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageResponse {
    @NotNull
    private Integer matchId;
    @NotNull
    private Integer stageId;
    private String stageName;
    private String description;

    private Integer firearmTypeId;
    private Integer scoreClassificationId;

    private Integer targetPaper;
    private Integer targetPopper;
    private Integer targetPlates;
    private Integer targetDisappear;
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    private String startPosition;
    private String startOn;
}
