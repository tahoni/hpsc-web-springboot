package za.co.hpsc.web.models.match;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.utils.ValueUtil;

/**
 * Represents a club within the system, which maintains details about its name, abbreviation,
 * and associated matches.
 *
 * <p>
 * The {@code Club} class serves as an entity in the persistence layer and is used to
 * encapsulate data related to a club, including its unique identifier, name, and a list of matches.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to return the club's name as a
 * string representation.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// TOOO: fix Javadoc
public class ClubDto {
    private Long id;

    @NotNull
    private String name;
    private String abbreviation;

    public ClubDto(Club clubEntity) {
        this.id = clubEntity.getId();
        this.name = clubEntity.getName();
        this.abbreviation = clubEntity.getAbbreviation();
    }

    /**
     * Initialises the club entity with details from a given {@code ClubResponse} object.
     *
     * @param clubResponse the {@code ClubResponse} object containing the club's name
     *                     and abbreviation. Must not be null.
     */
    public void init(@NotNull ClubResponse clubResponse) {
        this.name = ValueUtil.nullAsEmptyString(clubResponse.getClubName());
        this.abbreviation = clubResponse.getClubCode();
    }

    @Override
    public String toString() {
        return this.name + " (" + this.abbreviation + ")";
    }
}
