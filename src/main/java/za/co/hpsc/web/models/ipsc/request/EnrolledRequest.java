package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledRequest {
    @NotNull
    private Integer memberId;
    private Integer competitorId;

    private Integer matchId;
    private Integer squadId;
    private Integer teamId;
    private Integer divisionId;

    private String refNo;
    private Integer tagId;
    private Integer nonTeamCategoryId;

    @NotNull
    private Boolean majorPowerFactor;
    private Boolean scoreClassificationId;

    private Boolean isDisqualified;
    private Integer disqualificationRuleId;
    private Boolean stageDisqualification;
    private LocalDateTime disqualifiedDate;
    private String disqualifiedNote;
}
