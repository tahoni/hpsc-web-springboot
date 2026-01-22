package za.co.hpsc.web.models.domain.placings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PlacingsOverall extends Placing {
    private BigDecimal matchPoints;
    private BigDecimal matchPercentage;

    public PlacingsOverall() {
        this.matchPoints = BigDecimal.ZERO;
        this.matchPercentage = BigDecimal.ZERO;
    }
}
