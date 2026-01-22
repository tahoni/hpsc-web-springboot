package za.co.hpsc.web.models.domain.placings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PlacingsStage extends Placing {
    private int points;
    private int penalties;
    private BigDecimal time;
    private BigDecimal hitFactor;
    private BigDecimal stagePoints;
    private BigDecimal stagePercentage;

    public PlacingsStage() {
        super();
        this.points = 0;
        this.penalties = 0;
        this.time = BigDecimal.ZERO;
        this.hitFactor = BigDecimal.ZERO;
        this.stagePoints = BigDecimal.ZERO;
        this.stagePercentage = BigDecimal.ZERO;
    }
}
