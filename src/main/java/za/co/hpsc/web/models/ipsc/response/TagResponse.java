package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.TagRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    @NotNull
    private Integer tagId;
    @NotNull
    private String tagName;

    public TagResponse(TagRequest tag) {
        this.tagId = tag.getTagId();
        this.tagName = tag.getTagName();
    }
}
