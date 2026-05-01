package za.co.hpsc.web.models.ipsc.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchSearchRequest {
    private String[] matchId;
    private String matchName;
    private LocalDate startDate;
    private LocalDate endDate;
}
