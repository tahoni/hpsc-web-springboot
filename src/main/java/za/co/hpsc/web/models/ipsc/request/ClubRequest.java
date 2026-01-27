package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequest {
    @NotNull
    private Integer clubId;
    private String clubCode;
    private String clubName;
    private String contact;

    private String address1;
    private String address2;
    private String city;
    private String province;
    private String countryId;
    private String postalCode;

    private String officePhoneNumber;
    private String alternativePhoneNumber;
    private String faxNumber;
    private String email;

    private String website;
}
