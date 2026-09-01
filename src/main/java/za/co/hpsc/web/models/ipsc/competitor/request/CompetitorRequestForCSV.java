package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request model for bulk-importing IPSC competitors from CSV data.
 *
 * <p>
 * Mirrors {@link CompetitorRequest}'s fields, other than {@code competitorId} — CSV bulk import
 * only ever creates new competitors, so no identifier is accepted. Column headers are matched
 * using {@link PropertyNamingStrategies.UpperCamelCaseStrategy}, so a CSV header of
 * {@code FirstName} maps onto the {@code firstName} field, and so on.
 * </p>
 *
 * @see CompetitorRequest
 * @since 8.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class CompetitorRequestForCSV {
    /**
     * The competitor's first name.
     */
    @JsonProperty(required = true)
    private String firstName;
    /**
     * The competitor's last name.
     */
    @JsonProperty(required = true)
    private String lastName;
    /**
     * The competitor's middle name(s), if any.
     */
    private String middleNames;
    /**
     * The competitor's nickname, if any.
     */
    private String nickname;
    /**
     * The competitor's date of birth.
     */
    private LocalDate dateOfBirth;
    /**
     * The competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name.
     */
    private String gender;
    /**
     * The name of the competitor's home club; resolved against existing clubs by name.
     */
    private String homeClub;
    /**
     * The competitor's SAPSA membership number.
     */
    private Integer sapsaNumber;
    /**
     * The competitor's number, as assigned for competition.
     */
    private String competitorNumber;
    /**
     * The competitor's home club membership number; must be unique across all competitors.
     */
    private String clubNumber;
    /**
     * The competitor's national identity number.
     */
    private String idNumber;
    /**
     * The competitor's cellphone number.
     */
    private String cellphoneNumber;
    /**
     * The competitor's email address.
     */
    private String emailAddress;

    /**
     * Constructs a {@code CompetitorRequestForCSV} with a subset of fields, leaving
     * {@code dateOfBirth}, {@code gender}, {@code homeClub} and {@code clubNumber} unset.
     *
     * @param firstName        the competitor's first name.
     * @param lastName         the competitor's last name.
     * @param middleNames      the competitor's middle name(s), if any.
     * @param nickname         the competitor's nickname, if any.
     * @param sapsaNumber      the competitor's SAPSA membership number.
     * @param competitorNumber the competitor's number, as assigned for competition.
     * @param cellphoneNumber  the competitor's cellphone number.
     * @param emailAddress     the competitor's email address.
     */
    public CompetitorRequestForCSV(String firstName, String lastName, String middleNames, String nickname,
                                   Integer sapsaNumber, String competitorNumber,
                                   String cellphoneNumber, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleNames = middleNames;
        this.nickname = nickname;
        this.sapsaNumber = sapsaNumber;
        this.competitorNumber = competitorNumber;
        this.cellphoneNumber = cellphoneNumber;
        this.emailAddress = emailAddress;
    }
}
