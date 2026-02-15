package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;

import java.math.BigDecimal;
import java.util.Objects;

// TODO: Javadoc
// TODO: add tests
@Getter
public class MatchIdentityDto extends IdentityDto {
    private final IpscMatch matchEntity;

    private final String matchName;
    private final Long matchId;

    public MatchIdentityDto(Club clubEntity, IpscMatch matchEntity, String clubName, String matchName) {
        super(clubEntity, clubName);
        this.matchEntity = matchEntity;
        this.matchName = matchName;
        this.matchId = ((matchEntity != null) ? matchEntity.getId() : 0L);
    }

    @Override
    public boolean isRefreshRequired() {
        // A refresh of the club rankings is required if the match associated with the club
        // requires a refresh
        if (matchEntity == null) {
            return false;
        }
        return matchEntity.isRefreshRequired();
    }

    @Override
    public BigDecimal refreshRankings() {
        if (matchEntity == null) {
            return BigDecimal.ZERO;
        }

        // Calculate the highest score among the competitors associated with the match identity
        BigDecimal highestScore = this.matchEntity.getMatchCompetitors().stream()
                .map(MatchCompetitor::getMatchPoints)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Refresh the rankings in relation to the highest score for each of the matches
        this.matchEntity.refreshRankings(highestScore);
        return highestScore;
    }
}
