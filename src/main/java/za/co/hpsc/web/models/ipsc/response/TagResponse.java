package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.TagRequest;

/**
 * Represents a response model for tag-related data in the system.
 *
 * <p>
 * This class encapsulates details of a tag, such as its ID and name. It provides functionality
 * to populate its fields directly from a {@link TagRequest} object.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    @NotNull
    private Integer tagId = 0;
    @NotNull
    private String tagName = "";

    /**
     * Constructs a new {@code TagResponse} object by initialising its fields using the values
     * from a given {@link TagRequest} object.
     *
     * @param tagRequest the {@link TagRequest}object containing data to initialise
     *                   the {@code TagResponse} instance.
     */
    public TagResponse(TagRequest tagRequest) {
        this.tagId = tagRequest.getTagId();
        this.tagName = tagRequest.getTagName();
    }
}
