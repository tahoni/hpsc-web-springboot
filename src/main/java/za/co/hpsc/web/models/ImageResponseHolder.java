package za.co.hpsc.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class designed to hold a collection of {@code ImageResponse} objects.
 * This class provides basic functionality for managing and storing a list of image
 * responses, which encapsulate metadata and details about individual images.
 * <p>
 * The {@code ImageResponseHolder} is initialized with an empty list of {@code ImageResponse}
 * objects by default but allows flexible initialization with a predefined list using the
 * provided constructors.
 * <p>
 * Key characteristics:
 * - Allows the storage and retrieval of multiple {@code ImageResponse} objects.
 * - Supports initialization with or without a pre-defined list of responses.
 * <p>
 * This class uses Lombok annotations to automatically generate getter and setter
 * methods, as well as no-argument and argument-based constructors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseHolder {
    private List<ImageResponse> images = new ArrayList<>();
}
