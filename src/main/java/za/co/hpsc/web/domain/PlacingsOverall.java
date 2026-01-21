package za.co.hpsc.web.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class PlacingsOverall extends Placing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal matchPoints;
    private BigDecimal matchPercentage;

    public PlacingsOverall() {
        this.matchPoints = BigDecimal.ZERO;
        this.matchPercentage = BigDecimal.ZERO;
    }
}
