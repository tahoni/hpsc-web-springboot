package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.MatchCategoryConverter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ipsc_match")
public class IpscMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(nullable = false)
    private String name;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Convert(converter = FirearmTypeConverter.class)
    @Column(name = "match_firearm_type")
    private FirearmType matchFirearmType;

    @Convert(converter = MatchCategoryConverter.class)
    @Column(name = "match_category")
    private MatchCategory matchCategory;

    // A stage can't exist without its match, so its lifecycle follows the match's. Excluded from
    // Lombok's toString/equals/hashCode, since IpscMatchStage.match points straight back here.
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stageNumber")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<IpscMatchStage> stages = new ArrayList<>();

    @Column(name = "url")
    private String url;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
