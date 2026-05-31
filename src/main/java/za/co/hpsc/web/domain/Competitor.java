package za.co.hpsc.web.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a competitor in the system, which maintains details about its name,
 * competitor number, category, and associated matches.
 *
 * <p>
 * The {@code Competitor} class serves as an entity in the persistence layer and is used to
 * encapsulate data related to a competitor, such as their name, unique identifier,
 * competitor number, category, and other details. It also maintains a list of matches
 * associated with the competitor.
 * It provides constructors for creating instances with specific details or using default values.
 * Additionally, it overrides the {@code toString} method to provide a formatted string
 * representation of the competitor's name, including middle names if available.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String firstName;
    @NotNull
    @Column(nullable = false)
    private String lastName;
    private String middleNames;
    private LocalDate dateOfBirth;

    private Integer sapsaNumber;
    @NotNull
    @Column(nullable = false)
    private String competitorNumber;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @OneToMany(mappedBy = "competitor", fetch = FetchType.EAGER)
    private List<MatchCompetitor> competitorMatches = new ArrayList<>();
    @OneToMany(mappedBy = "competitor", fetch = FetchType.EAGER)
    private List<MatchStageCompetitor> competitorStageMatches = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String firstName = ValueUtil.nullAsDefaultString(this.firstName, "").trim();
        sb.append(firstName).append(" ");

        String middleNames = ValueUtil.nullAsDefaultString(this.middleNames, "").trim();
        if (!middleNames.isEmpty()) {
            sb.append(middleNames).append(" ");
        }

        String lastName = ValueUtil.nullAsDefaultString(this.lastName, "").trim();
        sb.append(lastName);

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
