package za.co.hpsc.web.models.ipsc.match.holders.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;

/**
 * Data Transfer Object (DTO) for representing match results.
 *
 * <p>
 * The {@code MatchResultsDto} class is used to encapsulate and transfer
 * comprehensive data related to a shooting match's results across different
 * layers of the application. It provides an aggregated view of a match,
 * its associated club, competitors, stages, and other related details.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyResultsDto {
    private MatchOnlyDto match;
    private ClubDto club;
}
