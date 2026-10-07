package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;

import java.time.LocalDate;
import java.util.List;

/**
 * A persisted IPSC competitor, as returned by {@code IpscCompetitorController}'s CRUD endpoints.
 *
 * <p>
 * Built from a {@link Competitor} via {@link #CompetitorResponse(Competitor)}, which flattens the home club to its
 * {@link ClubIdentifier}. {@code competitorId}, {@code firstName} and {@code lastName} are always present; every
 * other field may be null, or empty for {@code emailAddresses}.
 * </p>
 *
 * @since 8.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitorResponse {
    /** The competitor's own identifier. */
    @NonNull
    private Long competitorId;
    /** The competitor's first name. */
    @NonNull
    private String firstName;
    /** The competitor's last name. */
    @NonNull
    private String lastName;
    /** The competitor's middle name(s), if any. */
    private String middleNames;
    /** The competitor's nickname, if any. */
    private String nickName;
    /** The competitor's date of birth; may be null. */
    private LocalDate dateOfBirth;
    /** The competitor's gender; may be null. */
    private Gender gender;
    /** The identifier of the competitor's home club, or {@code null} if none is set. */
    private ClubIdentifier homeClub;
    /** The competitor's SAPSA membership number; may be null. */
    private Integer sapsaNumber;
    /** The competitor's number, as assigned for competition, may be null. */
    private Integer competitorNumber;
    /** The competitor's HPSC membership number, or {@code null} for every other home club. */
    private String clubNumber;
    /** The competitor's national identity number; may be null. */
    private String idNumber;
    /** The competitor's cellphone number; may be null. */
    private String cellphoneNumber;
    /** The competitor's email addresses; empty if none. */
    private List<String> emailAddresses;
    /** Whether the competitor's SAPSA membership is paid up; may be null if not recorded. */
    private Boolean paidUpSapsa;
    /** Whether the competitor's NGPSA membership is paid up; may be null if not recorded. */
    private Boolean paidUpNgpsa;
    /** Whether the competitor's club membership is paid up; may be null if not recorded. */
    private Boolean paidUpClub;
    /** Whether the competitor has been verified; may be null if not recorded. */
    private Boolean isVerified;

    /**
     * Creates a response from a persisted competitor.
     *
     * @param competitor the competitor to convert.
     *                   Must not be null.
     *                   The lazily loaded home club and email addresses are read here, so call this while the
     *                   persistence session is still open.
     */
    public CompetitorResponse(@NonNull Competitor competitor) {
        this.competitorId = competitor.getId();
        this.firstName = competitor.getFirstName();
        this.lastName = competitor.getLastName();
        this.middleNames = competitor.getMiddleNames();
        this.nickName = competitor.getNickName();

        this.dateOfBirth = competitor.getDateOfBirth();
        this.gender = competitor.getGender();
        this.homeClub = ((competitor.getHomeClub() != null) ? competitor.getHomeClub().getIdentifier() : null);
        this.sapsaNumber = competitor.getSapsaNumber();
        this.competitorNumber = competitor.getCompetitorNumber();
        this.clubNumber = competitor.getClubNumber();
        this.idNumber = competitor.getIdNumber();

        this.cellphoneNumber = competitor.getCellphoneNumber();
        this.emailAddresses = competitor.getEmailAddresses();

        this.paidUpSapsa = competitor.getPaidUpSapsa();
        this.paidUpNgpsa = competitor.getPaidUpNgpsa();
        this.paidUpClub = competitor.getPaidUpClub();
        this.isVerified = competitor.getIsVerified();
    }
}
