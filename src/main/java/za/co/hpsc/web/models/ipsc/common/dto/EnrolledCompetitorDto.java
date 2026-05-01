package za.co.hpsc.web.models.ipsc.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a competitor enrolled in a match.
 *
 * <p>
 * This class aggregates a competitor's identity, club affiliation, category, firearm type,
 * division, and power factor, along with their associated match and match stage results.
 * It can be constructed from various entity and DTO types to facilitate mapping between
 * domain objects and the transfer layer.
 * </p>
 *
 * <p>
 * When constructed from a {@link MatchCompetitor} or {@link MatchStageCompetitor} entity,
 * the fields are populated from the entity's data. When constructed from a
 * {@link MatchCompetitorDto}, the fields are populated directly from the DTO.
 * If the provided argument is {@code null}, all fields retain their default values.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCompetitorDto {
    private UUID uuid = UUID.randomUUID();

    private transient Integer competitorIndex;

    @NotNull
    private CompetitorDto competitor;
    @NotNull
    private List<MatchCompetitorDto> competitorMatches = new ArrayList<>();
    @NotNull
    private List<MatchStageCompetitorDto> competitorMatchStages = new ArrayList<>();
    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    private ClubIdentifier club;
    private FirearmType firearmType;
    private Division division;
    private PowerFactor powerFactor;

    /**
     * Constructs an {@code EnrolledCompetitorDto} from a {@link MatchCompetitor} entity.
     *
     * <p>
     * Populates the competitor details, club affiliation, competitor category, firearm type,
     * division, and power factor from the provided entity. If {@code matchCompetitorEntity}
     * is {@code null}, all fields retain their default values.
     * </p>
     *
     * @param matchCompetitorEntity the {@link MatchCompetitor} entity to map from, or
     *                              {@code null} to create an instance with default values.
     */
    public EnrolledCompetitorDto(MatchCompetitor matchCompetitorEntity) {
        if (matchCompetitorEntity != null) {
            // Initialises the match competitor details
            this.competitor = new CompetitorDto(matchCompetitorEntity.getCompetitor());

            // Initialises the club details
            this.club = matchCompetitorEntity.getMatchClub();

            // Initialises the competitor attributes
            this.competitorCategory = matchCompetitorEntity.getCompetitorCategory();
            this.firearmType = matchCompetitorEntity.getFirearmType();
            this.division = matchCompetitorEntity.getDivision();
            this.powerFactor = matchCompetitorEntity.getPowerFactor();
        }
    }

    /**
     * Constructs an {@code EnrolledCompetitorDto} from a {@link MatchStageCompetitor} entity.
     *
     * <p>
     * Populates the competitor details, club affiliation, competitor category, firearm type,
     * division, and power factor from the provided entity. If {@code matchStageCompetitorEntity}
     * is {@code null}, all fields retain their default values.
     * </p>
     *
     * @param matchStageCompetitorEntity the {@link MatchStageCompetitor} entity to map from,
     *                                   or {@code null} to create an instance with default values.
     */
    public EnrolledCompetitorDto(MatchStageCompetitor matchStageCompetitorEntity) {
        if (matchStageCompetitorEntity != null) {
            // Initialises the match competitor details
            this.competitor = new CompetitorDto(matchStageCompetitorEntity.getCompetitor());

            // Initialises the club details
            this.club = matchStageCompetitorEntity.getMatchClub();

            // Initialises the competitor attributes
            this.competitorCategory = matchStageCompetitorEntity.getCompetitorCategory();
            this.firearmType = matchStageCompetitorEntity.getFirearmType();
            this.division = matchStageCompetitorEntity.getDivision();
            this.powerFactor = matchStageCompetitorEntity.getPowerFactor();
        }
    }

    /**
     * Constructs an {@code EnrolledCompetitorDto} from a {@link MatchCompetitorDto}.
     *
     * <p>
     * Populates the competitor details, club affiliation, competitor category, firearm type,
     * division, and power factor directly from the provided DTO. If {@code matchCompetitorDto}
     * is {@code null}, all fields retain their default values.
     * </p>
     *
     * @param matchCompetitorDto the {@link MatchCompetitorDto} to map from, or {@code null}
     *                           to create an instance with default values.
     */
    public EnrolledCompetitorDto(MatchCompetitorDto matchCompetitorDto) {
        if (matchCompetitorDto != null) {
            // Initialises the match competitor details
            this.competitor = matchCompetitorDto.getCompetitor();

            // Initialises the club details
            this.club = matchCompetitorDto.getClub();

            // Initialises the competitor attributes
            this.competitorCategory = matchCompetitorDto.getCompetitorCategory();
            this.firearmType = matchCompetitorDto.getFirearmType();
            this.division = matchCompetitorDto.getDivision();
            this.powerFactor = matchCompetitorDto.getPowerFactor();
        }
    }
}
