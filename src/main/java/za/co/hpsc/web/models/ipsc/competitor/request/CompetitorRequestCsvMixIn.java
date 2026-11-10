package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Jackson mix-in binding {@link CompetitorRequest}'s CSV constructor to the UpperCamelCase column
 * headers used by the bulk CSV import. It disables {@link CompetitorRequest}'s {@code @JsonCreator}, which takes a
 * {@code competitorId}, and binds the constructor without one instead, for CSV reading only. Unknown columns are
 * ignored.
 *
 * <p>
 * CSV bulk import only ever creates new competitors, so a row has no identifier of its own: a {@code CompetitorId}
 * column is not read. {@code EmailAddresses} is a single cell of addresses separated by the shared array separator.
 * </p>
 *
 * @since 9.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CompetitorRequestCsvMixIn {
    /** The JSON constructor, which takes a {@code competitorId}, is not used to read CSV. */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    CompetitorRequestCsvMixIn(Long competitorId, String firstName, String lastName, String middleNames,
                              String nickName, LocalDate dateOfBirth, String gender, String homeClub,
                              Integer sapsaNumber, String competitorNumber, String clubNumber, String idNumber,
                              String cellphoneNumber, List<String> emailAddresses, Boolean paidUpSapsa,
                              Boolean paidUpNgpsa, Boolean paidUpClub, Boolean isVerified) {
    }

    @JsonCreator
    CompetitorRequestCsvMixIn(@JsonProperty(value = "FirstName", required = true) String firstName,
                              @JsonProperty(value = "LastName", required = true) String lastName,
                              @JsonProperty("MiddleNames") String middleNames,
                              @JsonProperty("NickName") String nickName,
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
                              @JsonProperty("PaidUpNgpsa") Boolean paidUpNgpsa,
                              @JsonProperty("PaidUpClub") Boolean paidUpClub,
                              @JsonProperty("IsVerified") Boolean isVerified) {
    }
}
