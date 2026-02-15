package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO: Javadoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClubMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id")
    private Club club;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id")
    private IpscMatch match;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ClubMatchCompetitor> clubCompetitors = new ArrayList<>();

    @NotNull
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    // TODO: Javadoc
    public boolean isRefreshRequired() {
        LocalDateTime dateLastUpdated = ((this.dateEdited != null) ? this.dateEdited :
                ((this.dateUpdated != null) ? this.dateUpdated : this.dateCreated));

        // If the refresh date is null, we assume that the ranking needs to be updated
        if (this.dateRefreshed == null) {
            return true;
        }
        // If the refresh date is before the last update date, we need to refresh the ranking
        return this.dateRefreshed.isBefore(dateLastUpdated);
    }

    // TODO: Javadoc
    public void refreshRankings(BigDecimal highestScore) {
        clubCompetitors.forEach(competitor -> {
            competitor.refreshRankings(highestScore);
        });
        this.dateRefreshed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return this.match.toString() + " at " + this.club.toString();
    }
}
