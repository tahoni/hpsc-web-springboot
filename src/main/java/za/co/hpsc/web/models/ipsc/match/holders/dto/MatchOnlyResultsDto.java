package za.co.hpsc.web.models.ipsc.match.holders.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;

// TODO: add Javadoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyResultsDto {
    private MatchOnlyDto match;
    private ClubDto club;
}
