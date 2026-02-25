package za.co.hpsc.web.models.ipsc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.IpscMatch;

import java.util.ArrayList;
import java.util.List;

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
public class MatchResultsDto {
    private MatchDto match;
    private ClubDto club;
    private List<ScoreDto> scores = new ArrayList<>();
    private List<CompetitorDto> competitors = new ArrayList<>();
    private List<MatchStageDto> stages = new ArrayList<>();
    private List<MatchCompetitorDto> matchCompetitors = new ArrayList<>();
    private List<MatchStageCompetitorDto> matchStageCompetitors = new ArrayList<>();

    private IpscMatch ipscMatch;

    /**
     * Constructs a new {@code MatchResultsDto} instance based on the provided match.
     *
     * @param match the {@link MatchDto} object representing the shooting match.
     *              It contains details such as the match's name, scheduled date,
     *              firearm type, associated club, and other metadata.
     */
    public MatchResultsDto(MatchDto match) {
        this.match = match;
    }
}
