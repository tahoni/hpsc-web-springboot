package za.co.hpsc.web.models.ipsc.match.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;

import java.util.Objects;

/**
 * Every value a bulk import row supplied, exactly as given and all as text, with a value the row left out reported as
 * an empty string. Nothing is resolved or looked up, so it can describe a row that could not be imported, whatever is
 * missing or unknown in it.
 *
 * @see MatchBulkResponse
 * @since 17.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchRow {
    private String matchDate = "";
    private String matchName = "";
    private String club = "";
    private String matchFirearmType = "";
    private String matchCategory = "";
    private String startTime = "";
    private String endTime = "";
    private String url = "";

    /**
     * Describes a request by the values it supplied.
     *
     * @param request the request to describe; may be null, as for an empty row, in which case every value is empty.
     */
    public MatchRow(MatchRequest request) {
        if (request == null) {
            return;
        }
        matchDate = text(request.getMatchDate());
        matchName = text(request.getMatchName());
        club = text(request.getClub());
        matchFirearmType = text(request.getMatchFirearmType());
        matchCategory = text(request.getMatchCategory());
        startTime = text(request.getStartTime());
        endTime = text(request.getEndTime());
        url = text(request.getUrl());
    }

    private static String text(Object value) {
        return Objects.toString(value, "");
    }
}
