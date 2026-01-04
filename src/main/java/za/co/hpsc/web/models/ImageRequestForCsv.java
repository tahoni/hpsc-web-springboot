package za.co.hpsc.web.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class ImageRequestForCsv {
    @JsonProperty(required = true)
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    @JsonProperty(required = true)
    private String filePath;
    @JsonProperty(required = true)
    private String fileName;

    @JsonCreator
    public ImageRequestForCsv(@JsonProperty(value = "title", required = true) String title, @JsonProperty(value = "filePath", required = true) String filePath, @JsonProperty(value = "fileName", required = true) String fileName) {
        this.title = title;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
