package za.co.hpsc.web.models.ipsc.request;

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
public class MatchRequest {
    @NotNull
    private Integer matchId;
    private String matchName;
    private LocalDateTime matchDate;

    private Integer squadCount;
    private Integer firearmTypeId;
    private Integer matchLevel;
    private Boolean chrono;

    private String location;
    private String countryId;

    private String matchDirector;
    private String rangeMaster;
    private String statsDirector;
}
