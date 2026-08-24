package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.hpsc.web.converters.FirearmTypeConverter;
import za.co.hpsc.web.converters.PowerFactorConverter;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shooter_log")
public class ShooterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id", nullable = false)
    private Competitor competitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Convert(converter = FirearmTypeConverter.class)
    @Column(name = "firearm_type", nullable = false)
    private FirearmType firearmType;

    @Convert(converter = PowerFactorConverter.class)
    @Column(name = "power_factor", nullable = false)
    private PowerFactor powerFactor;

    @Column(name = "log_value", precision = 19, scale = 6)
    private BigDecimal logValue;

    @Column(name = "calculated_date", nullable = false)
    private LocalDateTime calculatedDate;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
}
