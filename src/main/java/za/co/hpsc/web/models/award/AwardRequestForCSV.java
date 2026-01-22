package za.co.hpsc.web.models.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.constants.DateConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a request model for image details, primarily used for
 * CSV-related operations.
 * <p>
 * The {@code AwardRequestForCSV} abstract class encapsulates metadata about an award event,
 * including both the award ceremony and individual award details, as well as winner
 * information for first, second, and third places.
 * This class is specifically designed to handle structured data points required for
 * generating or managing CSV representations of awards.
 * The class mandates the presence of certain required fields. The title, ceremony title,
 * and details about the first, second, and third places define the basic attributes of an award.
 * Additional optional fields such as summary, description, category, and tags provide
 * further descriptive information about the award.
 * This class uses the {@code @JsonProperty} annotation to specify mandatory fields
 * for JSON deserialization and ensures that they are populated when an instance is created.
 * The {@code @JsonCreator} constructor allows for creating instances with a subset of fields,
 * specifically focusing on the required fields for minimal valid initialisation.
 * </p>
 */
@Getter
@Setter
public abstract class AwardRequestForCSV {
    @JsonProperty(required = true)
    private String ceremonyTitle;
    private String ceremonySummary;
    private String ceremonyDescription;
    private String ceremonyCategory;
    private List<String> ceremonyTags = new ArrayList<>();

    @JsonFormat(pattern = DateConstants.ISO_DATE_FORMAT)
    private LocalDate date;
    private String imageFilePath;

    @JsonProperty(required = true)
    private String title;
    private String summary;
    private String description;
    private String category;
    private List<String> tags = new ArrayList<>();

    @JsonProperty(required = true)
    private String firstPlaceName;
    private String secondPlaceName;
    private String thirdPlaceName;
    private String firstPlaceImageFileName;
    private String secondPlaceImageFileName;
    private String thirdPlaceImageFileName;

    /**
     * Constructs an instance of {@code AwardRequestForCSV} object with the specified title,
     * ceremony title, and award details for first, second, and third places.
     *
     * <p>
     * This constructor is annotated with {@code @JsonCreator} to enable deserialization
     * from JSON, specifically requiring the title, ceremony title, and details for
     * first place.
     * </p>
     *
     * @param title           the title of the award. Must not be null or blank.
     * @param ceremonyTitle   the title of the award ceremony. Must not be null or blank.
     * @param firstPlaceName  the name of the first-place winner. Must not be null or blank.
     * @param secondPlaceName the name of the second-place winner. Can be null.
     * @param thirdPlaceName  the name of the third-place winner. Can be null.
     */
    @JsonCreator
    public AwardRequestForCSV(@JsonProperty(value = "title", required = true) String title,
                              @JsonProperty(value = "ceremonyTitle", required = true) String ceremonyTitle,
                              @JsonProperty(value = "firstPlaceName", required = true) String firstPlaceName,
                              @JsonProperty(value = "secondPlaceName") String secondPlaceName,
                              @JsonProperty(value = "thirdPlaceName") String thirdPlaceName) {
        this.ceremonyTitle = ceremonyTitle;
        this.title = title;
        this.firstPlaceName = firstPlaceName;
        this.secondPlaceName = secondPlaceName;
        this.thirdPlaceName = thirdPlaceName;
    }
}
