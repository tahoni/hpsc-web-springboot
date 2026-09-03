package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

import java.time.LocalDate;

/**
 * Request model for bulk-importing IPSC matches from CSV data.
 *
 * <p>
 * Mirrors {@link MatchRequest}'s fields, other than {@code matchId} — CSV bulk import only ever
 * creates new matches, so no identifier is accepted. {@link MatchRequest}'s nested
 * {@code stages} list is instead represented here as {@link #stages}, a single
 * semicolon-separated CSV cell of {@code <stageNumber>-<stageName>} entries (e.g.
 * {@code "1-Stage 1;2-Stage 2"}). Column headers are matched using
 * {@link PropertyNamingStrategies.UpperCamelCaseStrategy}, so a CSV header of {@code MatchName}
 * maps onto the {@code matchName} field, and so on.
 * </p>
 *
 * @see MatchRequest
 * @since 8.3.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class MatchRequestForCSV {
    /** Date the match was/will be shot. */
    @JsonProperty(required = true)
    @JsonFormat(pattern = IpscConstants.IPSC_INPUT_DATE_FORMAT)
    private LocalDate matchDate;
    /** The match's name. */
    @JsonProperty(required = true)
    private String matchName;
    /**
     * The name of the club hosting the match; resolved against existing clubs by name. May be
     * null or blank, in which case the match defaults to
     * {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     */
    private String club;
    /** The firearm type this match is shot with; resolved against {@link za.co.hpsc.web.enums.FirearmType} by name. */
    private String matchFirearmType;
    /** The category/tier of this match; resolved against {@link za.co.hpsc.web.enums.MatchCategory} by name. */
    private String matchCategory;
    /** The stages that make up this match, as a single semicolon-separated CSV cell of
     * {@code <stageNumber>-<stageName>} entries (e.g. {@code "1-Stage 1;2-Stage 2"}). */
    private String stages;

    /**
     * Constructs a {@code MatchRequestForCSV} from its CSV/JSON representation.
     *
     * <p>
     * Each parameter is bound to its {@link PropertyNamingStrategies.UpperCamelCaseStrategy}
     * column/property name explicitly, since {@code @JsonNaming} alone only governs
     * serialisation — a multi-argument {@code @JsonCreator} constructor needs each parameter's
     * name spelled out for Jackson to bind it during deserialisation.
     * </p>
     *
     * @param matchDate        the date the match was/will be shot. Must not be null.
     * @param matchName        the match's name. Must not be null or blank.
     * @param club             the name of the club hosting the match; resolved against existing clubs by name.
     *                         May be null or blank, in which case the match defaults to
     *                         {@link IpscConstants#DEFAULT_MATCH_CLUB_IDENTIFIER}.
     * @param matchFirearmType the firearm type this match is shot with; resolved against
     *                         {@link za.co.hpsc.web.enums.FirearmType} by name.
     * @param matchCategory    the category/tier of this match; resolved against
     *                         {@link za.co.hpsc.web.enums.MatchCategory} by name.
     * @param stages           the stages that make up this match, as a single semicolon-separated
     *                         CSV cell of {@code <stageNumber>-<stageName>} entries.
     */
    @JsonCreator
    public MatchRequestForCSV(@JsonProperty(value = "MatchDate", required = true) LocalDate matchDate,
                              @JsonProperty(value = "MatchName", required = true) String matchName,
                              @JsonProperty("Club") String club,
                              @JsonProperty("MatchFirearmType") String matchFirearmType,
                              @JsonProperty("MatchCategory") String matchCategory,
                              @JsonProperty("Stages") String stages) {
        this.matchDate = matchDate;
        this.matchName = matchName;
        this.club = club;
        this.matchFirearmType = matchFirearmType;
        this.matchCategory = matchCategory;
        this.stages = stages;
    }
}
