package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.models.ipsc.response.StageResponse;

/**
 * Data Transfer Object (DTO) representing a specific stage in a shooting match.
 *
 * <p>
 * The {@code MatchStageDto} class encapsulates data related to an individual stage
 * in a shooting match.
 * It includes details such as the stage's unique identifier, associated match, stage number,
 * stage name, and range number.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchStageDto {
    private Long id;

    @NotNull
    private MatchDto match;

    @NotNull
    private Integer stageNumber;
    private String stageName;
    private Integer rangeNumber;

    /**
     * Constructs a new {@code MatchStageDto} instance with ata from the
     * provided {@link MatchStage} entity.
     *
     * @param matchStageEntity the {@link MatchStage} entity containing stage-related information,
     *                         such as the unique identifier, associated match, and stage number.
     *                         Must not be null.
     */
    public MatchStageDto(@NotNull MatchStage matchStageEntity) {
        this.id = matchStageEntity.getId();
        this.match = new MatchDto(matchStageEntity.getMatch());
        this.stageNumber = matchStageEntity.getStageNumber();
    }

    /**
     * Constructs a new {@code MatchStageDto} instance using the provided
     * {@link MatchStage} entity and {@link MatchDto} object.
     *
     * @param matchStageEntity the {@link MatchStage} entity containing stage-related information,
     *                         such as the unique identifier, stage number, and stage name.
     *                         Must not be null.
     * @param matchDto         the {@link  MatchDto} object representing the associated match.
     *                         Must not be null.
     */
    public MatchStageDto(@NotNull MatchStage matchStageEntity, @NotNull MatchDto matchDto) {
        this.id = matchStageEntity.getId();
        this.match = matchDto;
        this.stageNumber = matchStageEntity.getStageNumber();
        this.stageName = matchStageEntity.getStageName();
    }

    /**
     * Initialises the current {@code MatchStageDto} instance using the provided
     * {@link MatchDto} object and {@link StageResponse} object.
     *
     * @param matchDto      the {@link MatchDto} object representing the associated match.
     *                      Must not be null.
     * @param stageResponse the {@link StageResponse} object containing stage-related
     *                      data, including stage ID and stage name. Must not be null.
     */
    public void init(@NotNull MatchDto matchDto, @NotNull StageResponse stageResponse) {
        this.match = matchDto;
        this.stageNumber = stageResponse.getStageId();
        this.stageName = stageResponse.getStageName();
    }

    @Override
    public String toString() {
        return stageNumber + " for " + match.toString();
    }
}
