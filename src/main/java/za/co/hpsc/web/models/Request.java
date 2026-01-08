package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags = new ArrayList<>();

    public Request(String title, String summary, String description, String category,
                   List<String> tags) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.tags = (tags != null ? tags : new ArrayList<>());
    }

    public Request(String title) {
        this.title = title;
    }
}
