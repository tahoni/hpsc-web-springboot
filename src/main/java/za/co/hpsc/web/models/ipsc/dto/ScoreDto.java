package za.co.hpsc.web.models.ipsc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private transient Integer stageId;
    private transient Integer memberId;

    private Integer scoreA;
    private Integer scoreB;
    private Integer scoreC;
    private Integer scoreD;

    private Integer misses;
    private Integer penalties;
    private Integer procedurals;
    private String time;

    private Boolean deduction;
    private String deductionPercentage;

    private Integer extraShot;
    private String overTime;

    private String hitFactor;
    private Integer finalScore;

    private Boolean isDisqualified;

    private LocalDateTime lastModified;
}
