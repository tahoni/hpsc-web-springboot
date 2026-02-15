package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;

@Getter
@Setter
public class MatchIdentityDto extends IdentityDto {
    private IpscMatch matchEntity;

    private String matchName;
    private Long matchId;

    public MatchIdentityDto(Club clubEntity, ClubIdentifier clubIdentifier, IpscMatch matchEntity, String clubName, String matchName) {
        super(clubEntity, clubIdentifier, clubName);
        this.matchEntity = matchEntity;
        this.matchName = matchName;
        this.matchId = ((matchEntity != null) ? matchEntity.getId() : 0L);
    }

    @Override
    public boolean isRefreshRequired() {
        if (matchEntity == null) {
            return false;
        }
        return matchEntity.isRefreshRequired();
    }
}
