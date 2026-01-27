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
public class MemberRequest {
    @NotNull
    private Integer memberId;
    private String lastName;
    private String firstName;
    private String comment;

    @NotNull
    private Boolean female;
    @NotNull
    private LocalDateTime dateOfBirth;

    private String icsAlias;
    private String refNo;
    private Boolean inactive;
    @NotNull
    private Boolean isRegisteredForMatch;

    private Integer qualificationId;
    private String scoreClassificationId;

    private Integer defaultTagId;
    private Integer defaultDivisionId;
    private Integer defaultNonTeamCategoryId;

    private String regionId;
    private String address1;
    private String address2;
    private String city;
    private String province;
    private String countryId;
    private String postalCode;

    private String homePhoneNumber;
    private String alternativePhoneNumber;
    private String faxNumber;
    private String email;

    private Boolean printLabel;
}
