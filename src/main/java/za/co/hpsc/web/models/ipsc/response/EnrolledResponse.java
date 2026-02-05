package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.EnrolledRequest;

import java.time.LocalDateTime;

/**
 * Represents a response model for an enrolled member, typically used to transfer data
 * within the system regarding a member's enrollment status and associated attributes.
 *
 * <p>
 * This class encapsulates details such as member information, match details, category
 * information, disqualification status, and other relevant fields. It provides
 * functionality to populate its fields directly from an {@link EnrolledRequest} object.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledResponse {
    @NotNull
    private Integer memberId = 0;
    private Integer competitorId = 0;

    private Integer matchId = 0;
    private Integer squadId = 0;
    private Integer divisionId = 0;

    private String refNo = "";
    private Integer tagId = 0;
    private Integer nonTeamCategoryId = 0;

    private Boolean majorPowerFactor = false;
    private Boolean scoreClassificationId = false;

    private Boolean isDisqualified = false;
    private Integer disqualificationRuleId = 0;
    private Boolean stageDisqualification = false;
    private LocalDateTime disqualifiedDate = LocalDateTime.now();
    private String disqualifiedNote = "";

    /**
     * Constructs a new {@code EnrolledResponse} object by initialising its fields using
     * the values from a given {@link EnrolledRequest} object.
     *
     * @param enrolledRequest the {@link EnrolledRequest} object containing data to initialise
     *                        the {@code EnrolledResponse} instance.
     */
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
