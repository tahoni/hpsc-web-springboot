package za.co.hpsc.web.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"title", "summary", "description", "category", "tags", "filePath", "fileName"})
public abstract class ImageRequestForCsv {
    private String title;
    private String summary;
    private String description;

    private String category;
    private String tags;

    private String filePath;
    private String fileName;
}
