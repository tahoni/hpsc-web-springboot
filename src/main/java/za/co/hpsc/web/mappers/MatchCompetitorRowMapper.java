package za.co.hpsc.web.mappers;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.helpers.CompetitorHelpers;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorRow;

import java.util.Objects;

/**
 * Describes a bulk import row as a {@link MatchCompetitorRow}, preferring the values already resolved onto a
 * {@link MatchCompetitor} to the text the row supplied.
 *
 * <p>
 * The counterpart of {@link MatchCompetitorMapper}, which copies a request onto an entity: this maps in the other
 * direction, towards a response model. It is a class of its own because it needs no repositories, and the
 * {@link MatchCompetitorMapper} resolves things through them.
 * </p>
 */
@Component
public class MatchCompetitorRowMapper {

    /**
     * Describes a row by the values it supplied, replacing each one the match competitor has resolved with the
     * resolved value: the competitor's and match's identifiers, the competitor's name and number, and the match
     * club, category, firearm type, division and power factor. Anything the match competitor lacks keeps the text
     * the row supplied.
     *
     * @param request         the request for the row; may be null, as for an empty row.
     * @param matchCompetitor the match competitor populated as far as the row could be resolved; may be null, in
     *                        which case the row is described by the request alone.
     * @return the row's values, with a missing value as an empty string.
     */
    public MatchCompetitorRow toRow(@Nullable MatchCompetitorRequest request,
                                    @Nullable MatchCompetitor matchCompetitor) {
        MatchCompetitorRow row = new MatchCompetitorRow(request);
        row.setCompetitorName(CompetitorHelpers.cleanCompetitorName(row.getCompetitorName()));
        if (matchCompetitor == null) {
            return row;
        }

        Competitor competitor = matchCompetitor.getCompetitor();
        if (competitor != null) {
            row.setCompetitorId(text(competitor.getId(), row.getCompetitorId()));
            row.setCompetitorNumber(text(competitor.getCompetitorNumber(), row.getCompetitorNumber()));
        }
        if (matchCompetitor.getMatch() != null) {
            row.setMatchId(text(matchCompetitor.getMatch().getId(), row.getMatchId()));
        }
        row.setMatchClub(text(matchCompetitor.getMatchClub() == null ? null : matchCompetitor.getMatchClub().getName(),
                row.getMatchClub()));
        row.setCompetitorCategory(text(matchCompetitor.getCompetitorCategory() == null ? null
                : matchCompetitor.getCompetitorCategory().getName(), row.getCompetitorCategory()));
        row.setFirearmType(text(matchCompetitor.getFirearmType() == null ? null
                : matchCompetitor.getFirearmType().getNames().getFirst(), row.getFirearmType()));
        row.setDivision(text(matchCompetitor.getDivision() == null ? null
                : matchCompetitor.getDivision().getName(), row.getDivision()));
        row.setPowerFactor(text(matchCompetitor.getPowerFactor() == null ? null
                : matchCompetitor.getPowerFactor().getName(), row.getPowerFactor()));
        return row;
    }

    private static String text(@Nullable Object resolved, String fallback) {
        return (resolved == null) ? fallback : Objects.toString(resolved);
    }
}
