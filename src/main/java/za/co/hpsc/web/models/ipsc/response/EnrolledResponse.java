package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.EnrolledRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledResponse {
    @NotNull
    private Integer memberId;
    private Integer competitorId;

    private Integer matchId;
    private Integer squadId;
    private Integer divisionId;

    private String refNo;
    private Integer tagId;
    private Integer nonTeamCategoryId;

    private Boolean majorPowerFactor;
    private Boolean scoreClassificationId;

    private Boolean isDisqualified;
    private Integer disqualificationRuleId;
    private Boolean stageDisqualification;
    private LocalDateTime disqualifiedDate;
    private String disqualifiedNote;

    public EnrolledResponse(EnrolledRequest enrolledRequest) {
        this.memberId = enrolledRequest.getMemberId();
        this.competitorId = enrolledRequest.getCompetitorId();
        this.matchId = enrolledRequest.getMatchId();
        this.squadId = enrolledRequest.getSquadId();
        this.divisionId = enrolledRequest.getDivisionId();
        this.refNo = enrolledRequest.getRefNo();
        this.tagId = enrolledRequest.getTagId();
        this.nonTeamCategoryId = enrolledRequest.getNonTeamCategoryId();
        this.majorPowerFactor = enrolledRequest.getMajorPowerFactor();
        this.scoreClassificationId = enrolledRequest.getScoreClassificationId();
        this.isDisqualified = enrolledRequest.getIsDisqualified();
        this.disqualificationRuleId = enrolledRequest.getDisqualificationRuleId();
        this.stageDisqualification = enrolledRequest.getStageDisqualification();
        this.disqualifiedDate = enrolledRequest.getDisqualifiedDate();
        this.disqualifiedNote = enrolledRequest.getDisqualifiedNote();
    }
}
