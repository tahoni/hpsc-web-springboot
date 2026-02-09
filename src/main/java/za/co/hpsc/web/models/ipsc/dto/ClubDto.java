package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.Club;

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

    @NotNull
    private String name;
    private String abbreviation;

    // TODO: Javadoc
    public ClubDto(Long id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Constructs a new {@code ClubDto} instance with data from the provided
     * {@link Club} entity.
     *
     * @param clubEntity the {@link Club} entity containing the club's information
     *                   such as its unique identifier, name, and abbreviation.
     *                   Must not be null.
     */
    public ClubDto(@NotNull Club clubEntity) {
        this.id = clubEntity.getId();
        this.name = clubEntity.getName();
        this.abbreviation = clubEntity.getAbbreviation();
    }

    // TODO: Javadoc
    public ClubDto(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.abbreviation + ")";
    }
}
