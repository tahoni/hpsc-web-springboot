package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Jackson mix-in binding {@link CompetitorRequest}'s constructor to the UpperCamelCase column
 * headers used by {@link CompetitorRequestCsv}. It is declared with the same signature as
 * {@link CompetitorRequest}'s {@code @JsonCreator}, so its annotations replace that constructor's
 * for CSV reading only.
 */
public abstract class CompetitorRequestCsvMixIn {
    @JsonCreator
    CompetitorRequestCsvMixIn(@JsonProperty(value = "FirstName", required = true) String firstName,
                              @JsonProperty(value = "LastName", required = true) String lastName,
                              @JsonProperty("MiddleNames") String middleNames,
                              @JsonProperty("Nickname") String nickname,
                              @JsonProperty("DateOfBirth") LocalDate dateOfBirth,
                              @JsonProperty("Gender") String gender,
                              @JsonProperty("HomeClub") String homeClub,
                              @JsonProperty("SapsaNumber") Integer sapsaNumber,
                              @JsonProperty("CompetitorNumber") String competitorNumber,
                              @JsonProperty("ClubNumber") String clubNumber,
                              @JsonProperty("IdNumber") String idNumber,
                              @JsonProperty("CellphoneNumber") String cellphoneNumber,
                              @JsonProperty("PaidUpSapsa") Boolean paidUpSapsa,
                              @JsonProperty("PaidUpClub") Boolean paidUpClub,
                              @JsonProperty("EmailAddresses") String emailAddresses) {
    }
}
