package za.co.hpsc.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
public class ImageResponse extends ImageRequest {
    @Getter
    @Setter
    private UUID id = UUID.randomUUID();
    @Getter
    private String mimeType;

    public ImageResponse(String title, String summary, String description, String category, String tags,
                         String filePath, String fileName, UUID id, String mimeType) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = ((id != null) ? id : UUID.randomUUID());
        setMimeType(mimeType);
    }

    public ImageResponse(String title, String summary, String description, String category, String tags,
                         String filePath, String fileName) {
        super(title, summary, description, category, tags, filePath, fileName);
        this.id = UUID.randomUUID();
        setMimeType("");
    }

    public ImageResponse(ImageRequest imageRequest) {
        this(imageRequest.getTitle(), imageRequest.getSummary(), imageRequest.getDescription(),
                imageRequest.getCategory(), imageRequest.getTags(), imageRequest.getFilePath(),
                imageRequest.getFileName());
        setMimeType("");
    }

    public void setMimeType(String mimeType) {
        if ((mimeType != null) && (!mimeType.isBlank())) {
            this.mimeType = mimeType;
        } else {
            Optional<MediaType> optionalMediaType = MediaTypeFactory.getMediaType(this.getFileName());
            optionalMediaType.ifPresent(mediaType -> this.mimeType = mediaType.toString());
        }
    }
}
