package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.HpscConstants;

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
    @JsonFormat(pattern = HpscConstants.HPSC_INPUT_DATE_FORMAT)
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
     * Constructs a {@code CompetitorRequestForCSV} from its CSV/JSON representation.
     *
     * <p>
     * Each parameter is bound to its {@link PropertyNamingStrategies.UpperCamelCaseStrategy}
     * column/property name explicitly, since {@code @JsonNaming} alone only governs
     * serialisation — a multi-argument {@code @JsonCreator} constructor needs each parameter's
     * name spelled out for Jackson to bind it during deserialisation.
     * </p>
     *
     * @param firstName        the competitor's first name. Must not be null or blank.
     * @param lastName         the competitor's last name. Must not be null or blank.
     * @param middleNames      the competitor's middle name(s), if any.
     * @param nickname         the competitor's nickname, if any.
     * @param dateOfBirth      the competitor's date of birth.
     * @param gender           the competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name.
     * @param homeClub         the name of the competitor's home club; resolved against existing clubs by name.
     * @param sapsaNumber      the competitor's SAPSA membership number.
     * @param competitorNumber the competitor's number, as assigned for competition.
     * @param clubNumber       the competitor's home club membership number.
     * @param idNumber         the competitor's national identity number.
     * @param cellphoneNumber  the competitor's cellphone number.
     * @param emailAddress     the competitor's email address.
     */
    @JsonCreator
    public CompetitorRequestForCSV(@JsonProperty(value = "FirstName", required = true) String firstName,
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
                                   @JsonProperty("EmailAddress") String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleNames = middleNames;
        this.nickname = nickname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.homeClub = homeClub;
        this.sapsaNumber = sapsaNumber;
        this.competitorNumber = competitorNumber;
        this.clubNumber = clubNumber;
        this.idNumber = idNumber;
        this.cellphoneNumber = cellphoneNumber;
        this.emailAddress = emailAddress;
    }
}
