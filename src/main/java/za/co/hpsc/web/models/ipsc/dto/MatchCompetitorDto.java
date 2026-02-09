package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubReference;
import za.co.hpsc.web.enums.Discipline;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.disciplines.FirearmTypeToDisciplinesForFirearmType;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;
import za.co.hpsc.web.utils.ValueUtil;

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
 * It includes firearm type, discipline, power factor, and scoring details.
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

    private ClubReference club;
    private FirearmType firearmType;
    private Discipline discipline;
    private PowerFactor powerFactor;

    private BigDecimal matchPoints;
    private BigDecimal matchRanking;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;

    /**
     * Constructs a new {@code MatchCompetitorDto} instance with data from the
     * provided {@link MatchCompetitor} entity.
     *
     * @param matchCompetitorEntity the {@link MatchCompetitor} entity containing information
     *                              about a competitor's participation in a specific match.
     *                              Must not be null.
     */
    public MatchCompetitorDto(MatchCompetitor matchCompetitorEntity) {
        // Initialises the match competitor details
        this.id = matchCompetitorEntity.getId();
        this.competitor = new CompetitorDto(matchCompetitorEntity.getCompetitor());
        this.match = new MatchDto(matchCompetitorEntity.getMatch());

        // Initialises the competitor attributes
        this.club = matchCompetitorEntity.getClub();
        this.firearmType = matchCompetitorEntity.getFirearmType();
        this.discipline = matchCompetitorEntity.getDiscipline();
        this.powerFactor = matchCompetitorEntity.getPowerFactor();

        // Initialises the competitor scoring details
        this.matchPoints = matchCompetitorEntity.getMatchPoints();
        this.matchRanking = matchCompetitorEntity.getMatchRanking();

        // Initialises the date fields
        this.dateCreated = matchCompetitorEntity.getDateCreated();
        this.dateUpdated = LocalDateTime.now();
    }

    /**
     * Constructs a new {@code MatchCompetitorDto} instance, associating a competitor with a match.
     *
     * @param competitorDto the {@link CompetitorDto} representing the competitor in the match.
     *                      Must not be null.
     * @param matchDto      the {@link MatchDto} representing the match in which the
     *                      competitor participates.
     *                      Must not be null.
     */
    public MatchCompetitorDto(@NotNull CompetitorDto competitorDto, @NotNull MatchDto matchDto) {
        // Initialises the match competitor details
        this.competitor = competitorDto;
        this.match = matchDto;

        // Initialises the date fields
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
        this.dateEdited = LocalDateTime.now();
    }

    // TODO: Javadoc (not yet ready)
    public void init(List<ScoreResponse> scoreResponses, EnrolledResponse enrolledResponse) {
        // Initializes aggregate score from multiple score responses
        this.matchPoints = BigDecimal.ZERO;
        if (scoreResponses != null) {
            scoreResponses.forEach(scoreResponse -> matchPoints =
                    matchPoints.add(BigDecimal.valueOf(ValueUtil.nullAsZero(scoreResponse.getFinalScore()))));
        }

        // Don't overwrite an existing date creation timestamp
        this.dateCreated = ((this.dateCreated != null) ? this.dateCreated : LocalDateTime.now());
        // Initialises the date updated
        this.dateUpdated = LocalDateTime.now();
        // Sets the date edited to the latest score update timestamp
        if (scoreResponses != null) {
            this.dateEdited = scoreResponses.stream()
                    .map(ScoreResponse::getLastModified)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
        } else {
            this.dateEdited = LocalDateTime.now();
        }

        // Initialises the competitor attributes
        if (enrolledResponse != null) {
            // Determines the power factor based on the major power factor flag
            this.powerFactor = (enrolledResponse.getMajorPowerFactor() ? PowerFactor.MAJOR : PowerFactor.MINOR);
            // Determines the club based on the club reference number
            this.club = ClubReference.getByCode(enrolledResponse.getRefNo()).orElse(ClubReference.UNKNOWN);
            // Determines the discipline based on the division ID
            this.discipline = Discipline.getByCode(enrolledResponse.getDivisionId()).orElse(null);
            // Determines the firearm type from the discipline
            this.firearmType =
                    FirearmTypeToDisciplinesForFirearmType.getFirearmTypeFromDiscipline(this.discipline);
            // TODO: populate category
        }
    }

    public String toString() {
        return this.match.toString() + ": " + this.competitor.toString();
    }
}
