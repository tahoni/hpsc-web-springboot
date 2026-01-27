package za.co.hpsc.web.models.match.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class MemberRequestForXml {
    @JsonProperty(required = true)
    private Integer memberId;
    private String lastName;
    private String firstName;
    @JsonProperty(value = "Init")
    private String comment;

    @JsonProperty(required = true)
    private Boolean female;
    @JsonProperty(value = "DOB")
    private LocalDateTime dateOfBirth;

    private String icsAlias;
    private String refNo;
    @JsonProperty(value = "InActive", required = true)
    private Boolean inactive;
    @JsonProperty(value = "Register", required = true)
    private Boolean isRegisteredForMatch;

    @JsonProperty(value = "QualId")
    private Integer qualificationId;
    @JsonProperty(value = "ClassId")
    private String scoreClassificationId;

    @JsonProperty(value = "DfltTagId")
    private Integer defaultTagId;
    @JsonProperty(value = "DfltDivId")
    private Integer defaultDivisionId;
    @JsonProperty(value = "DfltCatId")
    private Integer defaultNonTeamCategoryId;

    private String regionId;
    private String address1;
    private String address2;
    private String city;
    private String province;
    private String countryId;
    @JsonProperty(value = "PostCode")
    private String postalCode;

    @JsonProperty(value = "PhoneHome")
    private String homePhoneNumber;
    @JsonProperty(value = "PhoneAlt")
    private String alternativePhoneNumber;
    @JsonProperty(value = "PhoneFax")
    private String faxNumber;
    private String email;

    @JsonProperty(required = true)
    private Boolean printLabel;
}
