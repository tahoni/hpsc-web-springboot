package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    List<CompetitorDto> competitors = new ArrayList<>();
    List<MatchStageDto> stages = new ArrayList<>();
    List<MatchCompetitorDto> matchCompetitors = new ArrayList<>();
    List<MatchStageCompetitorDto> matchStageCompetitors = new ArrayList<>();

    /**
     * Constructs a new {@code MatchResultsDto} instance based on the provided match.
     *
     * @param match the {@link MatchDto} object representing the shooting match.
     *              It contains details such as the match's name, scheduled date,
     *              division, associated club, and other metadata.
     *              Must not be null.
     */
    public MatchResultsDto(@NotNull MatchDto match) {
        this.match = match;
        this.club = match.getClub();
    }
}
