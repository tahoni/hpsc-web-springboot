package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a competitor's information.
 *
 * <p>
 * The {@code CompetitorDto} class is used to transfer competitor-related data between various layers
 * of the application.
 * It encapsulates details such as the competitor's name, unique identifiers, date of birth,
 * and competitor category.
 * It also provides utility methods for mapping data from entity and response models.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorDto {
    private UUID uuid = UUID.randomUUID();
    private Long id;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String middleNames;
    private LocalDate dateOfBirth;

    private Integer sapsaNumber;
    @NotNull
    private String competitorNumber;

    private CompetitorCategory category = CompetitorCategory.NONE;

    /**
     * Constructs a new {@code CompetitorDto} instance with data from the provided
     * {@link Competitor} entity.
     *
     * @param competitorEntity the {@link Competitor} entity containing the competitor's information,
     *                         such as unique identifier, first name, last name, middle names,
     *                         competitor number, date of birth, and category.
     *                         Must not be null.
     */
    public CompetitorDto(@NotNull Competitor competitorEntity) {
        if (competitorEntity == null) {
            return;
        }

        // Initialises competitor details
        this.id = competitorEntity.getId();

        // Initialises competitor attributes
        this.firstName = competitorEntity.getFirstName();
        this.lastName = competitorEntity.getLastName();
        this.middleNames = competitorEntity.getMiddleNames();
        this.dateOfBirth = competitorEntity.getDateOfBirth();

        // Initialises competitor number and SAPSA number
        this.sapsaNumber = competitorEntity.getSapsaNumber();
        this.competitorNumber = competitorEntity.getCompetitorNumber();

        // Initialises competitor category
        this.category = competitorEntity.getCategory();
    }

    // TOOD: Javadoc (not yet ready)
    public void init(MemberResponse memberResponse) {
        // Initialises competitor attributes
        this.firstName = memberResponse.getFirstName();
        this.lastName = memberResponse.getLastName();
        if (memberResponse.getDateOfBirth() != null) {
            this.dateOfBirth = memberResponse.getDateOfBirth().toLocalDate();
        }

        // Initialises competitor number and SAPSA number based on the member's ICS alias
        this.competitorNumber = memberResponse.getIcsAlias();
        if (NumberUtils.isCreatable(memberResponse.getIcsAlias())) {
            this.sapsaNumber = Integer.parseInt(memberResponse.getIcsAlias());
        }

        // Initialises competitor category based on the member's category code'
        // TODO: populate category
    }

    @Override
    public String toString() {
        if ((this.middleNames != null) && (!this.middleNames.isBlank())) {
            return this.firstName + " " + this.middleNames + " " + this.lastName;
        } else {
            return this.firstName + " " + this.lastName;
        }
    }
}
