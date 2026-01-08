package za.co.hpsc.web.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class AwardRequestForCSV {
    @JsonProperty(required = true)
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    private String date;
    @JsonProperty(required = true)
    private String imageFilePath;
    @JsonProperty(required = true)
    private String firstPlace;
    @JsonProperty(required = true)
    private String secondPlace;
    @JsonProperty(required = true)
    private String thirdPlace;
    private String firstPlaceImageFileName;
    private String secondPlaceImageFilePath;
    private String thirdPlaceImageFilePath;

    @JsonCreator
    public AwardRequestForCSV(@JsonProperty(value = "title", required = true) String title,
                              @JsonProperty(value = "firstPlace", required = true) String firstPlace,
                              @JsonProperty(value = "secondPlace", required = true) String secondPlace,
                              @JsonProperty(value = "thirdPlace", required = true) String thirdPlace) {
        this.title = title;
        this.firstPlace = firstPlace;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }
}
