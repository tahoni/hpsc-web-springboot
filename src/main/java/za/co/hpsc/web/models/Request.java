package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic request containing details and metadata such as
 * title, summary, description, category, and tags.
 *
 * <p>
 * The {@code Request} class serves as a base model for various types of requests
 * by encapsulating common properties. Each request is characterized by its title,
 * with optional fields for a brief summary, detailed description, category, and
 * associated tags.
 * This class provides constructors to initialize the request object with different
 * combinations of its fields.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class Request {
    @NotNull
    @NotBlank
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags = new ArrayList<>();

    /**
     * Constructs a new {@code Request} object with the specified details.
     * This constructor initializes the fields for the title, summary,
     * description, category, and tags.
     *
     * @param title       the title of the request. Must not be null or blank.
     * @param summary     a brief summary of the request. It may be null.
     * @param description a detailed description of the request. It may be null.
     * @param category    the category under which the request is classified. It may be null.
     * @param tags        a list of tags associated with the request. If null, an empty list is assigned.
     */
    public Request(String title, String summary, String description, String category,
                   List<String> tags) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.tags = ValueUtil.nullAsEmptyList(tags);
    }

    public Request(String title) {
        this.title = title;
    }
}
