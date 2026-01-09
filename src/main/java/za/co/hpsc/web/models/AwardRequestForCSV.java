package za.co.hpsc.web.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a model for capturing award-related request details to be exported or processed in CSV format.
 * <p>
 * The {@code AwardRequestForCSV} abstract class encapsulates detailed information about an award event
 * and its associated attributes. This class is specifically designed to handle structured data points
 * required for generating or managing CSV representations of awards. It supports capturing metadata for
 * both the award ceremony and individual award details, as well as winner information for first, second,
 * and third places.
 * <p>
 * Key features of this class include:
 * - Metadata about the ceremony, such as title, summary, description, category, and tags.
 * - Structured data fields for the award details, including title, summary, description, category, and tags.
 * - Information about the winners, including names and associated image file paths for first, second,
 * and third places.
 * - A constructor that mandates certain fields such as category name, title, and winner information
 * to ensure proper initialization for valid CSV representation.
 */
// TODO: Javadoc
@Getter
@Setter
public abstract class AwardRequestForCSV {
    @JsonProperty(required = true)
    private String ceremonyTitle;
    private String ceremonySummary;
    private String ceremonyDescription;
    private String ceremonyCategory;
    private List<String> ceremonyTags = new ArrayList<>();

    private LocalDateTime date;
    @JsonProperty(required = true)
    private String imageFilePath;

    @JsonProperty(required = true)
    private String title;
    private String summary;
    private String description;
    private String category;
    private List<String> tags = new ArrayList<>();

    @JsonProperty(required = true)
    private String firstPlace;
    @JsonProperty(required = true)
    private String secondPlace;
    @JsonProperty(required = true)
    private String thirdPlace;
    private String firstPlaceImageFileName;
    private String secondPlaceImageFilePath;
    private String thirdPlaceImageFilePath;

    /**
     * Constructs an instance of {@code AwardRequestForCSV} with the specified parameters.
     * This constructor initializes fields required to represent an award request for CSV processing.
     *
     * @param categoryName the name of the category associated with the award ceremony. Must not be null or blank.
     * @param title        the title of the award. Must not be null or blank.
     * @param firstPlace   the name of the first-place winner. Must not be null or blank.
     * @param secondPlace  the name of the second-place winner. Must not be null or blank.
     * @param thirdPlace   the name of the third-place winner. Must not be null or blank.
     */
    @JsonCreator
    public AwardRequestForCSV(@JsonProperty(value = "categoryName", required = true) String categoryName,
                              @JsonProperty(value = "title", required = true) String title,
                              @JsonProperty(value = "firstPlace", required = true) String firstPlace,
                              @JsonProperty(value = "secondPlace", required = true) String secondPlace,
                              @JsonProperty(value = "thirdPlace", required = true) String thirdPlace) {
        this.ceremonyCategory = categoryName;
        this.title = title;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }
}
