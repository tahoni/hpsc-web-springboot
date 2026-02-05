package za.co.hpsc.web.models.match;

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
    private Long id;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String middleNames;
    private Integer sapsaNumber;
    @NotNull
    private String competitorNumber;
    private LocalDate dateOfBirth;

    private CompetitorCategory category = CompetitorCategory.NONE;

    /**
     * Constructs a new {@code CompetitorDto} instance with data from the provided
     * {@link Competitor} entity.
     *
     * @param competitor the {@link Competitor} entity containing the competitor's information,
     *                   such as unique identifier, first name, last name, middle names,
     *                   competitor number, date of birth, and category. Must not be null.
     */
    public CompetitorDto(@NotNull Competitor competitor) {
        this.id = competitor.getId();
        this.firstName = competitor.getFirstName();
        this.lastName = competitor.getLastName();
        this.middleNames = competitor.getMiddleNames();
        this.sapsaNumber = competitor.getSapsaNumber();
        this.competitorNumber = competitor.getCompetitorNumber();
        this.dateOfBirth = competitor.getDateOfBirth();
        this.category = competitor.getCategory();
    }

    public void init(MemberResponse memberResponse) {
        this.firstName = memberResponse.getFirstName();
        this.lastName = memberResponse.getLastName();
        this.dateOfBirth = memberResponse.getDateOfBirth().toLocalDate();

        // Initialises competitor number and SAPSA number based on the member's
        // reference number and ICS alias'
        this.competitorNumber = memberResponse.getIcsAlias();
        if (NumberUtils.isCreatable(memberResponse.getIcsAlias())) {
            this.sapsaNumber = Integer.parseInt(memberResponse.getIcsAlias());
        }

        // TODO: populate category
    }

    @Override
    public String toString() {
        if ((middleNames != null) && (!middleNames.isBlank())) {
            return firstName + " " + middleNames + " " + lastName;
        } else {
            return firstName + " " + lastName;
        }
    }
}
