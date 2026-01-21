package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
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
 * by encapsulating common properties. Each request is characterised by its title,
 * with optional fields for a brief summary, detailed description, category, and
 * associated tags.
 * It provides multiple constructors to support different initialisation scenarios, allowing
 * request data to be populated with varying levels of detail.
 * </p>
 */
@Getter
@Setter
public class Request {
    @NotNull
    @NotBlank
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    /**
     * Constructs a new {@code Request} object with default values
     *
     * <p>
     * Ensures that the list of tags is not null by initialising it to an empty list.
     * </p>.
     */
    public Request() {
        this.tags = new ArrayList<>();
    }

    /**
     * Constructs a new {@code Request} object with the specified title.
     *
     * @param title the title of the request. Must not be null or blank.
     */
    public Request(@NotNull @NotBlank String title) {
        this();
        this.title = title;
    }

    /**
     * Constructs a new {@code Request} object with the specified details.
     *
     * <p>
     * This constructor initialises the fields for the title, summary, description,
     * category, and tags.
     * </p>
     *
     * @param title       the title of the request. Must not be null or blank.
     * @param summary     a brief summary of the request. Can be null.
     * @param description a detailed description of the request. Can be null.
     * @param category    the category under which the request is classified. Can be null.
     * @param tags        a list of tags associated with the request. If null,
     *                    an empty list is assigned.
     */
    public Request(@NotNull @NotBlank String title, String summary, String description,
                   String category, List<String> tags) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.tags = ValueUtil.nullAsEmptyList(tags);
    }
}
