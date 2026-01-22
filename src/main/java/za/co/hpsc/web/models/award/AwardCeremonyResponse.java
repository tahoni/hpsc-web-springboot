package za.co.hpsc.web.models.award;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.Response;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a response object that contains details about an award ceremony.
 * This class extends the {@link Response} class to include information such as
 * the date of the ceremony, the path to representative images, and a list of awards.
 * <p>
 * The {@code AwardCeremonyResponse} class is designed to encapsulate information
 * about an award ceremony, including details of individual awards presented during
 * the event. The class provides multiple constructors for flexible initialisation
 * with varying levels of detail, including options to set UUID, title, description,
 * tags, and other response metadata.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class AwardCeremonyResponse extends Response {
    private String date;
    @NotNull
    private String imageFilePath = "";

    @NotNull
    private List<AwardResponse> awards = new ArrayList<>();

    /**
     * Constructs a new {@code AwardCeremonyResponse} object with the specified date,
     * image file path, and list of awards.
     *
     * <p>
     * This constructor initialises the fields and ensures that the
     * file path is not null by substituting an empty string for null values.
     * Also ensures that the list of awards is not null by substituting an empty list
     * for null values.
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param date          the date and time of the award ceremony. Must not be null.
     * @param imageFilePath the file path to the images associated with the award ceremony.
     *                      If null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing
     *                      individual awards. If null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(String date, String imageFilePath, List<AwardResponse> awards) {
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs a new {@code AwardCeremonyResponse} object with the specified UUID, date,
     * image file path, and list of awards.
     *
     * <p>
     * This constructor initialises the fields and ensures that the
     * file path is not null by substituting an empty string for null values.
     * Also ensures that the list of awards is not null by substituting an empty list
     * for null values.
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid          the unique identifier for the award ceremony. If null,
     *                      it defaults to a randomly generated UUID through
     *                      the superclass constructor.
     * @param date          the date and time of the award ceremony. Must not be null.
     * @param imageFilePath the file path to the images associated with the award ceremony.
     *                      If null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing
     *                      individual awards. If null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(UUID uuid, String date, String imageFilePath, List<AwardResponse> awards) {
        super(uuid);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs a new {@code AwardCeremonyResponse} object with the specified unique
     * identifier, title, date, image file path, and list of awards.
     *
     * <p>
     * This constructor initialises the fields and ensures that the
     * file path is not null by substituting an empty string for null values.
     * Also ensures that the list of awards is not null by substituting an empty list
     * for null values.
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid          the unique identifier for the award ceremony. If null,
     *                      it defaults to a randomly generated UUID through
     *                      the superclass constructor.
     * @param title         the title of the award ceremony. Must not be null or empty.
     * @param date          the date and time of the award ceremony. Must not be null.
     * @param imageFilePath the file path to the image associated with the award ceremony.
     *                      If null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing
     *                      individual awards. If null, it defaults to an empty list.
     */
    public AwardCeremonyResponse(UUID uuid, String title, String date, String imageFilePath,
                                 List<AwardResponse> awards) {
        super(uuid, title);
        this.date = date;
        this.imageFilePath = ValueUtil.nullAsEmptyString(imageFilePath);
        this.awards = ValueUtil.nullAsEmptyList(awards);
    }

    /**
     * Constructs a new {@code AwardCeremonyResponse} object with the specified details.
     *
     * <p>
     * This constructor initialises the fields and ensures that the
     * file path is not null by substituting an empty string for null values.
     * Also ensures that the list of awards is not null by substituting an empty list
     * for null values.
     * A randomly generated UUID is assigned through the superclass constructor
     * if the provided UUID is null.
     * </p>
     *
     * @param uuid          the unique identifier for the award ceremony. If null,
     *                      it defaults to a randomly generated UUID through
     *                      the superclass constructor.
     * @param title         the title of the award ceremony. Must not be null or empty.
     * @param summary       a brief summary of the award ceremony. Can be null or empty.
     * @param description   a detailed description of the award ceremony. Can be null or empty.
     * @param category      the category associated with the award ceremony. Can be null or empty.
     * @param tags          a list of tags relevant to the award ceremony. If null,
     *                      it defaults to an empty list.
     * @param date          the date and time of the award ceremony. Must not be null.
     * @param imageFilePath the file path to the images associated with the award ceremony.
     *                      If null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing
     *                      individual awards. If null, it defaults to an empty list.
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
     * Constructs new {@code AwardCeremonyResponse} object with the specified title,
     * summary, description, category, tags, date, image file path, and list of awards.
     *
     * <p>
     * This constructor initialises the fields and ensures that the
     * file path is not null by substituting an empty string for null values.
     * Also ensures that the list of awards is not null by substituting an empty list
     * for null values.
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param title         the title of the award ceremony. Must not be null or empty.
     * @param summary       a brief summary of the award ceremony. Can be null or empty.
     * @param description   a detailed description of the award ceremony. Can be null or empty.
     * @param category      the category associated with the award ceremony. Can be null or empty.
     * @param tags          a list of tags relevant to the award ceremony. If null,
     *                      it defaults to an empty list.
     * @param date          the date and time of the award ceremony. Must not be null.
     * @param imageFilePath the file path to the images associated with the award ceremony.
     *                      If null, it defaults to an empty string.
     * @param awards        the list of {@link AwardResponse} objects representing
     *                      individual awards. If null, it defaults to an empty list.
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
     * Constructs a new {@code AwardCeremonyResponse} object using a list
     * of {@link AwardRequest} objects.
     *
     * <p>
     * The first element in the list is used to initialise the award ceremony's
     * title, description, summary, category, tags, date, and image file path.
     * A collection of {@link AwardResponse} objects is created from the
     * provided {@link AwardRequest} list.
     * A randomly generated UUID is assigned through the superclass constructor.
     * </p>
     *
     * @param awardRequestList a list of {@link AwardRequest} objects containing details
     *                         of the award ceremony. If null or empty, the instance
     *                         will be initialised with default values.
     */
    public AwardCeremonyResponse(List<AwardRequest> awardRequestList) {
        super();
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
