package za.co.hpsc.web.models.ipsc.shooterlog.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.SystemConstants;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Every value a bulk import row supplied, exactly as given and all as text, with a value the row left out reported as
 * an empty string. Nothing is resolved or looked up, so it can describe a row that could not be imported, whatever is
 * missing or unknown in it.
 *
 * @see ShooterLogBulkResponse
 * @since 16.3.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ShooterLogRow {
    /** The identifier of the shooter log to change, for a bulk update row; empty for a row that creates a log. */
    private String shooterLogId = "";
    private String startDate = "";
    private String endDate = "";
    /** The match identifiers, separated by {@link SystemConstants#ARRAY_SEPARATOR}. */
    private String matchIds = "";
    /** The identifiers of the matches a bulk partial update row adds, separated like {@link #matchIds}. */
    private String addMatchIds = "";
    /** The identifiers of the matches a bulk partial update row removes, separated like {@link #matchIds}. */
    private String removeMatchIds = "";

    /**
     * Describes a request by the values it supplied.
     *
     * @param request the request to describe; may be null, as for an empty row, in which case every value is empty.
     */
    public ShooterLogRow(ShooterLogRequest request) {
        if (request == null) {
            return;
        }
        startDate = text(request.getStartDate());
        endDate = text(request.getEndDate());
        if (request.getMatchIds() != null) {
            matchIds = request.getMatchIds().stream()
                    .map(ShooterLogRow::text)
                    .collect(Collectors.joining(SystemConstants.ARRAY_SEPARATOR));
        }
    }

    private static String text(Object value) {
        return Objects.toString(value, "");
    }
}
