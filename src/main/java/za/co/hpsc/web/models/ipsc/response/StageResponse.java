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
    private Integer matchId;
    @NotNull
    private Integer stageId;
    private String stageName;
    private String description;

    private Integer firearmId;
    private Integer scoreClassificationId;

    private Integer targetPaper;
    private Integer targetPopper;
    private Integer targetPlates;
    private Integer targetDisappear;
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    private Integer startPosition;
    private Integer startOn;

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
