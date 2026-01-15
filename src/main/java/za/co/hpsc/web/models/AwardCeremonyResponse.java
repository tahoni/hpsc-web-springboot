package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a response object that contains details about an award ceremony.
 * This class extends the {@link Response} class to include information such as
 * the date of the ceremony, the path to a representative image, and a list of awards.
 * <p>
 * The {@code AwardCeremonyResponse} class is designed to encapsulate information
 * about an award ceremony, including details of individual awards presented during
 * the event. The class provides multiple constructors for flexible initialisation
 * with varying levels of detail, including options to set UUID, title, description,
 * tags, and other response metadata.
 * <p>
 * Key Features:
 * - Stores the ceremony date as a {@link LocalDateTime}.
 * - Holds the file path of the ceremony's representative image.
 * - Maintains a list of awards, each represented by an {@link AwardResponse}.
 * <p>
 * Default values:
 * - The image file path is initialised to an empty string.
 * - The awards list is initialised to an empty {@link ArrayList}.
 * <p>
 * Constructors:
 * - Default constructor initialises all fields to their default values.
 * - Multiple constructors allow initialising the object with varying levels
 * of data such as UUID, title, summary, category, tags, date, imageFilePath,
 * and award details.
 * - A specialised constructor accepts a list of {@link AwardRequest}
 * objects to initialise the award details.
 */
// TODO: Javadoc
@Getter
@Setter
public class AwardCeremonyResponse extends Response {
    private String date;
    @NotNull
    private String imageFilePath = "";

    @NotNull
    private List<AwardResponse> awards = new ArrayList<>();

    /**
     * Default constructor for the {@code AwardCeremonyResponse} class.
     * <p>
     * Initialises the {@code AwardCeremonyResponse} object with default values.
     * The {@code date} field is set to {@code null}, and the {@code imageFilePath}
     * field is initialised to an empty string.
     * <p>
     * This constructor delegates the initialisation of common properties to the
     * superclass {@link Response}.
     */
    public AwardCeremonyResponse() {
        super();
        this.date = null;
        this.imageFilePath = "";
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} with the specified date, image file path,
     * and list of awards.
     *
     * @param date          the date and time of the award ceremony; must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony;
     *                      if null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing individual awards;
     *                      if null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(String date, String imageFilePath, List<AwardResponse> awards) {
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} with the specified UUID, date, image file path,
     * and list of awards.
     *
     * @param uuid          the unique identifier for the award ceremony; if null, it defaults to a randomly
     *                      generated UUID through the superclass constructor.
     * @param date          the date and time of the award ceremony; must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony; if null, it defaults
     *                      to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing individual awards; if null,
     *                      it defaults to an empty list.
     */
    public AwardCeremonyResponse(UUID uuid, String date, String imageFilePath, List<AwardResponse> awards) {
        super(uuid);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} with the specified details.
     *
     * @param uuid          the unique identifier for the award ceremony; if null, it defaults to a randomly
     *                      generated UUID through the superclass constructor.
     * @param title         the title of the award ceremony; must not be null or empty.
     * @param summary       a brief summary of the award ceremony; can be null or empty.
     * @param description   a detailed description of the award ceremony; can be null or empty.
     * @param category      the category associated with the award ceremony; can be null or empty.
     * @param tags          a list of tags relevant to the award ceremony, if null, it defaults to an empty list.
     * @param date          the date and time of the award ceremony; must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony; if null, it defaults
     *                      to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing individual awards; if null,
     *                      it defaults to an empty list.
     */
    public AwardCeremonyResponse(UUID uuid, String title, String summary, String description,
                                 String category, List<String> tags, String date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(uuid, title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} with the specified unique identifier,
     * title, date, image file path, and list of awards.
     *
     * @param uuid          the unique identifier for the award ceremony; if null, it defaults to a
     *                      randomly generated UUID through the superclass constructor.
     * @param title         the title of the award ceremony; must not be null or empty.
     * @param date          the date and time of the award ceremony; must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony; if null, it
     *                      defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing individual awards;
     *                      if null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(UUID uuid, String title, String date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(uuid, title);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} with the specified title, summary, description,
     * category, tags, date, image file path, and list of awards.
     *
     * @param title         the title of the award ceremony; must not be null or empty.
     * @param summary       a brief summary of the award ceremony; can be null or empty.
     * @param description   a detailed description of the award ceremony; can be null or empty.
     * @param category      the category associated with the award ceremony; can be null or empty.
     * @param tags          a list of tags relevant to the award ceremony; if null, it defaults to an empty list.
     * @param date          the date and time of the award ceremony; must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony; if null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing individual awards; if null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(String title, String summary, String description, String category,
                                 List<String> tags, String date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs an instance of {@code AwardCeremonyResponse} using a list of {@link AwardRequest} objects.
     * The first element in the list is used to initialise the award ceremony's title, description,
     * summary, category, tags, date, and image file path. A collection of {@link AwardResponse} objects
     * is created from the provided {@link AwardRequest} list.
     *
     * @param awardRequestList a list of {@link AwardRequest} objects containing details for the award
     *                         ceremony; if null or empty, the instance will be initialised with default values.
     */
    public AwardCeremonyResponse(List<AwardRequest> awardRequestList) {
        this();
        if ((awardRequestList == null) || (awardRequestList.isEmpty())) {
            return;
        }

        // Ceremony info (initialised from the first request in the list)
        AwardRequest firstRequest = awardRequestList.getFirst();
        super.setTitle(firstRequest.getCeremonyTitle());
        super.setDescription(firstRequest.getCeremonyDescription());
        super.setSummary(firstRequest.getCeremonySummary());
        super.setCategory(firstRequest.getCeremonyCategory());
        super.setTags(firstRequest.getCeremonyTags());
        this.date = firstRequest.getDate();
        this.imageFilePath = firstRequest.getImageFilePath();

        // Awards info
        this.awards = awardRequestList.stream()
                .map(AwardResponse::new)
                .toList();
    }
}
