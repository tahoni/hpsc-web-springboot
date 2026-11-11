package za.co.hpsc.web.models.ipsc.competitor.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;

import java.util.Objects;

/**
 * Every value a bulk import row supplied, exactly as given and all as text, with a value the row left out reported as
 * an empty string. Nothing is resolved or looked up, so it can describe a row that could not be imported, whatever is
 * missing or unknown in it. The email addresses are a single value, separated by
 * {@link SystemConstants#ARRAY_SEPARATOR} as in the CSV.
 *
 * @see CompetitorBulkResponse
 * @since 16.5.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CompetitorRow {
    private String firstName = "";
    private String lastName = "";
    private String middleNames = "";
    private String nickName = "";
    private String dateOfBirth = "";
    private String gender = "";
    private String homeClub = "";
    private String sapsaNumber = "";
    private String competitorNumber = "";
    private String clubNumber = "";
    private String idNumber = "";
    private String cellphoneNumber = "";
    private String emailAddresses = "";
    private String paidUpSapsa = "";
    private String paidUpNgpsa = "";
    private String paidUpClub = "";
    private String isVerified = "";

    /**
     * Describes a request by the values it supplied.
     *
     * @param request the request to describe; may be null, as for an empty row, in which case every value is empty.
     */
    public CompetitorRow(CompetitorRequest request) {
        if (request == null) {
            return;
        }
        firstName = text(request.getFirstName());
        lastName = text(request.getLastName());
        middleNames = text(request.getMiddleNames());
        nickName = text(request.getNickName());
        dateOfBirth = text(request.getDateOfBirth());
        gender = text(request.getGender());
        homeClub = text(request.getHomeClub());
        sapsaNumber = text(request.getSapsaNumber());
        competitorNumber = text(request.getCompetitorNumber());
        clubNumber = text(request.getClubNumber());
        idNumber = text(request.getIdNumber());
        cellphoneNumber = text(request.getCellphoneNumber());
        emailAddresses = (request.getEmailAddresses() == null) ? ""
                : String.join(SystemConstants.ARRAY_SEPARATOR, request.getEmailAddresses());
        paidUpSapsa = text(request.getPaidUpSapsa());
        paidUpNgpsa = text(request.getPaidUpNgpsa());
        paidUpClub = text(request.getPaidUpClub());
        isVerified = text(request.getIsVerified());
    }

    private static String text(Object value) {
        return Objects.toString(value, "");
    }
}
