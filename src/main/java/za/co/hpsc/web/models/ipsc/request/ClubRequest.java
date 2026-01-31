package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequest {
    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ClubId", required = true)
    private Integer clubId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "ClubCode")
    private String clubCode;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Club")
    private String clubName;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Contact")
    private String contact;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Address1")
    private String address1;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Address2")
    private String address2;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "City")
    private String city;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Province")
    private String province;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "CountryId")
    private String countryId;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PostCode")
    private String postalCode;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Phone")
    private String officePhoneNumber;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PhoneAlt")
    private String alternativePhoneNumber;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "PhoneFax")
    private String faxNumber;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "Email")
    private String email;

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(value = "WebSite")
    private String website;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataWrapper {
        @JacksonXmlProperty(localName = "row")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ClubRequest> row;
    }
}
