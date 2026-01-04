package za.co.hpsc.web.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Abstract class representing a request model for image details,
 * primarily used for CSV-related operations.
 * <p>
 * This class encapsulates metadata about an image, including its title, summary,
 * detailed description, category, associated tags, file path, and file name.
 * It is intended to be extended by specific implementations that handle CSV-related
 * functionality for images.
 * <p>
 * The class mandates the presence of certain required fields (title, filePath, fileName)
 * that define the basic attributes of an image. Additional optional fields such as summary,
 * description, category, and tags provide further descriptive information about the image.
 * <p>
 * This class uses the {@code @JsonProperty} annotation to specify mandatory fields
 * for JSON deserialization and ensures that they are populated when an instance is created.
 * <p>
 * The {@code @JsonCreator} constructor allows for creating instances with a subset of fields,
 * specifically focusing on the required fields for minimal valid initialization.
 */
@Getter
@Setter
public abstract class ImageRequestForCsv {
    @JsonProperty(required = true)
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    @JsonProperty(required = true)
    private String filePath;
    @JsonProperty(required = true)
    private String fileName;

    /**
     * Constructs a new {@code ImageRequestForCsv} instance with the specified title,
     * file path, and file name.
     * <p>
     * This constructor is annotated with {@code @JsonCreator} to enable
     * deserialization from JSON, specifically requiring the title, file path,
     * and file name as mandatory fields. The provided details are used to
     * initialize the corresponding attributes of the object.
     * </p>
     *
     * @param title    the title of the image. This field is required.
     * @param filePath the file path where the image is stored. This field is required.
     * @param fileName the name of the file containing the image. This field is required.
     */
    @JsonCreator
    public ImageRequestForCsv(@JsonProperty(value = "title", required = true) String title,
                              @JsonProperty(value = "filePath", required = true) String filePath,
                              @JsonProperty(value = "fileName", required = true) String fileName) {
        this.title = title;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
