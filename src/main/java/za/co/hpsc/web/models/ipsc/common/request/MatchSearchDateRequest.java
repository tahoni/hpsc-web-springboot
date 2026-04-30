package za.co.hpsc.web.models.ipsc.common.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchSearchDateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String matchName;
}
