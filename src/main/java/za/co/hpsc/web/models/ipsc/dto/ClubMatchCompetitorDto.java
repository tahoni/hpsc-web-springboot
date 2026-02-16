package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.ClubMatchCompetitor;
import za.co.hpsc.web.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the association between a competitor and a club match.
 *
 * <p>
 * The {@code ClubMatchCompetitorDto} class encapsulates data related to a competitor's
 * participation in a specific club match.
 * It includes firearm type, discipline, power factor, and scoring details.
 * Additionally, it holds references to the associated competitor and club match entities.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubMatchCompetitorDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;

    @NotNull
    private CompetitorDto competitor;

    private ClubIdentifier clubName;
    private FirearmType firearmType;
    private Division division;
    private PowerFactor powerFactor;

    private BigDecimal clubPoints;
    private BigDecimal clubRanking;

    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateRefreshed;

    /**
     * Constructs a new {@code ClubMatchCompetitorDto} instance with data from the
     * provided {@link ClubMatchCompetitor} entity.
     *
     * @param clubMatchCompetitorEntity the {@link ClubMatchCompetitor} entity containing information
     *                                  about a competitor's participation in a specific club match.
     *                                  Must not be null.
     */
    public ClubMatchCompetitorDto(@NotNull ClubMatchCompetitor clubMatchCompetitorEntity) {
        // Initialises the club match competitor details
        this.id = clubMatchCompetitorEntity.getId();
        this.competitor = new CompetitorDto(clubMatchCompetitorEntity.getCompetitor());

        // Initialises the competitor attributes
        this.clubName = clubMatchCompetitorEntity.getClubName();
        this.competitorCategory = clubMatchCompetitorEntity.getCompetitorCategory();
        this.firearmType = clubMatchCompetitorEntity.getFirearmType();
        this.division = clubMatchCompetitorEntity.getDivision();
        this.powerFactor = clubMatchCompetitorEntity.getPowerFactor();

        // Initialises the competitor scoring details
        this.clubPoints = clubMatchCompetitorEntity.getClubPoints();
        this.clubRanking = clubMatchCompetitorEntity.getClubRanking();

        // Initialises the date fields
        this.dateCreated = clubMatchCompetitorEntity.getDateCreated();
        this.dateUpdated = clubMatchCompetitorEntity.getDateUpdated();
        this.dateRefreshed = clubMatchCompetitorEntity.getDateRefreshed();
    }

    /**
     * Returns a string representation of this {@code ClubMatchCompetitorDto} object.
     *
     * @return a string combining the club match and the competitor associated with this object.
     */
    @Override
    public String toString() {
        return "ClubMatchCompetitor: " + this.competitor.toString();
    }
}
