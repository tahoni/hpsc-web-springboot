package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.ScoreRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponse {
    @NotNull
    private Integer matchId;
    @NotNull
    private Integer stageId;
    @NotNull
    private Integer memberId;

    private Integer scoreA;
    private Integer scoreB;
    private Integer scoreC;
    private Integer scoreD;

    private Integer misses;
    private Integer penalties;
    private Integer procedurals;
    private BigDecimal time;

    private Boolean deduction;
    private Integer deductionPercentage;

    private Integer extraShot;
    private BigDecimal overTime;

    private BigDecimal hitFactor;
    private Integer finalScore;

    private Boolean isDisqualified;

    private LocalDateTime lastModified;

    /**
     * Initializes response from request; copies all fields
     */
    public ScoreResponse(ScoreRequest scoreRequest) {
        this.matchId = scoreRequest.getMatchId();
        this.stageId = scoreRequest.getStageId();
        this.memberId = scoreRequest.getMemberId();
        this.scoreA = scoreRequest.getScoreA();
        this.scoreB = scoreRequest.getScoreB();
        this.scoreC = scoreRequest.getScoreC();
        this.scoreD = scoreRequest.getScoreD();
        this.misses = scoreRequest.getMisses();
        this.penalties = scoreRequest.getPenalties();
        this.procedurals = scoreRequest.getProcedurals();
        this.time = scoreRequest.getTime();
        this.deduction = scoreRequest.getDeduction();
        this.deductionPercentage = scoreRequest.getDeductionPercentage();
        this.extraShot = scoreRequest.getExtraShot();
        this.overTime = scoreRequest.getOverTime();
        this.hitFactor = scoreRequest.getHitFactor();
        this.finalScore = scoreRequest.getFinalScore();
        this.isDisqualified = scoreRequest.getIsDisqualified();
        this.lastModified = scoreRequest.getLastModified();
    }
}
