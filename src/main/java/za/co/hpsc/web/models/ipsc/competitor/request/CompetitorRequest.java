package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.HpscConstants;

import java.time.LocalDate;
import java.util.List;

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
    private String nickname;
    /** The competitor's date of birth. */
    @JsonFormat(pattern = HpscConstants.HPSC_INPUT_DATE_FORMAT)
    private LocalDate dateOfBirth;
    /** The competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name. */
    private String gender;
    /** The name of the competitor's home club; resolved against existing clubs by name. */
    private String homeClub;
    /** The competitor's SAPSA membership number. */
    private Integer sapsaNumber;
    /** The competitor's number, as assigned for competition. */
    private String competitorNumber;
    /** The competitor's home club membership number; must be unique across all competitors. */
    @JsonProperty(required = true)
    private String clubNumber;
    /** The competitor's national identity number. */
    private String idNumber;
    /** The competitor's cellphone number. */
    private String cellphoneNumber;
    /** The competitor's email addresses, if any. */
    private List<String> emailAddresses;

    /**
     * Constructs a {@code CompetitorRequest} from its JSON representation.
     *
     * @param competitorId     the identifier of the competitor to update, or {@code null} when creating a new one.
     * @param firstName        the competitor's first name. Must not be null or blank.
     * @param lastName         the competitor's last name. Must not be null or blank.
     * @param middleNames      the competitor's middle name(s), if any.
     * @param nickname         the competitor's nickname, if any.
     * @param dateOfBirth      the competitor's date of birth.
     * @param gender           the competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name.
     * @param homeClub         the name of the competitor's home club; resolved against existing clubs by name.
     * @param sapsaNumber      the competitor's SAPSA membership number.
     * @param competitorNumber the competitor's number, as assigned for competition.
     * @param clubNumber       the competitor's home club membership number; must be unique across all competitors.
     *                         Must not be null or blank.
     * @param idNumber         the competitor's national identity number.
     * @param cellphoneNumber  the competitor's cellphone number.
     * @param emailAddresses   the competitor's email addresses, if any.
     */
    @JsonCreator
    public CompetitorRequest(@JsonProperty("competitorId") Long competitorId,
                             @JsonProperty(value = "firstName", required = true) String firstName,
                             @JsonProperty(value = "lastName", required = true) String lastName,
                             @JsonProperty("middleNames") String middleNames,
                             @JsonProperty("nickname") String nickname,
                             @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                             @JsonProperty("gender") String gender,
                             @JsonProperty("homeClub") String homeClub,
                             @JsonProperty("sapsaNumber") Integer sapsaNumber,
                             @JsonProperty("competitorNumber") String competitorNumber,
                             @JsonProperty(value = "clubNumber", required = true) String clubNumber,
                             @JsonProperty("idNumber") String idNumber,
                             @JsonProperty("cellphoneNumber") String cellphoneNumber,
                             @JsonProperty("emailAddresses") List<String> emailAddresses) {
        this.competitorId = competitorId;
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
        this.emailAddresses = emailAddresses;
    }
}
