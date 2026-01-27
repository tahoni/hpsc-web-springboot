package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {
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
    @NotNull
    private BigDecimal time;

    private Integer deduction;
    private Integer deductionPercentage;

    private Integer extraShot;
    private BigDecimal overTime;

    @NotNull
    private BigDecimal hitFactor;
    @NotNull
    private Integer finalScore;

    private Boolean isDisqualified;

    private LocalDateTime lastModified;

    private Boolean scoreNotVerified;
    private Boolean removeFromScoring;
    private boolean flagForDeletion;
}
