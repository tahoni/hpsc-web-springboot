package za.co.hpsc.web.models.matches;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class MatchLogResponseHolder {
    @NotNull
    private Map<String, List<MatchLogResponse>> matchLogs;
}
