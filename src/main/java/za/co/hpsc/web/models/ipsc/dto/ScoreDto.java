package za.co.hpsc.web.models.ipsc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;
    private Integer index;

    Integer matchIndex;
    Integer stageIndex;
    Integer memberIndex;

    private String time;
    private String hitFactor;
    private Integer finalScore;

    private LocalDateTime lastModified = LocalDateTime.now();

    public ScoreDto(ScoreResponse scoreResponse) {
        if (scoreResponse != null) {
            // Initialises the score details
            this.matchIndex = scoreResponse.getMatchId();
            this.stageIndex = scoreResponse.getStageId();
            this.memberIndex = scoreResponse.getMemberId();

            // Initialises the score attributes
            this.time = scoreResponse.getTime();
            this.hitFactor = scoreResponse.getHitFactor();
            this.finalScore = scoreResponse.getFinalScore();

            // Initialises the last modified timestamp
            this.lastModified = (scoreResponse.getLastModified() != null) ?
                    scoreResponse.getLastModified() : LocalDateTime.now();
        } else {
            this.lastModified = LocalDateTime.now();
        }
    }
}
