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
            String clubIdentifierName = clubIdentifier.getName();
            this.name = clubIdentifierName;
            this.abbreviation = clubIdentifierName;
        }
    }

    // TODO: Javadoc
    public ClubDto(Club clubEntity, ClubIdentifier clubIdentifier) {
        if (clubEntity != null) {
            this.id = clubEntity.getId();
            this.name = clubEntity.getName();
            this.abbreviation = clubEntity.getAbbreviation();
        } else if (clubIdentifier != null) {
            this.abbreviation = clubIdentifier.getName();
        }
    }

    // TODO: Javadoc
    public void init(ClubResponse clubResponse) {
        if (clubResponse != null) {
            this.index = clubResponse.getClubId();
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
        if ((abbreviation != null) && (!abbreviation.isBlank())) {
            return this.name + " (" + this.abbreviation + ")";
        } else {
            return this.name;
        }
    }
}
