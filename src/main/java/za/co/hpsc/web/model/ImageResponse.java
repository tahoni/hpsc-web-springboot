package za.co.hpsc.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
public class ImageResponse extends ImageRequest {
    @Getter
    @Setter
    private UUID id = UUID.randomUUID();
    @Getter
    private String mimeType;

    @Getter
    List<String> tagsList;

    public ImageResponse(String title, String summary, String description, String category, String tags,
                         String filePath, String fileName, UUID id, String mimeType) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = ((id != null) ? id : UUID.randomUUID());
        setMimeType(mimeType);
        this.setTagsList(null);
    }

    public ImageResponse(String title, String summary, String description, String category, String tags,
                         String filePath, String fileName) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = UUID.randomUUID();
        setMimeType(null);
        setTagsList(null);
    }

    public ImageResponse(ImageRequest imageRequest) {
        this(imageRequest.getTitle(), imageRequest.getSummary(), imageRequest.getDescription(),
                imageRequest.getCategory(), imageRequest.getTags(), imageRequest.getFilePath(),
                imageRequest.getFileName());
        setMimeType(null);
        setTagsList(null);
    }

    public void setMimeType(String mimeType) {
        if ((mimeType != null) && (!mimeType.isBlank())) {
            this.mimeType = mimeType;
        } else {
            if (this.getFileName() != null) {
                Optional<MediaType> optionalMediaType = MediaTypeFactory.getMediaType(this.getFileName());
                optionalMediaType.ifPresent(mediaType -> this.mimeType = mediaType.toString());
            }
        }
    }

    public void setTagsList(List<String> tagsList) {
        if ((tagsList != null) && (!tagsList.isEmpty())) {
            this.tagsList = tagsList;
        } else {
            if (this.getTags() != null) {
                this.tagsList = List.of(this.getTags().split("\\|"));
            }
        }
    }
}
