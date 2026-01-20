package za.co.hpsc.web.models.image;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class designed to hold a collection of {@link ImageResponse} objects.
 * This class provides basic functionality for managing and storing a list of image
 * responses, which encapsulate metadata and details about individual images.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseHolder {
    @NotNull
    private List<ImageResponse> images = new ArrayList<>();
}
