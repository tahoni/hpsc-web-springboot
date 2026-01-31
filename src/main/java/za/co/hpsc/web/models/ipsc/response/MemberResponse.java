package za.co.hpsc.web.models.ipsc.response;

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
}
