package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * Request to partially update an existing IPSC competitor.
 *
 * <p>
 * The competitor to update is identified by the ID in the request path, so no field here is required: any field left
 * {@code null} is left unchanged on the competitor. That includes {@code emailAddresses}, which is {@code null}
 * unless supplied, so omitting it keeps the competitor's addresses rather than clearing them. Unlike
 * {@link CompetitorRequest}, which creates or replaces a competitor in full, this carries no {@code competitorId}.
 * </p>
 *
 * @see CompetitorRequest
 * @since 9.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CompetitorPatchRequest {
    /** The competitor's first name; may be null. */
    private String firstName;
    /** The competitor's last name; may be null. */
    private String lastName;
    /** The competitor's middle name(s); may be null. */
    private String middleNames;
    /** The competitor's nickname; may be null. */
    private String nickname;
    /** The competitor's date of birth; may be null. */
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate dateOfBirth;
    /** The competitor's gender; resolved against {@link za.co.hpsc.web.enums.Gender} by name. May be null. */
    private String gender;
    /** The name or abbreviation of the competitor's home club; resolved against existing clubs. May be null. */
    private String homeClub;
    /** The competitor's SAPSA membership number; may be null. */
    private Integer sapsaNumber;
    /** The competitor's number, as assigned for competition; may be null. */
    private String competitorNumber;
    /**
     * The competitor's HPSC membership number; must be unique across all competitors. Supplying this or
     * {@code homeClub} re-applies the club number rule: required when the resulting home club is
     * {@link za.co.hpsc.web.constants.IpscConstants#HOME_CLUB_ABBREVIATION}, forced to {@code null} otherwise.
     */
    private String clubNumber;
    /** The competitor's national identity number; may be null. */
    private String idNumber;
    /** The competitor's cellphone number; may be null. */
    private String cellphoneNumber;
    /** The competitor's email addresses, replacing any existing ones; {@code null} leaves them unchanged. */
    private List<String> emailAddresses;
    /** Whether the competitor's SAPSA membership is paid up; may be null. */
    private Boolean paidUpSapsa;
    /** Whether the competitor's club membership is paid up; may be null. */
    private Boolean paidUpClub;
    /** Whether the competitor has been verified; may be null. */
    private Boolean isVerified;
}
