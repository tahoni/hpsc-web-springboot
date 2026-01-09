package za.co.hpsc.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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

    public AwardResponse(AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super();
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(UUID uuid, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(uuid);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(UUID uuid, String title, String summary, String description, String category,
                         List<String> tags, AwardPlace firstPlace, AwardPlace secondPlace,
                         AwardPlace thirdPlace) {
        super(uuid, title, summary, description, category, tags);
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

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
                awardRequest.getSecondPlaceImageFilePath(), awardRequest.getThirdPlaceImageFilePath());
    }
}
