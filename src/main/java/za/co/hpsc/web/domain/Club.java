package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.LocalDateTime;
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

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    private List<IpscMatch> matches = new ArrayList<>();

    public Club(String name, String abbreviation) {
        // Initialises club attributes
        this.name = name;
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String name = ValueUtil.nullAsDefaultString(this.name, "").trim();
        sb.append(name).append(" ");

        String abbreviation = ValueUtil.nullAsDefaultString(this.abbreviation, "").trim();
        if ((!abbreviation.isEmpty()) && (!abbreviation.equalsIgnoreCase(name))) {
            sb.append("(").append(abbreviation).append(")");
        }

        return sb.toString().trim();
    }

    @PrePersist
    void onInsert() {
        this.dateCreated = LocalDateTime.now();
        this.dateUpdated = this.dateCreated;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }
}
