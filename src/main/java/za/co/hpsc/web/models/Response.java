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
 * The {@code Response} class is a specialized subclass of {@code Request} that introduces
 * a unique identifier (UUID) for every response. This identifier is either provided during
 * the object construction or automatically generated if not supplied.
 * This class is designed to handle various use cases where a response must be uniquely identified.
 * It provides multiple constructors to support different initialization scenarios, allowing
 * response data to be populated with varying levels of detail.
 * </p>
 */
public class Response extends Request {
    @Getter
    @NotNull
    private UUID uuid = UUID.randomUUID();

    public Response() {
        this.uuid = ValueUtil.nullAsRandomUUID(null);
    }

    public Response(UUID uuid) {
        this.uuid = ValueUtil.nullAsRandomUUID(uuid);
    }

    public Response(UUID uuid, String title, String summary, String description, String category,
                    List<String> tags) {
        super(title, summary, description, category, tags);
        this.uuid = ValueUtil.nullAsRandomUUID(uuid);
    }

    public Response(UUID uuid, String title) {
        super(title);
        this.uuid = ValueUtil.nullAsRandomUUID(uuid);
    }

    public Response(String title, String summary, String description, String category,
                    List<String> tags) {
        super(title, summary, description, category, tags);
        this.uuid = ValueUtil.nullAsRandomUUID(null);
    }
}
