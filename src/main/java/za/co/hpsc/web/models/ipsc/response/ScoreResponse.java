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
    private Integer matchId = 0;
    @NotNull
    private Integer stageId = 0;
    @NotNull
    private Integer memberId = 0;

    private Integer scoreA = 0;
    private Integer scoreB = 0;
    private Integer scoreC = 0;
    private Integer scoreD = 0;

    private Integer misses = 0;
    private Integer penalties = 0;
    private Integer procedurals = 0;
    private BigDecimal time = BigDecimal.ZERO;

    private Boolean deduction = false;
    private Integer deductionPercentage = 0;

    private Integer extraShot = 0;
    private BigDecimal overTime = BigDecimal.ZERO;

    private BigDecimal hitFactor = BigDecimal.ZERO;
    private Integer finalScore = 0;

    private Boolean isDisqualified = false;

    private LocalDateTime lastModified = LocalDateTime.now();

    /**
     * Constructs a new {@code ScoreResponse} object by initialising its fields using the values
     * from a given {@link ScoreRequest} object.
     *
     * @param scoreRequest the {@link ScoreRequest}object containing data to initialise
     *                     the {@code ScoreResponse} instance.
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
