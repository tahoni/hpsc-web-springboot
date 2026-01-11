package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A container class designed to hold a collection of {@link AwardCeremonyResponse} objects.
 * This class provides basic functionality for managing and storing a list of award ceremony
 * responses, which encapsulate metadata and details about individual award ceremonies.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardResponseHolder {
    @NotNull
    private List<AwardCeremonyResponse> awards;
}
