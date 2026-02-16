package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.utils.NumberUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Javadoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClubMatchCompetitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_match_id")
    private ClubMatch clubMatch;

    @Enumerated(EnumType.STRING)
    private ClubIdentifier clubName;
    @Enumerated(EnumType.STRING)
    private FirearmType firearmType;
    @Enumerated(EnumType.STRING)
    private Division division;
    @Enumerated(EnumType.STRING)
    private PowerFactor powerFactor;

    private BigDecimal clubPoints;
    private BigDecimal clubRanking;

    @Enumerated(EnumType.STRING)
    private CompetitorCategory competitorCategory = CompetitorCategory.NONE;

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateRefreshed;

    public ClubMatchCompetitor(ClubMatch clubMatchEntity, MatchCompetitor matchCompetitorEntity) {
        // Initialises the club and match details
        this.clubMatch = clubMatchEntity;
        // Initialises the competitor details
        this.competitor = matchCompetitorEntity.getCompetitor();

        // Initialises the competitor attributes
        this.clubName = matchCompetitorEntity.getClubName();
        this.competitorCategory = matchCompetitorEntity.getCompetitorCategory();
        this.firearmType = matchCompetitorEntity.getFirearmType();
        this.division = matchCompetitorEntity.getDivision();
        this.powerFactor = matchCompetitorEntity.getPowerFactor();

        // Initialises the date fields
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = LocalDateTime.now();
    }

    public void refreshRankings(BigDecimal highestScore) {
        // Refresh the match points
        List<BigDecimal> matchPointList = this.clubMatch.getMatch().getMatchCompetitors().stream()
                .filter(matchCompetitor -> this.competitor.getId().equals(matchCompetitor.getCompetitor().getId()))
                .map(MatchCompetitor::getMatchPoints)
                .toList();
        this.clubPoints = NumberUtil.calculateSum(matchPointList);
        // Refresh the club ranking for the match
        this.clubRanking = NumberUtil.calculatePercentage(clubPoints, highestScore);
        this.dateRefreshed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return this.clubMatch.toString() + ": " + this.competitor.toString();
    }
}
