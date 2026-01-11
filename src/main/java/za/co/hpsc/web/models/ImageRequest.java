package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.List;

/**
 * A specialised request class that encapsulates additional information for image-related requests.
 * The {@code ImageRequest} class extends {@link Request} and introduces properties for
 * specifying the file path and file name associated with the image. It provides constructors
 * for initialising an image request with basic or detailed metadata.
 */
@Getter
@Setter
@NoArgsConstructor
public class ImageRequest extends Request {
    @NotNull
    private String filePath;
    @NotNull
    private String fileName;

    /**
     * Constructs a new {@code ImageRequest} object with the specified title,
     * file path, and file name.
     * This constructor initialises the image request by setting the title using the superclass
     * constructor and handling null values for file path and file name by replacing them
     * with empty strings.
     *
     * @param title    the title of the image request. Must not be null or blank.
     * @param filePath the file path where the image is stored. If null,
     *                 it will be replaced with an empty string.
     * @param fileName the name of the file containing the image. If null,
     *                 it will be replaced with an empty string.
     */
    public ImageRequest(@NotNull @NotBlank String title, String filePath, String fileName) {
        super(title);
        this.filePath = ValueUtil.nullAsEmptyString(filePath);
        this.fileName = ValueUtil.nullAsEmptyString(fileName);
    }

    /**
     * Constructs a new {@code ImageRequest} object with the specified details.
     * This constructor initialises all the fields required for an image-related request
     * by using the parent class constructor to set common properties and handling null
     * values for image-specific properties using default substitutions.
     *
     * @param title       the title of the image request. Must not be null or blank.
     * @param summary     a brief summary of the request. It may be null.
     * @param description a detailed description of the request. It may be null.
     * @param category    the category under which the image request is classified. It may be null.
     * @param tags        a list of tags associated with the image request. If null,
     *                    an empty list is assigned.
     * @param filePath    the file path where the image is stored. If null,
     *                    it will be replaced with an empty string.
     * @param fileName    the name of the file containing the image. If null,
     *                    it will be replaced with an empty string.
     */
    public ImageRequest(@NotNull @NotBlank String title, String summary, String description,
                        String category, List<String> tags, String filePath, String fileName) {
        super(title, summary, description, category, tags);
        this.filePath = ValueUtil.nullAsEmptyString(filePath);
        this.fileName = ValueUtil.nullAsEmptyString(fileName);
    }
}
