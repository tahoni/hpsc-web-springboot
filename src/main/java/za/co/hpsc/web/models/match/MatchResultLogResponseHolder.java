package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MatchResultLogResponseHolder {
    @NotNull
    private List<MatchResultLogResponse> matchResultLogs;
}
