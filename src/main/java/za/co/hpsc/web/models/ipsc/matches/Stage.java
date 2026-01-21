package za.co.hpsc.web.models.ipsc.matches;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;

@Getter
@Setter
@AllArgsConstructor
public class Stage {
    private int stageNumber;

    public Stage() {
        this.stageNumber = 0;
    }

    @Override
    public String toString() {
        return IpscConstants.STAGE_NAME_PREFIX + " " + this.stageNumber;
    }
}
