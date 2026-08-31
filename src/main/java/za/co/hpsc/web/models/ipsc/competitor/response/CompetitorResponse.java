package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;

import java.time.LocalDate;

/**
 * A persisted IPSC competitor, as returned by {@code IpscCompetitorController}'s CRUD endpoints.
 *
 * @since 8.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorResponse {
    /** The competitor's own identifier. */
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
    /** The competitor's gender. */
    private Gender gender;
    /** The identifier of the competitor's home club, or {@code null} if none is set. */
    private ClubIdentifier homeClub;
    /** The competitor's SAPSA membership number. */
    private Integer sapsaNumber;
    /** The competitor's number, as assigned for competition. */
    private String competitorNumber;
    /** The competitor's home club membership number. */
    private String clubNumber;
    /** The competitor's national identity number. */
    private String idNumber;
    /** The competitor's cellphone number. */
    private String cellphoneNumber;
    /** The competitor's email address. */
    private String emailAddress;
}
