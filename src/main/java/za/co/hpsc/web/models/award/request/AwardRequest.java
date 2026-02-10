package za.co.hpsc.web.models.award.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.Request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A specialised request class that encapsulates additional information for award-related requests.
 *
 * <p>
 * The {@code AwardRequest} class extends the functionality of the {@link Request} base class
 * by introducing additional fields specific to awards. These include information about
 * the award ceremony, associated media files, and the names of winners for first, second,
 * and third places. It provides constructors for initialising an award request with basic
 * or detailed metadata.
 * </p>
 */
@Getter
@Setter
public class AwardRequest extends Request {
    private LocalDate date;
    private String imageFilePath;

    @NotNull
    private String ceremonyTitle = "";
    private String ceremonySummary;
    private String ceremonyDescription;
    private String ceremonyCategory;
    private List<String> ceremonyTags = new ArrayList<>();

    @NotNull
    private String firstPlaceName = "";
    private String secondPlaceName;
    private String thirdPlaceName;

    private String firstPlaceImageFileName;
    private String secondPlaceImageFileName;
    private String thirdPlaceImageFileName;

    /**
     * Constructs a new {@code AwardRequest} object with the specified details.
     *
     * <p>
     * This constructor initialises fields specific to an award, such as the title of the award,
     * the ceremony title, and the names of recipients for first, second, and third places.
     * </p>
     *
     * @param title           the title of the award. Must not be null or blank.
     * @param ceremonyTitle   the title of the ceremony associated with the award.
     *                        Must not be null.
     * @param firstPlaceName  the name of the first-place recipient.
     *                        Must not be null.
     * @param secondPlaceName the name of the second-place recipient.
     *                        Can be null.
     * @param thirdPlaceName  the name of the third-place recipient.
     *                        Can be null.
     */
    public AwardRequest(@NotNull @NotBlank String title, @NotNull String ceremonyTitle,
                        @NotNull String firstPlaceName, String secondPlaceName, String thirdPlaceName) {
        super(title);
        this.firstPlaceName = firstPlaceName;
        this.secondPlaceName = secondPlaceName;
        this.thirdPlaceName = thirdPlaceName;
        this.ceremonyTitle = ceremonyTitle;
    }
}
