package za.co.hpsc.web.models.ipsc.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class IdentityDto {
    private Club clubEntity;

    private String clubName;
    private Long clubId;

    public IdentityDto(Club clubEntity, String clubName) {
        this.clubEntity = clubEntity;
        this.clubName = clubName;
        this.clubId = ((clubEntity != null) ? clubEntity.getId() : 0L);
    }

    public abstract boolean isRefreshRequired();

    public abstract BigDecimal refreshRankings();
}
