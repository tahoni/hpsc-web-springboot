package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Request to create or update an IPSC competitor.
 *
 * @see za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse
 * @since 8.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CompetitorRequest {
    /** Identifier of the competitor to update, or {@code null} when creating a new competitor. */
    private Long competitorId;
    /** The competitor's first name. */
    @JsonProperty(required = true)
    private String firstName;
    /** The competitor's last name. */
    @JsonProperty(required = true)
    private String lastName;
    /** The competitor's middle name(s), if any. */
    private String middleNames;
    /** The competitor's nickname, if any. */
    private String nickName;
    /** The competitor's date of birth. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate dateOfBirth;
    /** The competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name. */
    private String gender;
    /** The abbreviation of the competitor's home club; resolved against existing clubs by name or abbreviation. */
    private String homeClub;
    /** The competitor's SAPSA membership number. */
    private Integer sapsaNumber;
    /** The competitor's number, as assigned for competition. */
    private String competitorNumber;
    /**
     * The competitor's HPSC membership number; must be unique across all competitors. Required
     * when {@code homeClub} is {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION},
     * ignored (forced to {@code null}) otherwise.
     */
    private String clubNumber;
    /** The competitor's national identity number. */
    private String idNumber;
    /** The competitor's cellphone number. */
    private String cellphoneNumber;
    /** The competitor's email addresses, if any. */
    private List<String> emailAddresses = new ArrayList<>();
    /** Whether the competitor's SAPSA membership is paid up; stored as {@code null} when omitted. */
    private Boolean paidUpSapsa;
    /** Whether the competitor's club membership is paid up; stored as {@code null} when omitted. */
    private Boolean paidUpClub;
    /** Whether the competitor has been verified; stored as {@code null} when omitted. */
    private Boolean isVerified;

    /**
     * Constructs a {@code CompetitorRequest} from its JSON representation.
     *
     * @param competitorId     the identifier of the competitor to update, or {@code null} when creating a new one.
     * @param firstName        the competitor's first name. Must not be null or blank.
     * @param lastName         the competitor's last name. Must not be null or blank.
     * @param middleNames      the competitor's middle name(s), if any.
     * @param nickName         the competitor's nickname, if any.
     * @param dateOfBirth      the competitor's date of birth.
     * @param gender           the competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name.
     * @param homeClub         the name of the competitor's home club; resolved against existing clubs by name.
     * @param sapsaNumber      the competitor's SAPSA membership number.
     * @param competitorNumber the competitor's number, as assigned for competition.
     * @param clubNumber       the competitor's HPSC membership number; must be unique across all competitors.
     *                         Required when {@code homeClub} is HPSC, ignored (forced to {@code null}) otherwise.
     * @param idNumber         the competitor's national identity number.
     * @param cellphoneNumber  the competitor's cellphone number.
     * @param emailAddresses   the competitor's email addresses, if any.
     * @param paidUpSapsa      whether the competitor's SAPSA membership is paid up; stored as {@code null} when
     *                         omitted.
     * @param paidUpClub       whether the competitor's club membership is paid up; stored as {@code null} when
     *                         omitted.
     * @param isVerified       whether the competitor has been verified; stored as {@code null} when omitted.
     */
    @JsonCreator
    public CompetitorRequest(@JsonProperty("competitorId") Long competitorId,
                             @JsonProperty(value = "firstName", required = true) String firstName,
                             @JsonProperty(value = "lastName", required = true) String lastName,
                             @JsonProperty("middleNames") String middleNames,
                             @JsonProperty("nickName") String nickName,
                             @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                             @JsonProperty("gender") String gender,
                             @JsonProperty("homeClub") String homeClub,
                             @JsonProperty("sapsaNumber") Integer sapsaNumber,
                             @JsonProperty("competitorNumber") String competitorNumber,
                             @JsonProperty("clubNumber") String clubNumber,
                             @JsonProperty("idNumber") String idNumber,
                             @JsonProperty("cellphoneNumber") String cellphoneNumber,
                             @JsonProperty("emailAddresses") List<String> emailAddresses,
                             @JsonProperty("paidUpSapsa") Boolean paidUpSapsa,
                             @JsonProperty("paidUpClub") Boolean paidUpClub,
                             @JsonProperty("isVerified") Boolean isVerified) {
        this.competitorId = competitorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleNames = middleNames;
        this.nickName = nickName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.homeClub = homeClub;
        this.sapsaNumber = sapsaNumber;
        this.competitorNumber = competitorNumber;
        this.clubNumber = clubNumber;
        this.idNumber = idNumber;
        this.cellphoneNumber = cellphoneNumber;
        if (emailAddresses != null) {
            this.emailAddresses = emailAddresses;
        }
        this.paidUpSapsa = paidUpSapsa;
        this.paidUpClub = paidUpClub;
        this.isVerified = isVerified;
    }

    /**
     * Checks that the request carries the names needed to create a competitor, before any of it is used. Nothing is
     * looked up, so this only checks that values are present.
     *
     * <p>
     * The request is valid when both {@code firstName} and {@code lastName} have text, so a null, empty or
     * whitespace-only name is rejected. Every other field is optional; the home club, gender and other values that
     * are resolved against enums or the database are not checked here, so a value that is present but unknown is not
     * caught.
     * </p>
     *
     * @throws ValidationException if the first name or the last name is missing.
     */
    public void validate() {
        if (!hasText(getFirstName())) {
            throw new ValidationException("First name is required.");
        }
        if (!hasText(getLastName())) {
            throw new ValidationException("Last name is required.");
        }
    }
}
