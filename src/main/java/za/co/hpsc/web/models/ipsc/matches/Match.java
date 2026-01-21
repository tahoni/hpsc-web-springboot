package za.co.hpsc.web.models.ipsc.matches;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.utils.StringUtil;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Match {
    @NotNull
    private Club matchClub;
    private Division matchDivision;
    private MatchCategory matchCategory;

    public Match() {
        this.matchClub = new Club();
        this.matchDivision = null;
        this.matchCategory = null;
    }

    @Override
    public String toString() {
        // Prepare parameters for formatting
        Map<String, String> parameters = Map.of(
                "clubName", matchClub.toString(),
                "divisionName", (matchDivision != null ? matchDivision.getDisplayName() : ""),
                "categoryName", (matchCategory != null ? matchCategory.getDisplayName() : "")
        );

        // Format and return match name
        return StringUtil.formatStringWithNamedParameters(IpscConstants.MATCH_NAME_FORMAT, parameters);
    }
}
