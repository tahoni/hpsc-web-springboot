package za.co.hpsc.web.models.ipsc.matches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

@Getter
@Setter
@AllArgsConstructor
public class Range {
    private int rangeNumber;

    public Range() {
        this.rangeNumber = 0;
    }

    @Override
    public String toString() {
        return IpscConstants.RANGE_NAME_PREFIX + " " + this.rangeNumber;
    }
}
