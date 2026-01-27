package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationRequest {
    @NotNull
    private Integer memberId;
    @NotNull
    private Integer divisionId;

    @NotNull
    private String internationalClassificationId;
    @NotNull
    private String nationalClassificationId;
    private String scoreClassificationId;
}
