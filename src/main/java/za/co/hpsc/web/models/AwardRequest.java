package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specialized type of request used for creating and managing award-related details.
 *
 * <p>
 * The {@code AwardRequest} class extends the functionality of the {@code Request} base class
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
 * Note: Validation constraints such as {@code @NotNull} ensure proper data initialization where required.
 */
// TODO: Javadoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardRequest extends Request {
    private LocalDateTime date;
    @NotNull
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
    private String secondPlaceImageFilePath;
    private String thirdPlaceImageFilePath;
}
