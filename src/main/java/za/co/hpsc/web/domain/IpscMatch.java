package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.MatchCategoryConverter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a match within the system, encapsulating details about the match's
 * associated club, name, scheduled date, division, category, stages, and competitors.
 *
 * <p>
 * The {@code Match} class is an entity in the persistence layer, used to store and
 * retrieve match-related data. It enables associations with other entities such as
 * {@link Club}, {@link IpscMatchStage}, and {@link MatchCompetitor}.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return a context-specific
 * representation of the match's display name.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IpscMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @NotNull
    @Column(nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Convert(converter = FirearmTypeConverter.class)
    private FirearmType matchFirearmType;
    @Convert(converter = MatchCategoryConverter.class)
    private MatchCategory matchCategory;

    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER)
    private List<IpscMatchStage> matchStages = new ArrayList<>();
    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER)
    private List<MatchCompetitor> matchCompetitors = new ArrayList<>();

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateEdited;
    private LocalDateTime dateRefreshed;

    public void init(MatchDto matchDto) {
        if (matchDto != null) {
            // Initialises the match attributes
            this.name = matchDto.getName();
            this.scheduledDate = ((matchDto.getScheduledDate() != null) ?
                    matchDto.getScheduledDate() : LocalDateTime.now());
            this.matchFirearmType = matchDto.getMatchFirearmType();
            this.matchCategory = matchDto.getMatchCategory();

            // Initialise the date fields
            this.dateEdited = matchDto.getDateEdited();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String name = ((this.name != null) ? this.name.trim() : "");
        sb.append(name).append(" ");

        String scheduledDate = DateUtil.formatDateTime(this.scheduledDate, IpscConstants.IPSC_OUTPUT_DATE_TIME_FORMAT);
        if (!scheduledDate.isEmpty()) {
            sb.append("(").append(scheduledDate).append(")");
        }

        return sb.toString().trim();
    }

    @PrePersist
    void onInsert() {
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
