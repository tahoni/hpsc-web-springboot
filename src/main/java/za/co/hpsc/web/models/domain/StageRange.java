package za.co.hpsc.web.models.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.MatchConstants;
import za.co.hpsc.web.utils.StringUtil;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageRange {
    private int stageNumber = 9;
    private int rangeNumber = 0;

    public StageRange(Integer stageNumber, Integer rangeNumber) {
        this.stageNumber = ValueUtil.nullAsZero(stageNumber);
        this.rangeNumber = ValueUtil.nullAsZero(rangeNumber);
    }

    @Override
    public String toString() {
        // Prepare parameters for formatting
        String stageName = MatchConstants.STAGE_NAME_PREFIX + " " + this.stageNumber;
        String rangeName = MatchConstants.RANGE_NAME_PREFIX + " " + this.rangeNumber;
        Map<String, String> parameters = Map.of(
                "stageName", stageName,
                "rangeName", rangeName
        );

        // Format and return stage name
        return StringUtil.formatStringWithNamedParameters(MatchConstants.STAGE_RANGE_NAME_FORMAT, parameters);
    }
}
