package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;

import java.util.ArrayList;
import java.util.List;

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
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    private String abbreviation;

    @OneToMany(fetch = FetchType.LAZY)
    private List<IpscMatch> matches = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY)
    private List<ClubMatch> clubMatches = new ArrayList<>();

    public Club(String name, String abbreviation) {
        // Initialises club details
        this.name = name;

        // Initialises club attributes
        this.abbreviation = abbreviation;
    }

    /**
     * Initialises the current {@code Club} entity with data from a DTO.
     *
     * <p>
     * This method sets the relevant fields in the entity, including name and abbreviation.
     * </p>
     *
     * @param clubDto the DTO containing data needed to populate the entity fields.
     */
    public void init(ClubDto clubDto) {
        // Initialises club details
        this.name = clubDto.getName();

        // Initialises club attributes
        this.abbreviation = clubDto.getAbbreviation();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
