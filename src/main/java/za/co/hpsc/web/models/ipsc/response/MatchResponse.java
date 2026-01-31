package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    @NotNull
    private Integer matchId;
    private String matchName;
    private LocalDateTime matchDate;

    private Integer squadCount;
    private Integer firearmTypeId;
}
