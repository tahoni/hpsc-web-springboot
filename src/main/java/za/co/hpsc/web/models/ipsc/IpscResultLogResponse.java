package za.co.hpsc.web.models.ipsc;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.Response;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpscResultLogResponse extends Response {
    @NotNull
    private IpscResultResponse result = new IpscResultResponse();
    @NotNull
    private IpscLogResponse log = new IpscLogResponse();
}
