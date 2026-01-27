package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class ClubRequestForXml {
    private Integer clubId;
    private String clubCode;
    @JsonProperty(value = "Club")
    private String clubName;
    private String contact;

    private String address1;
    private String address2;
    private String city;
    private String province;
    private String countryId;
    @JsonProperty(value = "PostCode")
    private String postalCode;

    @JsonProperty(value = "Phone")
    private String officePhoneNumber;
    @JsonProperty(value = "PhoneAlt")
    private String alternativePhoneNumber;
    @JsonProperty(value = "PhoneFax")
    private String faxNumber;
    private String email;

    @JsonProperty(value = "WebSite")
    private String website;
}
