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
public class TagRequest {
    @NotNull
    private Integer tagId;
    @NotNull
    private String tagName;
}
