package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @JacksonXmlText
    String value;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Integer memberId;
    @JacksonXmlProperty(isAttribute = true)
    private String lastName;
    @JacksonXmlProperty(isAttribute = true)
    private String firstName;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Init")
    private String comment;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Boolean female;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DOB")
    private LocalDateTime dateOfBirth;

    @JacksonXmlProperty(isAttribute = true)
    private String icsAlias;
    @JacksonXmlProperty(isAttribute = true)
    private String refNo;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "InActive", required = true)
    private Boolean inactive;
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Register", required = true)
    private Boolean isRegisteredForMatch;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "QualId")
    private Integer qualificationId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ClassId")
    private String scoreClassificationId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DfltTagId")
    private Integer defaultTagId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DfltDivId")
    private Integer defaultDivisionId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "DfltCatId")
    private Integer defaultNonTeamCategoryId;

    @JacksonXmlProperty(isAttribute = true)
    private String regionId;
    @JacksonXmlProperty(isAttribute = true)
    private String address1;
    @JacksonXmlProperty(isAttribute = true)
    private String address2;
    @JacksonXmlProperty(isAttribute = true)
    private String city;
    @JacksonXmlProperty(isAttribute = true)
    private String province;
    @JacksonXmlProperty(isAttribute = true)
    private String countryId;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PostCode")
    private String postalCode;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PhoneHome")
    private String homePhoneNumber;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PhoneAlt")
    private String alternativePhoneNumber;
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PhoneFax")
    private String faxNumber;
    @JacksonXmlProperty(isAttribute = true)
    private String email;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(required = true)
    private Boolean printLabel;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<MemberRequest> row;
    }
}
