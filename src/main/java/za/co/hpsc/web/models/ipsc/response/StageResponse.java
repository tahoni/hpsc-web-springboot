package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.StageRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageResponse {
    @NotNull
    private Integer matchId = 0;
    @NotNull
    private Integer stageId = 0;
    private String stageName = "";
    private String description = "";

    private Integer firearmId = 0;
    private Integer scoreClassificationId = 0;

    private Integer targetPaper = 0;
    private Integer targetPopper = 0;
    private Integer targetPlates = 0;
    private Integer targetDisappear = 0;
    private Integer targetPenalty = 0;

    private Integer minRounds = 0;
    private Integer maxPoints = 0;

    private Integer startPosition = 0;
    private Integer startOn = 0;

    /**
     * Initializes response from request; copies stage properties
     */
    public StageResponse(StageRequest stageRequest) {
        this.matchId = stageRequest.getMatchId();
        this.stageId = stageRequest.getStageId();
        this.stageName = stageRequest.getStageName();
        this.description = stageRequest.getDescription();
        this.firearmId = stageRequest.getFirearmId();
        this.scoreClassificationId = stageRequest.getScoreClassificationId();
        this.targetPaper = stageRequest.getTargetPaper();
        this.targetPopper = stageRequest.getTargetPopper();
        this.targetPlates = stageRequest.getTargetPlates();
        this.targetDisappear = stageRequest.getTargetDisappear();
        this.targetPenalty = stageRequest.getTargetPenalty();
        this.minRounds = stageRequest.getMinRounds();
        this.maxPoints = stageRequest.getMaxPoints();
        this.startPosition = stageRequest.getStartPosition();
        this.startOn = stageRequest.getStartOn();
    }
}
