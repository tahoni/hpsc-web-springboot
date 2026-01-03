package za.co.hpsc.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ImageRequest {
    private String title;
    private String summary;
    private String description;

    private String category;
    private List<String> tags;

    private String filePath;
    private String fileName;

    public ImageRequest(String title, String summary, String description, String category,
                        List<String> tags, String filePath, String fileName) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
