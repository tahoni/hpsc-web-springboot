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
    private Integer memberId;
    private Integer competitorId;

    private Integer matchId;
    private Integer squadId;
    private Integer divisionId;

    private String refNo;
    private Integer tagId;
    private Integer competitorCategoryId;

    private Boolean majorPowerFactor;
    private Boolean scoreClassificationId;

    private Boolean isDisqualified;
    private Integer disqualificationRuleId;
    private Boolean stageDisqualification;
    private LocalDateTime disqualifiedDate;
    private String disqualifiedNote;

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
        this.competitorCategoryId = enrolledRequest.getCompetitorCategoryId();
        this.majorPowerFactor = enrolledRequest.getMajorPowerFactor();
        this.scoreClassificationId = enrolledRequest.getScoreClassificationId();
        this.isDisqualified = enrolledRequest.getIsDisqualified();
        this.disqualificationRuleId = enrolledRequest.getDisqualificationRuleId();
        this.stageDisqualification = enrolledRequest.getStageDisqualification();
        this.disqualifiedDate = enrolledRequest.getDisqualifiedDate();
        this.disqualifiedNote = enrolledRequest.getDisqualifiedNote();
    }
}
