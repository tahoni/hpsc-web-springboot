package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.MatchCategoryConverter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;

import java.time.LocalDateTime;

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

    @Convert(converter = FirearmTypeConverter.class)
    @Column(name = "match_firearm_type")
    private FirearmType matchFirearmType;

    @Convert(converter = MatchCategoryConverter.class)
    @Column(name = "match_category")
    private MatchCategory matchCategory;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
