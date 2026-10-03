package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Jackson mix-in binding {@link CompetitorRequest}'s constructor to the UpperCamelCase column
 * headers used by the bulk CSV import. It is declared with the same signature as
 * {@link CompetitorRequest}'s {@code @JsonCreator}, so its annotations replace that constructor's
 * for CSV reading only. Unknown columns are ignored.
 *
 * <p>
 * CSV bulk import only ever creates new competitors, so a {@code CompetitorId} column is bound but never used.
 * {@code EmailAddresses} is a single cell of addresses separated by the shared array separator.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CompetitorRequestCsvMixIn {
    @JsonCreator
    CompetitorRequestCsvMixIn(@JsonProperty("CompetitorId") Long competitorId,
                              @JsonProperty(value = "FirstName", required = true) String firstName,
                              @JsonProperty(value = "LastName", required = true) String lastName,
                              @JsonProperty("MiddleNames") String middleNames,
                              @JsonProperty("Nickname") String nickname,
                              @JsonProperty("DateOfBirth")
                              @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT) LocalDate dateOfBirth,
                              @JsonProperty("Gender") String gender,
                              @JsonProperty("HomeClub") String homeClub,
                              @JsonProperty("SapsaNumber") Integer sapsaNumber,
                              @JsonProperty("CompetitorNumber") String competitorNumber,
                              @JsonProperty("ClubNumber") String clubNumber,
                              @JsonProperty("IdNumber") String idNumber,
                              @JsonProperty("CellphoneNumber") String cellphoneNumber,
                              @JsonProperty("EmailAddresses") List<String> emailAddresses,
                              @JsonProperty("PaidUpSapsa") Boolean paidUpSapsa,
                              @JsonProperty("PaidUpClub") Boolean paidUpClub,
                              @JsonProperty("IsVerified") Boolean isVerified) {
    }
}
