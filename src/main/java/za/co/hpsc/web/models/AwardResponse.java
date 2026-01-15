package za.co.hpsc.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Represents a response containing award details for first, second, and third places.
 *
 * <ul>
 * Key features of this class include:
 * - Support for managing details of the top three awards.
 * - Nested static class {@code AwardPlace}, representing individual award details.
 * - Multiple constructors for initialising response data with varying levels of detail,
 *   including award names, image file paths, and metadata from associated requests.
 * <p>
 * The {@code AwardResponse} class extends the {@link Response} class, inheriting
 * core response features such as UUID-based identification and base request properties.
 */
// TODO: Javadoc
@Getter
@Setter
public class AwardResponse extends Response {
    private AwardPlace firstPlace;
    private AwardPlace secondPlace;
    private AwardPlace thirdPlace;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AwardPlace {
        private int place;
        private String name;
        private String imageFilePath;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified award places
     * for first, second, and third positions.
     *
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Must not be null.
     * @param thirdPlace  the award place representing the third position. Must not be null.
     */
    public AwardResponse(AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super();
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID
     * and award places for first, second, and third positions.
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID is generated.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Must not be null.
     * @param thirdPlace  the award place representing the third position. Must not be null.
     */
    public AwardResponse(UUID uuid, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(uuid);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID, title, summary, description,
     * category, tags, and award places for first, second, and third positions.
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID is generated.
     * @param title       the title of the award response. Must not be null or blank.
     * @param summary     a brief summary of the award response. It may be null.
     * @param description a detailed description of the award response. It may be null.
     * @param category    the category under which the award response is classified. It may be null.
     * @param tags        a list of tags associated with the award response. If null, an empty list is assigned.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Must not be null.
     * @param thirdPlace  the award place representing the third position. Must not be null.
     */
    public AwardResponse(UUID uuid, String title, String summary, String description, String category,
                         List<String> tags, AwardPlace firstPlace, AwardPlace secondPlace,
                         AwardPlace thirdPlace) {
        super(uuid, title, summary, description, category, tags);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID, title, and award places
     * for first, second, and third positions.
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID is generated.
     * @param title       the title of the award response. Must not be null or blank.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Must not be null.
     * @param thirdPlace  the award place representing the third position. Must not be null.
     */
    public AwardResponse(UUID uuid, String title, AwardPlace firstPlace, AwardPlace secondPlace,
                         AwardPlace thirdPlace) {
        super(uuid, title);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(String title, String summary, String description, String category,
                         List<String> tags, AwardPlace firstPlace, AwardPlace secondPlace,
                         AwardPlace thirdPlace) {
        super(title, summary, description, category, tags);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(String title, String summary, String description,
                         String category, List<String> tags,
                         String firstPlaceName, String secondPlaceName, String thirdPlaceName,
                         String firstPlaceImageFileName, String secondPlaceImageFileName,
                         String thirdPlaceImageFileName) {
        super(title, summary, description, category, tags);
        this.firstPlace = new AwardPlace(1, firstPlaceName, firstPlaceImageFileName);
        this.secondPlace = new AwardPlace(2, secondPlaceName, secondPlaceImageFileName);
        this.thirdPlace = new AwardPlace(3, thirdPlaceName, thirdPlaceImageFileName);
    }

    public AwardResponse(AwardRequest awardRequest) {
        this(awardRequest.getTitle(), awardRequest.getSummary(), awardRequest.getDescription(),
                awardRequest.getCategory(), awardRequest.getTags(),
                awardRequest.getFirstPlaceName(), awardRequest.getSecondPlaceName(),
                awardRequest.getThirdPlaceName(), awardRequest.getFirstPlaceImageFileName(),
                awardRequest.getSecondPlaceImageFileName(), awardRequest.getThirdPlaceImageFileName());
    }
}
