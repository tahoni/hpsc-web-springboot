package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.List;
import java.util.UUID;

/**
 * Represents a response extending common request properties and containing a unique identifier.
 *
 * <p>
 * The {@code Response} class is a specialised subclass of {@link Request} that introduces
 * a unique identifier (UUID) for every response. This identifier is either provided during
 * the object construction or automatically generated if not supplied.
 * This class is designed to handle various use cases where a response must be uniquely identified.
 * It provides multiple constructors to support different initialisation scenarios, allowing
 * response data to be populated with varying levels of detail.
 * </p>
 */
// TODO: Javadoc
public class Response extends Request {
    @Getter
    @NotNull
    private UUID uuid = UUID.randomUUID();

    /**
     * Default constructor for the {@code Response} class.
     * <p>
     * This constructor initialises a new instance of {@code Response} with a unique
     * identifier (UUID). If no UUID is explicitly provided, a random UUID is
     * generated using the {@link ValueUtil#nullAsRandomUuid} method to ensure that
     * every response instance is uniquely identifiable.
     */
    public Response() {
        this.uuid = ValueUtil.nullAsRandomUuid(null);
    }

    /**
     * Constructs a new {@code Response} object with a specified UUID.
     * If the provided UUID is null, a random UUID is generated.
     *
     * @param uuid the unique identifier for the response. If null, a random UUID is generated.
     */
    public Response(UUID uuid) {
        this.uuid = ValueUtil.nullAsRandomUuid(uuid);
    }

    /**
     * Constructs a new {@code Response} object with the specified details.
     * Extends functionality from the {@link Request} class and ensures that
     * a unique identifier (UUID) is assigned to every response. If the provided
     * UUID is null, a random UUID is generated.
     *
     * @param uuid        the unique identifier for the response. If null, a random UUID is generated.
     * @param title       the title of the response. Must not be null or blank.
     * @param summary     a brief summary of the response. It may be null.
     * @param description a detailed description of the response. It may be null.
     * @param category    the category under which the response is classified. It may be null.
     * @param tags        a list of tags associated with the response. If null, an empty list is assigned.
     */
    public Response(UUID uuid, String title, String summary, String description, String category,
                    List<String> tags) {
        super(title, summary, description, category, tags);
        this.uuid = ValueUtil.nullAsRandomUuid(uuid);
    }

    /**
     * Constructs a new {@code Response} object with the specified UUID and title.
     * If the provided UUID is null, a random UUID is generated. Extends functionality
     * from the {@link Request} class to manage response-specific details.
     *
     * @param uuid  the unique identifier for the response. If null, a random UUID is generated.
     * @param title the title of the response. Must not be null or blank.
     */
    public Response(UUID uuid, String title) {
        super(title);
        this.uuid = ValueUtil.nullAsRandomUuid(uuid);
    }

    /**
     * Constructs a new {@code Response} object with the specified details.
     * Extends functionality from the {@link Request} class to include
     * response-specific attributes.
     *
     * @param title       the title of the response. Must not be null or blank.
     * @param summary     a brief summary of the response. It may be null.
     * @param description a detailed description of the response. It may be null.
     * @param category    the category under which the response is classified. It may be null.
     * @param tags        a list of tags associated with the response. If null, an empty list is assigned.
     */
    public Response(String title, String summary, String description, String category,
                    List<String> tags) {
        super(title, summary, description, category, tags);
        this.uuid = ValueUtil.nullAsRandomUuid(null);
    }
}
