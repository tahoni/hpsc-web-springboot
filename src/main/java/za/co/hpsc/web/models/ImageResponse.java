package za.co.hpsc.web.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a response for an image resource, extending the {@code ImageRequest}
 * to include additional metadata such as a unique identifier and MIME type.
 * <p>
 * The {@code ImageResponse} class is designed to encapsulate details of an image resource
 * along with a generated or provided UUID and its inferred or explicitly provided MIME type.
 * This class also provides constructors for creating instances with various levels of
 * initialization and methods to handle MIME type determination.
 * <p>
 * Key functionalities include:
 * - Generating a unique identifier if not explicitly provided.
 * - Setting or inferring the MIME type based on the file name or provided value.
 * - Compatibility with {@code ImageRequest} objects for seamless conversions between
 * request and response representations of an image.
 */
public class ImageResponse extends ImageRequest {
    @Getter
    @Setter
    private UUID id;
    @Getter
    private String mimeType = "";

    /**
     * Constructs a new {@code ImageResponse} object with specified details.
     * This constructor initializes the title, file path, file name, and MIME type fields.
     * It also assigns a unique identifier if none is provided.
     *
     * @param title    the title of the image.
     * @param filePath the file path where the image is stored.
     * @param fileName the name of the file containing the image.
     * @param id       a unique identifier for the image response; if {@code null}, a new UUID will be generated.
     * @param mimeType the MIME type of the image; if {@code null} or blank, it will be inferred from the file name.
     */
    public ImageResponse(String title, String filePath, String fileName, UUID id, String mimeType) {
        super(title, filePath, fileName);
        this.id = ((id != null) ? id : UUID.randomUUID());
        setMimeType(mimeType);
    }

    /**
     * Constructs a new {@code ImageResponse} object with the specified details,
     * including title, summary, description, category, tags, file path, file name,
     * an optional unique identifier, and MIME type. If the provided identifier
     * is {@code null}, a new random UUID is generated. The MIME type is either set
     * from the given value or determined based on the file name if no valid value
     * is provided.
     *
     * @param title       the title of the image.
     * @param summary     a brief summary of the image.
     * @param description a detailed description of the image.
     * @param category    the category to which the image belongs.
     * @param tags        a list of tags associated with the image.
     * @param filePath    the file path where the image is stored.
     * @param fileName    the name of the file containing the image.
     * @param id          a unique identifier for the image response; if {@code null},
     *                    a new UUID will be generated.
     * @param mimeType    the MIME type of the image; if {@code null} or blank,
     *                    it will be inferred from the file name.
     */
    public ImageResponse(String title, String summary, String description, String category,
                         List<String> tags, String filePath, String fileName, UUID id, String mimeType) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = ((id != null) ? id : UUID.randomUUID());
        setMimeType(mimeType);
    }

    /**
     * Constructs a new {@code ImageResponse} object with the specified details.
     * This constructor initializes the title, summary, description, category, tags,
     * file path, and file name fields, while also generating a unique identifier
     * and setting the MIME type for the image.
     *
     * @param title       the title of the image.
     * @param summary     a brief summary of the image.
     * @param description a detailed description of the image.
     * @param category    the category under which the image is classified.
     * @param tags        a list of tags associated with the image.
     * @param filePath    the file path where the image is stored.
     * @param fileName    the name of the file containing the image.
     */
    public ImageResponse(String title, String summary, String description, String category,
                         List<String> tags, String filePath, String fileName) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = UUID.randomUUID();
        setMimeType();
    }

    /**
     * Constructs a new {@code ImageResponse} instance using the details provided in the given
     * {@code ImageRequest} object. The constructor extracts necessary information such as the title,
     * summary, description, category, tags, file path, and file name from the {@code ImageRequest}
     * and initializes the corresponding fields in the {@code ImageResponse}. A unique identifier is
     * generated, if applicable, and the MIME type is set for the image.
     *
     * @param imageRequest the {@code ImageRequest} object containing the details to initialize
     *                     the image response; must include title, summary, description, category,
     *                     tags, file path, and file name.
     */
    public ImageResponse(ImageRequest imageRequest) {
        this(imageRequest.getTitle(), imageRequest.getSummary(), imageRequest.getDescription(),
                imageRequest.getCategory(), imageRequest.getTags(), imageRequest.getFilePath(),
                imageRequest.getFileName());
        setMimeType();
    }

    /**
     * Sets the MIME type for the current image response instance.
     * If the provided MIME type is null or blank, it attempts to determine the MIME type
     * based on the file name using {@code MediaTypeFactory}.
     *
     * @param mimeType the MIME type to be set; if null or blank, an attempt will be made
     *                 to infer it from the file name.
     */
    public void setMimeType(String mimeType) {
        if ((mimeType != null) && (!mimeType.isBlank())) {
            this.mimeType = mimeType;
        } else {
            if (this.getFileName() != null) {
                Optional<MediaType> optionalMediaType =
                        MediaTypeFactory.getMediaType(this.getFileName());
                optionalMediaType.ifPresent(mediaType ->
                        this.mimeType = mediaType.toString());
            }
        }
    }

    /**
     * Sets the MIME type for the current image response instance.
     * This method delegates to {@link #setMimeType(String)} with a null argument.
     * As a result, it attempts to determine the MIME type based on the file name
     * if no specific MIME type is provided.
     */
    public void setMimeType() {
        setMimeType(null);
    }
}
