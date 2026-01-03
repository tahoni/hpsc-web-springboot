package za.co.hpsc.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageRequest {
    private String title;
    private String summary;
    private String description;

    private String category;
    private String tags;

    private String filePath;
    private String fileName;

    public ImageRequest(String title, String summary, String description, String category,
                        String tags, String filePath, String fileName) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
