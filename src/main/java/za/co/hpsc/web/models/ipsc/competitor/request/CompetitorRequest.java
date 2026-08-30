package za.co.hpsc.web.models.ipsc.competitor.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request to create or update an IPSC competitor.
 *
 * @see za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse
 * @since 8.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorRequest {
    /** Identifier of the competitor to update, or {@code null} when creating a new competitor. */
    private Long competitorId;
    /** The competitor's first name. */
    private String firstName;
    /** The competitor's last name. */
    private String lastName;
    /** The competitor's middle name(s), if any. */
    private String middleNames;
    /** The competitor's nickname, if any. */
    private String nickname;
    /** The competitor's date of birth. */
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
    private String clubNumber;
    /** The competitor's national identity number. */
    private String idNumber;
    /** The competitor's cellphone number. */
    private String cellphoneNumber;
    /** The competitor's email address. */
    private String emailAddress;
}
