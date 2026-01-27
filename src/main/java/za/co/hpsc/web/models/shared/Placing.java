package za.co.hpsc.web.models.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a placement or ranking in a competition or event.
 *
 * <p>
 * This class encapsulates details about the position or rank
 * (e.g. first place, second place) along with an associated name
 * (e.g. the participant or team achieving the placement).
 * Typically, objects of this class can be used in contexts involving
 * rankings, leaderboard displays, or competition results.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Placing {
    private int place = 0;
    private String name;
}
