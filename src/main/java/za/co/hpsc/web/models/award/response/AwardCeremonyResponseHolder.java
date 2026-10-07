package za.co.hpsc.web.models.award.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A container class designed to hold a collection of {@link AwardCeremonyResponse} objects.
 * This class provides basic functionality for managing and storing a list of award ceremony
 * responses, which encapsulate metadata and details about individual award ceremonies.
 *
 * @since 1.1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class AwardCeremonyResponseHolder {
    @NonNull
    private List<AwardCeremonyResponse> awardCeremonies;
}
