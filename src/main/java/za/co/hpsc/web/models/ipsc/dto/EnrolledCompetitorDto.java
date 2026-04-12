package za.co.hpsc.web.models.ipsc.dto;

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

// TODO: Javadoc
// TODO: tests
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
