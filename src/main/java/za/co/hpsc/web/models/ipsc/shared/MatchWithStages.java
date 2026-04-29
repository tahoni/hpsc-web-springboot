package za.co.hpsc.web.models.ipsc.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.StageResponse;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchWithStages extends MatchResponse {
    private Integer numStages;
    private List<StageResponse> stages;
}
