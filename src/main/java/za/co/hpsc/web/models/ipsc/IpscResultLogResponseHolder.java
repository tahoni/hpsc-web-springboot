package za.co.hpsc.web.models.ipsc;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResultLogResponseHolder {
    @NotNull
    private List<IpscResultLogResponse> resultLogs;
}
