package za.co.hpsc.web.models.ipsc.matches;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.utils.StringUtil;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class StageRange {
    @NotNull
    private Stage stage;
    @NotNull
    private Range range;

    public StageRange() {
        this.stage = new Stage();
        this.range = new Range();
    }

    @Override
    public String toString() {
        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "stageName", stage.toString(),
                "rangeName", range.toString()
        );

        // Format and return stage name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.STAGE_RANGE_NAME_FORMAT, parameters);
    }
}
