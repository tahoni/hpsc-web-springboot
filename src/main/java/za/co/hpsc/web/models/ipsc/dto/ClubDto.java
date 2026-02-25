package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a club's information.
 *
 * <p>
 * The {@code ClubDto} class is used to transfer club-related data between various layers
 * of the application.
 * It encapsulates details such as the club's unique identifier, name, and abbreviation.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;
    private Integer index;
    private Long matchId;

    @NotNull
    private String name = "";
    private String abbreviation;

    /**
     * Constructs a new {@code ClubDto} instance with data from the provided
     * {@link Club} entity.
     *
     * @param clubEntity the {@link Club} entity containing the club's information
     *                   such as its unique identifier, name, and abbreviation.
     *                   Must not be null.
     */
    public ClubDto(Club clubEntity) {
        if (clubEntity != null) {
            // Initialises club details
            this.id = clubEntity.getId();

            // Initialises club attributes
            this.name = clubEntity.getName();
            this.abbreviation = clubEntity.getAbbreviation();
        }
    }

    // TODO: Javadoc
    public ClubDto(ClubResponse clubResponse) {
        if (clubResponse != null) {
            // Initialises club details
            this.index = clubResponse.getClubId();
            // Initialises club attributes
            this.name = clubResponse.getClubName();
            this.abbreviation = clubResponse.getClubCode();
        }
    }

    // TODO: Javadoc
    public ClubDto(ClubIdentifier clubIdentifier) {
        if (clubIdentifier != null) {
            // Initialises club attributes
            this.name = clubIdentifier.getName();
            this.abbreviation = clubIdentifier.getName();
        }
    }

    // TODO: Javadoc
    public ClubDto(Club clubEntity, ClubIdentifier clubIdentifier) {
        if (clubEntity != null) {
            // Initialises club details
            this.id = clubEntity.getId();
            // Initialises club attributes
            this.name = clubEntity.getName();
            this.abbreviation = clubEntity.getAbbreviation();

        } else if (clubIdentifier != null) {
            // Initialises club attributes
            this.name = clubIdentifier.getName();
            this.abbreviation = clubIdentifier.getName();
        }
    }

    // TODO: Javadoc
    public void init(ClubResponse clubResponse) {
        if (clubResponse != null) {
            // Initialises club details
            this.index = clubResponse.getClubId();
            // Initialises club attributes
            this.name = clubResponse.getClubName();
            this.abbreviation = clubResponse.getClubCode();
        }
    }

    /**
     * Returns a string representation of the club's information.
     *
     * <p>
     * The format includes the club name, and if available, the abbreviation.
     * </p>
     *
     * @return a string containing the club's name, optionally followed by its abbreviation,
     * or just the name if no abbreviation is specified.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Appends the club name if it is not null and not blank
        if ((this.name != null && (!this.name.isBlank()))) {
            sb.append(this.name.trim());
            sb.append(" ");
        }

        // Appends the club abbreviation in parentheses if it is not null, not blank,
        // and not the same as the name
        if ((this.abbreviation != null) && (!this.abbreviation.isBlank()) &&
                (!this.abbreviation.equalsIgnoreCase(this.name))) {
            sb.append("(");
            sb.append(this.abbreviation.trim());
            sb.append(")");
        }

        // Trim all leading and trailing whitespace, and remove parentheses
        // if they are the leading and trailing characters
        String result = sb.toString().trim();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1).trim();
        }

        return result.trim();
    }
}
