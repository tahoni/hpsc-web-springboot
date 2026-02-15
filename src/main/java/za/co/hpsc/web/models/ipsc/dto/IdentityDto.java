package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;

@Getter
@Setter
public abstract class IdentityDto {
    private Club clubEntity;
    private ClubIdentifier clubIdentifier;

    private String clubName;
    private Long clubId;

    public IdentityDto() {
        this.clubName = "";
        this.clubId = 0L;
    }

    public IdentityDto(String clubName) {
        this.clubName = clubName;
        this.clubId = 0L;
    }

    public IdentityDto(Club clubEntity, ClubIdentifier clubIdentifier, String clubName) {
        this.clubEntity = clubEntity;
        this.clubIdentifier = clubIdentifier;
        this.clubName = clubName;
        this.clubId = ((clubEntity != null) ? clubEntity.getId() : 0L);
    }

    public abstract boolean isRefreshRequired();
}
