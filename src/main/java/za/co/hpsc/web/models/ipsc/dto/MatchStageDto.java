package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.models.ipsc.response.StageResponse;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) that represents a stage in a shooting match.
 *
 * <p>
 * The {@code MatchstageDto} class is used to transfer stage-related data between various layers
 * of the application.
 * It encapsulates details such as the stage's unique identifier, associated match details,
 * stage-specific attributes such as name and number, and target-related information.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchStageDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;
    private Integer index;

    @NotNull
    private MatchDto match;

    @NotNull
    private Integer stageNumber = 0;
    private String stageName;
    private Integer rangeNumber;

    private Integer targetPaper;
    private Integer targetPopper;
    private Integer targetPlates;
    private Integer targetDisappear;
    private Integer targetPenalty;

    private Integer minRounds;
    private Integer maxPoints;

    /**
     * Constructs a new {@code MatchStageDto} instance with ata from the
     * provided {@link IpscMatchStage} entity.
     *
     * @param matchStageEntity the {@link IpscMatchStage} entity containing stage-related information,
     *                         such as the unique identifier, associated match, and stage number.
     *                         Must not be null.
     */
    public MatchStageDto(@NotNull IpscMatchStage matchStageEntity) {
        if (matchStageEntity == null) {
            return;
        }

        // Initialises the stage details
        this.id = matchStageEntity.getId();
        this.match = new MatchDto(matchStageEntity.getMatch());

        // Initialises the stage attributes
        this.stageNumber = matchStageEntity.getStageNumber();
        this.rangeNumber = matchStageEntity.getRangeNumber();

        // Initialises the target details
        this.targetPaper = matchStageEntity.getTargetPaper();
        this.targetPopper = matchStageEntity.getTargetPopper();
        this.targetPlates = matchStageEntity.getTargetPlates();
        this.targetDisappear = matchStageEntity.getTargetDisappear();
        this.targetPenalty = matchStageEntity.getTargetPenalty();

        // Initialises the max rounds and max points details
        this.minRounds = matchStageEntity.getMinRounds();
        this.maxPoints = matchStageEntity.getMaxPoints();
    }

    /**
     * Constructs a new {@code MatchStageDto} instance using the provided
     * {@link IpscMatchStage} entity and {@link MatchDto} object.
     *
     * @param matchStageEntity the {@link IpscMatchStage} entity containing stage-related information,
     *                         such as the unique identifier, stage number, and stage name.
     *                         Must not be null.
     * @param matchDto         the {@link  MatchDto} object representing the associated match.
     *                         Must not be null.
     */
    public MatchStageDto(@NotNull IpscMatchStage matchStageEntity, @NotNull MatchDto matchDto) {
        // Initialises the stage details
        this.id = matchStageEntity.getId();
        this.match = matchDto;

        // Initialises the stage attributes
        this.stageNumber = matchStageEntity.getStageNumber();
        this.stageName = matchStageEntity.getStageName();
        this.rangeNumber = matchStageEntity.getRangeNumber();

        // Initialises the target details
        this.targetPaper = matchStageEntity.getTargetPaper();
        this.targetPopper = matchStageEntity.getTargetPopper();
        this.targetPlates = matchStageEntity.getTargetPlates();
        this.targetDisappear = matchStageEntity.getTargetDisappear();
        this.targetPenalty = matchStageEntity.getTargetPenalty();

        // Initialises the max rounds and max points details
        this.minRounds = matchStageEntity.getMinRounds();
        this.maxPoints = matchStageEntity.getMaxPoints();
    }

    /**
     * Initialises the current {@code MatchStageDto} object using the provided
     * {@link MatchDto} and {@link StageResponse} objects.
     *
     * @param matchDto      the {@link MatchDto} object representing match-related information.
     *                      Must not be null.
     * @param stageResponse the {@link StageResponse} object containing stage-related information,
     *                      such as stage number, targets, and possible points.
     *                      Must not be null.
     */
    public void init(@NotNull MatchDto matchDto, @NotNull StageResponse stageResponse) {
        // Initialises the stage details
        this.index = stageResponse.getStageId();
        this.match = matchDto;

        // Initialises the stage attributes
        this.stageNumber = stageResponse.getStageId();
        this.stageName = stageResponse.getStageName();
        this.rangeNumber = 0;

        // Initialises the target details
        this.targetPaper = stageResponse.getTargetPaper();
        this.targetPopper = stageResponse.getTargetPopper();
        this.targetPlates = stageResponse.getTargetPlates();
        this.targetDisappear = stageResponse.getTargetDisappear();
        this.targetPenalty = stageResponse.getTargetPenalty();

        // Initialises the possible points details
        this.minRounds = stageResponse.getMinRounds();
        this.maxPoints = stageResponse.getMaxPoints();
    }

    /**
     * Returns a string representation of the stage of the match.
     *
     * <p>
     * The returned string includes the stage of the match, as well as the match itself.
     * </p>
     *
     * @return a string combining the stage number and the associated match information.
     */
    @Override
    public String toString() {
        return ((this.stageNumber != null) ? this.stageNumber : "0") + " for " + this.match.toString();
    }
}
