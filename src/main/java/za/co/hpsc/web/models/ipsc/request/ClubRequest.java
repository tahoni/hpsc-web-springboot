package za.co.hpsc.web.models.ipsc.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
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
    @JacksonXmlProperty(isAttribute = true, localName = "clubId")
    private Integer clubId;

    @JacksonXmlProperty(isAttribute = true, localName = "clubCode")
    private String clubCode;

    @JacksonXmlProperty(isAttribute = true, localName = "Club")
    @JsonProperty(value = "Club")
    private String clubName;

    @JacksonXmlProperty(isAttribute = true, localName = "contact")
    private String contact;

    @JacksonXmlProperty(isAttribute = true, localName = "address1")
    private String address1;

    @JacksonXmlProperty(isAttribute = true, localName = "address2")
    private String address2;

    @JacksonXmlProperty(isAttribute = true, localName = "city")
    private String city;

    @JacksonXmlProperty(isAttribute = true, localName = "province")
    private String province;

    @JacksonXmlProperty(isAttribute = true, localName = "countryId")
    private String countryId;

    @JacksonXmlProperty(isAttribute = true, localName = "PostCode")
    @JsonProperty(value = "PostCode")
    private String postalCode;

    @JacksonXmlProperty(isAttribute = true, localName = "Phone")
    @JsonProperty(value = "Phone")
    private String officePhoneNumber;

    @JacksonXmlProperty(isAttribute = true, localName = "PhoneAlt")
    @JsonProperty(value = "PhoneAlt")
    private String alternativePhoneNumber;

    @JacksonXmlProperty(isAttribute = true, localName = "PhoneFax")
    @JsonProperty(value = "PhoneFax")
    private String faxNumber;

    @JacksonXmlProperty(isAttribute = true, localName = "email")
    private String email;

    @JacksonXmlProperty(isAttribute = true, localName = "WebSite")
    @JsonProperty(value = "WebSite")
    private String website;
}
