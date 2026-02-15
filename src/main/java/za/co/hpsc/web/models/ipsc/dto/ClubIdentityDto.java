package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.ClubMatch;
import za.co.hpsc.web.domain.ClubMatchCompetitor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: Javadoc
// TODO: add tests
@Getter
public class ClubIdentityDto extends IdentityDto {
    private final List<ClubMatch> matchEntities;

    public ClubIdentityDto(Club clubEntity, List<ClubMatch> matchEntities,
                           String clubName) {
        super(clubEntity, clubName);
        this.matchEntities = (matchEntities != null ? matchEntities : new ArrayList<>());
    }

    @Override
    public boolean isRefreshRequired() {
        // A refresh of the club rankings is required if any of the matches associated with the club
        // require a refresh
        for (ClubMatch clubMatch : this.matchEntities) {
            boolean isRefreshOfMatchRequired = clubMatch.isRefreshRequired();
            if (isRefreshOfMatchRequired) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal refreshRankings() {
        // Calculate the highest score among the competitors associated with the club identity
        BigDecimal highestScore = this.matchEntities.stream()
                .filter(matchEntity -> matchEntity.getClubCompetitors() != null)
                .flatMap(matchEntity -> matchEntity.getClubCompetitors().stream())
                .map(ClubMatchCompetitor::getClubPoints)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Refresh the rankings in relation to the highest score for each of the matches
        this.matchEntities.forEach(matchEntity -> matchEntity.refreshRankings(highestScore));
        return highestScore;
    }
}
