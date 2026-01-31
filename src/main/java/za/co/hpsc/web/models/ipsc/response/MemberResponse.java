package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.MemberRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    @NotNull
    private Integer memberId;
    private String lastName;
    private String firstName;
    private String comment;

    private Boolean female;
    private LocalDateTime dateOfBirth;

    private String icsAlias;
    private String refNo;
    @NotNull
    private Boolean isRegisteredForMatch;

    private Integer qualificationId;
    private String scoreClassificationId;

    private String email;

    public MemberResponse(MemberRequest memberRequest) {
        this.memberId = memberRequest.getMemberId();
        this.lastName = memberRequest.getLastName();
        this.firstName = memberRequest.getFirstName();
        this.comment = memberRequest.getComment();
        this.female = memberRequest.getFemale();
        this.dateOfBirth = memberRequest.getDateOfBirth();
        this.icsAlias = memberRequest.getIcsAlias();
        this.refNo = memberRequest.getRefNo();
        this.isRegisteredForMatch = memberRequest.getIsRegisteredForMatch();
        this.qualificationId = memberRequest.getQualificationId();
        this.scoreClassificationId = memberRequest.getScoreClassificationId();
        this.email = memberRequest.getEmail();
    }
}
