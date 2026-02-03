package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.models.ipsc.response.StageResponse;

/**
 * Represents a stage within a match, encompassing details about its associated match,
 * stage number, range number, and the competitors within the stage.
 *
 * <p>
 * The {@code MatchStage} class is an entity in the persistence layer, used to store and
 * retrieve information regarding individual stages of a match. Each stage is uniquely
 * identified and linked to a specific match.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return a context-specific
 * representation of the stage.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// TOOO: fix Javadoc
public class MatchStageDto {
    private Long id;

    @NotNull
    private MatchDto match;

    @NotNull
    private Integer stageNumber;
    private String stageName;
    private Integer rangeNumber;

    public MatchStageDto(MatchStage matchStage, MatchDto matchDto) {
        this.id = matchStage.getId();
        this.match = matchDto;
        this.stageNumber = matchStage.getStageNumber();
        this.stageName = matchStage.getStageName();
    }

    public void init(@NotNull MatchDto match, @NotNull StageResponse stageResponse) {
        this.match = match;
        this.stageNumber = stageResponse.getStageId();
        this.stageName = stageResponse.getStageName();
    }

    @Override
    public String toString() {
        return stageNumber + " for " + match.toString();
    }
}
