package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class representing a list of {@link IpscResponse} instances.
 *
 * <p>
 * This class serves as a holder for multiple {@link IpscResponse} objects, encapsulating
 * data related to IPSC (International Practical Shooting Confederation) entities such as clubs,
 * matches, stages, tags, members, and scores.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
public class IpscResponseHolder {
    @NotNull
    List<IpscResponse> ipscList = new ArrayList<>();
}
