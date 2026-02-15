package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClubIdentityDto extends IdentityDto {
    List<IpscMatch> matchEntities = new ArrayList<>();

    public ClubIdentityDto(Club clubEntity, ClubIdentifier clubIdentifier, List<IpscMatch> matchEntities,
                           String clubName) {
        super(clubEntity, clubIdentifier, clubName);
        this.matchEntities = matchEntities;
    }

    @Override
    public boolean isRefreshRequired() {
        if (matchEntities == null) {
            return false;
        }

        // A refresh of the club rankings is required if any of the matches associated with the club
        // require a refresh
        for (IpscMatch ipscMatch : matchEntities) {
            boolean isRefreshOfMatchRequired = ipscMatch.isRefreshRequired();
            if (isRefreshOfMatchRequired) {
                return true;
            }
        }
        return false;
    }
}
