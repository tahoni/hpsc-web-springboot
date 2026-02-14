package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.divisions.FirearmTypeToDivisions;
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

    private Integer competitorIndex;
    private Integer matchIndex;

    @NotNull
    private CompetitorDto competitor;
    @NotNull
    private MatchDto match;
    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    private ClubIdentifier club;
    private FirearmType firearmType;
    private Division division;
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
     *                              about a competitor's participation in a specific match,
     *                              such as the competitor, match, and associated identifier.
     *                              Must not be null.
     */
    public MatchCompetitorDto(@NotNull MatchCompetitor matchCompetitorEntity) {
        // Initialises the match competitor details
        this.id = matchCompetitorEntity.getId();
        this.competitor = new CompetitorDto(matchCompetitorEntity.getCompetitor());
        this.match = new MatchDto(matchCompetitorEntity.getMatch());

        // Initialises the competitor attributes
        this.club = matchCompetitorEntity.getClub();
        this.competitorCategory = matchCompetitorEntity.getCompetitorCategory();
        this.firearmType = matchCompetitorEntity.getFirearmType();
        this.division = matchCompetitorEntity.getDivision();
        this.powerFactor = matchCompetitorEntity.getPowerFactor();

        // Initialises the competitor scoring details
        this.matchPoints = matchCompetitorEntity.getMatchPoints();
        this.matchRanking = matchCompetitorEntity.getMatchRanking();

        // Initialises the date fields
        this.dateCreated = matchCompetitorEntity.getDateCreated();
        this.dateUpdated = LocalDateTime.now();
    }

    /**
     * Constructs a new {@code MatchCompetitorDto} instance with data from the provided
     * {@link CompetitorDto} and {@link MatchDto} objects.
     *
     * @param competitorDto the {@link CompetitorDto} representing the competitor in the match.
     *                      Must not be null.
     * @param matchDto      the {@link MatchDto} representing the match in which the
     *                      competitor participates.
     *                      Must not be null.
     */
    public MatchCompetitorDto(CompetitorDto competitorDto, MatchDto matchDto) {
        if (competitorDto != null) {
            // Initialises the competitor and match details
            this.competitorIndex = competitorDto.getIndex();
            this.matchIndex = matchDto.getIndex();
            this.competitor = competitorDto;
            this.match = matchDto;

            // Initialises the date fields
            this.dateCreated = LocalDateTime.now();
            this.dateUpdated = LocalDateTime.now();
            this.dateEdited = LocalDateTime.now();
        }
    }

    /**
     * Initialises the current {@code CompetitorDto} instance with data from the provided
     * {@link EnrolledResponse} and a list of {@link ScoreResponse} objects.
     *
     * @param scoreResponses   a list of {@link ScoreResponse} objects containing performance metrics
     *                         and detailed scoring information.
     *                         Must not be null.
     * @param enrolledResponse the {@link EnrolledResponse} object containing information about the
     *                         competitor information in the match.
     *                         Can be null.
     */
    public void init(List<ScoreResponse> scoreResponses, EnrolledResponse enrolledResponse) {
        if (scoreResponses != null) {
            // Initializes aggregate score from multiple score responses
            this.matchPoints = BigDecimal.ZERO;
            scoreResponses.forEach(scoreResponse -> matchPoints =
                    matchPoints.add(BigDecimal.valueOf(ValueUtil.nullAsZero(scoreResponse.getFinalScore()))));

            // Don't overwrite an existing date creation timestamp
            this.dateCreated = ((this.dateCreated != null) ? this.dateCreated : LocalDateTime.now());
            // Initialises the date updated
            this.dateUpdated = LocalDateTime.now();
            // Sets the date edited to the latest score update timestamp
            this.dateEdited = scoreResponses.stream()
                    .map(ScoreResponse::getLastModified)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            // Initialises the competitor
            this.competitorCategory = CompetitorCategory.NONE;
            if (enrolledResponse != null) {
                // Initialise the competitor and match details
                this.competitorIndex = enrolledResponse.getCompetitorId();
                this.matchIndex = enrolledResponse.getMatchId();
                // TOOD: get DTOs

                // Determines the power factor based on the major power factor flag
                this.powerFactor = (enrolledResponse.getMajorPowerFactor() ? PowerFactor.MAJOR : PowerFactor.MINOR);
                // Determines the club based on the club reference number
                this.club = ClubIdentifier.getByCode(enrolledResponse.getRefNo()).orElse(ClubIdentifier.UNKNOWN);
                // Determines the discipline based on the division ID
                this.division = Division.getByCode(enrolledResponse.getDivisionId()).orElse(null);
                // Determines the firearm type from the discipline
                this.firearmType =
                        FirearmTypeToDivisions.getFirearmTypeFromDivision(this.division);
                // Determines the competitor category based on the competitor category ID
                this.competitorCategory =
                        CompetitorCategory.getByCode(enrolledResponse.getCompetitorCategoryId())
                                .orElse(CompetitorCategory.NONE);
            }
        }
    }

    /**
     * Returns a string representation of this {@code MatchCompetitorDto} object.
     *
     * <p>
     * The returned string includes the match of the match and the competitor.
     * </p>
     *
     * @return a string combining the match and the competitor associated with this object.
     */
    public String toString() {
        return this.match.toString() + ": " + this.competitor.toString();
    }
}
