package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a request to provide details and metadata for an image,
 * including its title, summary, description, category, tags, file path,
 * and file name.
 * <p>
 * The {@code ImageRequest} class serves as a data model for encapsulating
 * information required to describe an image resource. Each image is
 * characterized by its title, a brief summary, an optional detailed
 * description, a category it belongs to, a list of associated tags,
 * and the path and name of its file.
 * <p>
 * This class provides both a no-arguments constructor and a parameterized
 * constructor to initialize its fields.
 */
@Getter
@Setter
public class ImageRequest {
    @NotNull
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags = new ArrayList<>();

    @NotNull
    private String filePath;
    @NotNull
    private String fileName;

    /**
     * Constructs a new {@code ImageRequest} object with the specified title, file path, and file name.
     *
     * @param title    the title of the image.
     * @param filePath the file path where the image is stored.
     * @param fileName the name of the file containing the image.
     */
    public ImageRequest(String title, String filePath, String fileName) {
        this.title = title;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     * Constructs a new {@code ImageRequest} object with the specified details.
     * This constructor initializes the fields for the title, summary, description,
     * category, tags, file path, and file name.
     *
     * @param title       the title of the image.
     * @param summary     a brief summary of the image.
     * @param description a detailed description of the image.
     * @param category    the category under which the image is classified.
     * @param tags        a list of tags associated with the image.
     * @param filePath    the file path where the image is stored.
     * @param fileName    the name of the file containing the image.
     */
    public ImageRequest(String title, String summary, String description, String category,
                        List<String> tags, String filePath, String fileName) {
        this.title = title;
        this.summary = summary;
        this.description = description;

        this.category = category;
        this.tags = tags;

        this.filePath = filePath;
        this.fileName = fileName;
    }
}
