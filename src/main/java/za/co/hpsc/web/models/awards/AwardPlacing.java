package za.co.hpsc.web.models.awards;

import lombok.Getter;
import lombok.Setter;
import za.co.hpsc.web.models.shared.Placing;

@Getter
@Setter
public class AwardPlacing extends Placing {
    private String imageFilePath;

    public AwardPlacing() {
        super();
        this.imageFilePath = "";
    }

    public AwardPlacing(String imageFilePath) {
        super();
        this.imageFilePath = imageFilePath;
    }

    public AwardPlacing(int place, String name, String imageFilePath) {
        super(place, name);
        this.imageFilePath = imageFilePath;
    }
}
