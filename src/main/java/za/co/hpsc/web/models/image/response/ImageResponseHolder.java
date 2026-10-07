package za.co.hpsc.web.models.image.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A container class designed to hold a collection of {@link ImageResponse} objects.
 * This class provides basic functionality for managing and storing a list of image
 * responses, which encapsulate metadata and details about individual images.
 *
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
public class ImageResponseHolder {
    @NonNull
    private List<ImageResponse> images;
}
