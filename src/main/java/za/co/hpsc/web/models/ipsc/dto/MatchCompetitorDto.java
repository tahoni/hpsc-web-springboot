package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the association between a competitor and a match.
 *
 * <p>
 * The {@code MatchCompetitorDto} class encapsulates data related to a competitor's
 * participation in a specific match.
 * It includes division, discipline, power factor, and scoring details.
 * Additionally, it holds references to the associated competitor and match entities.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchCompetitorDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;

    @NotNull
    private CompetitorDto competitor;
    @NotNull
    private MatchDto match;

    private Division division;
    private Discipline discipline;
    private PowerFactor powerFactor;

    private BigDecimal matchPoints;
    private BigDecimal matchPercentage;

    private LocalDateTime dateUpdated;

    /**
     * Constructs a new {@code MatchCompetitorDto} instance with data from the
     * provided {@link MatchCompetitor} entity.
     *
     * @param matchCompetitorEntity the {@link MatchCompetitor} entity containing information
     *                              about a competitor's participation in a specific match.
     *                              Must not be null.
     */
    public MatchCompetitorDto(MatchCompetitor matchCompetitorEntity) {
        this.id = matchCompetitorEntity.getId();
        this.competitor = new CompetitorDto(matchCompetitorEntity.getCompetitor());
        this.match = new MatchDto(matchCompetitorEntity.getMatch());
        this.division = matchCompetitorEntity.getDivision();
        this.discipline = matchCompetitorEntity.getDiscipline();
        this.powerFactor = matchCompetitorEntity.getPowerFactor();
        this.matchPoints = matchCompetitorEntity.getMatchPoints();
        this.matchPercentage = matchCompetitorEntity.getMatchPercentage();
        this.dateUpdated = matchCompetitorEntity.getDateUpdated();
    }

    /**
     * Constructs a new {@code MatchCompetitorDto} instance, associating a competitor with a match.
     *
     * @param competitorDto the {@link CompetitorDto} representing the competitor in the match.
     *                      Must not be null.
     * @param matchDto      the {@link MatchDto} representing the match in which the
     *                      competitor participates. Must not be null.
     */
    public MatchCompetitorDto(@NotNull CompetitorDto competitorDto, @NotNull MatchDto matchDto) {
        this.competitor = competitorDto;
        this.match = matchDto;
    }

    // TODO: Javadoc (not yet ready)
    public void init(List<ScoreResponse> scoreResponses) {
        // Initializes aggregate score from multiple score responses
        this.matchPoints = BigDecimal.valueOf(scoreResponses.stream()
                .mapToDouble(ScoreResponse::getFinalScore)
                .sum());
        // Initialises the date updated from the latest score response's date updated
        this.dateUpdated =
                scoreResponses.stream()
                        .map(ScoreResponse::getLastModified)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());

        // TODO: populate category, division, discipline, power factor
    }

    public String toString() {
        return match.toString() + ": " + competitor.toString();
    }
}
