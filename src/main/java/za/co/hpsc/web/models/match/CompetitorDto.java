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
// TOOO: fix Javadoc
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

    public CompetitorDto(Competitor competitor) {
        this.id = competitor.getId();
        this.firstName = competitor.getFirstName();
        this.lastName = competitor.getLastName();
        this.middleNames = competitor.getMiddleNames();
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
