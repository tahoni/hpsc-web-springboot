package za.co.hpsc.web.models.award.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

/**
 * Represents an award placement in a competition or event,
 * including information about the place, name, and an associated image.
 * Extends the {@code Placing} class to include additional details specific to awards.
 */
// TODO: Javadoc
@Getter
@Setter
@NoArgsConstructor
public class AwardPlacing extends Placing {
    private String imageFilePath = "";

    public AwardPlacing(String imageFilePath) {
        super();
        this.imageFilePath = imageFilePath;
    }

    public AwardPlacing(int place, String name, String imageFilePath) {
        super(place, name);
        this.imageFilePath = imageFilePath;
    }
}
