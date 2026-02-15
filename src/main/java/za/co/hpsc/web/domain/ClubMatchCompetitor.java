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
    private ClubIdentifier clubIdentifier;
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
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    // TODO: Javadoc
    public void refreshRankings(BigDecimal highestScore) {
        this.clubRanking = NumberUtil.calculatePercentage(clubPoints, highestScore);
        this.dateRefreshed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return this.clubMatch.toString() + ": " + this.competitor.toString();
    }
}
