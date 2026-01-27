package za.co.hpsc.web.models.award.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.Response;
import za.co.hpsc.web.models.award.request.AwardRequest;

import java.util.List;
import java.util.UUID;

/**
 * Represents a response containing award details for first, second, and third places.
 * This class extends the {@link Response} class to include information such as
 * the first, second, and third place winners.
 *
 * <p>
 * The {@code AwardResponse} class is designed to encapsulate information
 * about an award, including details of individual winners for first, second,
 * and third places.
 * The class provides multiple constructors for flexible initialisation with varying
 * levels of detail, including options to set UUID, title, description, tags,
 * and other response metadata.
 * </p>
 */
@Getter
@Setter
public class AwardResponse extends Response {
    @NotNull
    private AwardPlacing firstPlace;
    private AwardPlacing secondPlace;
    private AwardPlacing thirdPlace;

    /**
     * Constructs a new {@code AwardResponse} object with the specified award places for
     * first, second, and third positions.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Can be null.
     * @param thirdPlace  the award place representing the third position. Can be null.
     */
    public AwardResponse(@NotNull AwardPlacing firstPlace, AwardPlacing secondPlace,
                         AwardPlacing thirdPlace) {
        super();
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID and award places
     * for first, second, and third positions.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID is generated.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Can be null.
     * @param thirdPlace  the award place representing the third position. Can be null.
     */
    public AwardResponse(UUID uuid, @NotNull AwardPlacing firstPlace, AwardPlacing secondPlace,
                         AwardPlacing thirdPlace) {
        super(uuid);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID, title,
     * and award places for first, second, and third positions.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID
     *                    is generated.
     * @param title       the title of the award response. Must not be null or blank.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Can be null.
     * @param thirdPlace  the award place representing the third position. Can be null.
     */
    public AwardResponse(UUID uuid, @NotNull @NotBlank String title, @NotNull AwardPlacing firstPlace,
                         AwardPlacing secondPlace, AwardPlacing thirdPlace) {
        super(uuid, title);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified UUID, title,
     * summary, description, category, tags, and award places for first, second,
     * and third positions.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID
     *                    is generated.
     * @param title       the title of the award response. Must not be null or blank.
     * @param summary     a brief summary of the award response. Can be null.
     * @param description a detailed description of the award response. Can be null.
     * @param category    the category under which the award response is classified. Can be null.
     * @param tags        a list of tags associated with the award response. If null,
     *                    an empty list is assigned.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Can be null.
     * @param thirdPlace  the award place representing the third position. Can be null.
     */
    public AwardResponse(UUID uuid, @NotNull @NotBlank String title, String summary, String description,
                         String category, List<String> tags, AwardPlacing firstPlace,
                         AwardPlacing secondPlace, AwardPlacing thirdPlace) {
        super(uuid, title, summary, description, category, tags);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified title, summary,
     * description, category, tags, and award places for first, second, and third positions.
     *
     * <p>
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param title       the title of the award response. Must not be null or blank.
     * @param summary     a brief summary of the award response. Can be null.
     * @param description a detailed description of the award response. Can be null.
     * @param category    the category under which the award response is classified. Can be null.
     * @param tags        a list of tags associated with the award response. If null,
     *                    an empty list is assigned.
     * @param firstPlace  the award place representing the first position. Must not be null.
     * @param secondPlace the award place representing the second position. Can be null.
     * @param thirdPlace  the award place representing the third position. Can be null.
     */
    public AwardResponse(@NotNull @NotBlank String title, String summary, String description,
                         String category, List<String> tags, @NotNull AwardPlacing firstPlace,
                         AwardPlacing secondPlace, AwardPlacing thirdPlace) {
        super(title, summary, description, category, tags);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Constructs a new {@code AwardResponse} object with the specified title, summary,
     * description, category, tags, and award place details for first, second,
     * and third positions.
     *
     * <p>
     * This constructor allows setting of award place details for first, second, and third
     * place by creating {@link AwardPlacing} objects using the provided parameters
     * of names and image file paths.
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param title                    the title of the award response. Must not be null or blank.
     * @param summary                  a brief summary of the award response. Can be null.
     * @param description              a detailed description of the award response. Can be null.
     * @param category                 the category under which the award response is classified.
     *                                 Can be null.
     * @param tags                     a list of tags associated with the award response. If null,
     *                                 an empty list is assigned.
     * @param firstPlaceName           the name of the awardee for the first position. Can be null.
     * @param secondPlaceName          the name of the awardee for the second position. Can be null.
     * @param thirdPlaceName           the name of the awardee for the third position. Can be null.
     * @param firstPlaceImageFileName  the file name of the image associated with the first position.
     *                                 Can be null.
     * @param secondPlaceImageFileName the file name of the image associated with the second position.
     *                                 Can be null.
     * @param thirdPlaceImageFileName  the file name of the image associated with the third position.
     *                                 Can be null.
     */
    public AwardResponse(@NotNull @NotBlank String title, String summary, String description,
                         String category, List<String> tags, String firstPlaceName,
                         String secondPlaceName, String thirdPlaceName, String firstPlaceImageFileName,
                         String secondPlaceImageFileName, String thirdPlaceImageFileName) {
        super(title, summary, description, category, tags);
        this.firstPlace = new AwardPlacing(1, firstPlaceName, firstPlaceImageFileName);
        this.secondPlace = new AwardPlacing(2, secondPlaceName, secondPlaceImageFileName);
        this.thirdPlace = new AwardPlacing(3, thirdPlaceName, thirdPlaceImageFileName);
    }

    /**
     * Constructs a new {@code AwardResponse} object by extracting award details
     * from the provided {@code AwardRequest}.
     *
     * <p>
     * This constructor initialises the {@code AwardResponse} fields based on the
     * information encapsulated in the given {@link AwardRequest}. The attributes such as
     * title, summary, description, category, tags, file path, and file name are copied
     * from the {@link AwardRequest} instance. The award place details for first, second,
     * and third positions are also copied from the request object.
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param awardRequest the request object containing details such as title, summary,
     *                     description, category, tags, and award place details for first,
     *                     second, and third positions. Must not be null.
     */
    public AwardResponse(@NotNull AwardRequest awardRequest) {
        // Initialises response fields from request attributes
        this(awardRequest.getTitle(), awardRequest.getSummary(), awardRequest.getDescription(),
                awardRequest.getCategory(), awardRequest.getTags(),
                awardRequest.getFirstPlaceName(), awardRequest.getSecondPlaceName(),
                awardRequest.getThirdPlaceName(), awardRequest.getFirstPlaceImageFileName(),
                awardRequest.getSecondPlaceImageFileName(), awardRequest.getThirdPlaceImageFileName());
    }
}
