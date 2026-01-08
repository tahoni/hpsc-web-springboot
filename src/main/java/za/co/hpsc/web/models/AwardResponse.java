package za.co.hpsc.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AwardResponse extends Response {
    private LocalDateTime date;
    private String imageFilePath;

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

    public AwardResponse(LocalDateTime date, String imageFilePath, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super();
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(UUID uuid, LocalDateTime date, String imageFilePath, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(uuid);
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(UUID uuid, String title, String summary, String description, String category, List<String> tags, LocalDateTime date, String imageFilePath, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(uuid, title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(UUID uuid, String title, LocalDateTime date, String imageFilePath, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(uuid, title);
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(String title, String summary, String description, String category, List<String> tags, LocalDateTime date, String imageFilePath, AwardPlace firstPlace, AwardPlace secondPlace, AwardPlace thirdPlace) {
        super(title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    public AwardResponse(LocalDateTime date, String title, String summary, String description, String category, List<String> tags, String imageFilePath, String firstPlaceName, String secondPlaceName, String thirdPlaceName, String firstPlaceImageFileName, String secondPlaceImageFileName, String thirdPlaceImageFileName) {
        super(title, summary, description, category, tags);
        this.date = date;
        this.imageFilePath = imageFilePath;
        this.firstPlace = new AwardPlace(1, firstPlaceName, firstPlaceImageFileName);
        this.secondPlace = new AwardPlace(2, secondPlaceName, secondPlaceImageFileName);
        this.thirdPlace = new AwardPlace(3, thirdPlaceName, thirdPlaceImageFileName);
    }

    public AwardResponse(AwardRequest awardRequest) {
        this(awardRequest.getDate(), awardRequest.getTitle(), awardRequest.getSummary(), awardRequest.getDescription(), awardRequest.getCategory(), awardRequest.getTags(), awardRequest.getImageFilePath(), awardRequest.getFirstPlaceName(), awardRequest.getSecondPlaceName(), awardRequest.getThirdPlaceName(), awardRequest.getFirstPlaceImageFileName(), awardRequest.getSecondPlaceImageFilePath(), awardRequest.getThirdPlaceImageFilePath());
    }
}
