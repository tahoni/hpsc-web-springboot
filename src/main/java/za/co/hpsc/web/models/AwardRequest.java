package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specialised type of request used for creating and managing award-related details.
 *
 * <p>
 * The {@code AwardRequest} class extends the functionality of the {@link Request} base class
 * by introducing additional fields specific to awards. These include information about
 * the award ceremony, associated media files, and the names of winners for first, second,
 * and third places. This class is designed to handle structured data required for award submissions
 * or processing purposes.
 * </p>
 *
 * <ul>
 * Key features of this class include:
 * - Tracking the date and time of the award-related event.
 * - Storing file paths for images associated with the event or winners.
 * - Capturing detailed metadata about the ceremony, such as title, summary, description, category, and tags.
 * - Maintaining names of individuals or entities for first, second, and third-place awards,
 *   along with specific image file paths for each winner.
 * </ul>
 * <p>
 * Note: Validation constraints such as {@code @NotNull} ensure proper data initialisation where required.
 */
// TODO: Javadoc
@Getter
@Setter
public class AwardRequest extends Request {
    private String date;
    private String imageFilePath;

    @NotNull
    private String ceremonyTitle;
    private String ceremonySummary;
    private String ceremonyDescription;
    private String ceremonyCategory;
    private List<String> ceremonyTags = new ArrayList<>();

    @NotNull
    private String firstPlaceName;
    @NotNull
    private String secondPlaceName;
    @NotNull
    private String thirdPlaceName;

    private String firstPlaceImageFileName;
    private String secondPlaceImageFileName;
    private String thirdPlaceImageFileName;

    /**
     * Constructs a new {@code AwardRequest} object with the specified details.
     * This constructor initializes fields specific to an award, such as the title of the award,
     * the ceremony title, and the names of recipients for first, second, and third places.
     *
     * @param title           the title of the award. Must not be null or blank.
     * @param ceremonyTitle   the title of the ceremony associated with the award. It may be null.
     * @param firstPlaceName  the name of the first-place recipient. It may be null.
     * @param secondPlaceName the name of the second-place recipient. It may be null.
     * @param thirdPlaceName  the name of the third-place recipient. It may be null.
     */
    public AwardRequest(@NotNull @NotBlank String title, String ceremonyTitle, String firstPlaceName,
                        String secondPlaceName, String thirdPlaceName) {
        super(title);
        this.firstPlaceName = firstPlaceName;
        this.secondPlaceName = secondPlaceName;
        this.thirdPlaceName = thirdPlaceName;
        this.ceremonyTitle = ceremonyTitle;
    }
}
