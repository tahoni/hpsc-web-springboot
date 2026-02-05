package za.co.hpsc.web.models.award.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

/**
 * Represents an award placement in a competition or event,
 * including information about the place, name, and an associated image.
 *
 * <p>
 * Extends the {@code Placing} class to include additional details specific to awards.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class AwardPlacing extends Placing {
    private String imageFilePath = "";

    /**
     * Constructs an {@code AwardPlacing} instance with the specified place, name,
     * and image file path.
     *
     * @param place         the ordinal position or rank for this award placement
     * @param name          the name or title associated with this award placement
     * @param imageFilePath the file path of the image associated with this award placement
     */
    public AwardPlacing(int place, String name, String imageFilePath) {
        super(place, name);
        this.imageFilePath = imageFilePath;
    }
}
