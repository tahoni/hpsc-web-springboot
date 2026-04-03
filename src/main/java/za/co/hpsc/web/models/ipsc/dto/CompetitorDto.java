package za.co.hpsc.web.models.ipsc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;
import za.co.hpsc.web.utils.IpscUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private transient List<Integer> indexes = new ArrayList<>();

    @NotNull
    private String firstName = "";
    @NotNull
    private String lastName = "";
    private String middleNames;
    private LocalDate dateOfBirth;

    private Integer sapsaNumber;
    @NotNull
    private String competitorNumber;

    /**
     * Constructs a new {@code CompetitorDto} instance using the provided
     * {@link Competitor} entity.
     *
     * @param competitorEntity the {@link Competitor} entity containing the competitor's information,
     *                         such as unique identifier, first name, last name, middle names,
     *                         competitor number, date of birth, and category.
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
    }

    /**
     * Constructs a new {@code CompetitorDto} instance using the provided
     * {@link MemberResponse} objecty.
     *
     * @param memberResponse the {@link MemberResponse} object containing competitor-related
     *                       information, such as the competitor's first name, last name,
     *                       date of birth, and SAPSA number.
     */
    // TODO: add tests for this constructor
    public CompetitorDto(MemberResponse memberResponse) {
        if (memberResponse != null) {
            // Initialises competitor details
            this.indexes = List.of(memberResponse.getMemberId());

            // Initialises competitor attributes
            this.firstName = memberResponse.getFirstName().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX,
                    "").trim();
            this.lastName = memberResponse.getLastName().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX,
                    "").trim();
            if (memberResponse.getDateOfBirth() != null) {
                this.dateOfBirth = memberResponse.getDateOfBirth().toLocalDate();
            }

            // Initialises competitor number and SAPSA number based on the member's ICS alias
            this.competitorNumber = memberResponse.getIcsAlias();
            this.sapsaNumber = IpscUtil.getValidSapsaNumber(memberResponse.getIcsAlias());
        }
    }

    /**
     * Initialises the current {@code CompetitorDto} instance with data from the provided
     * {@link MemberResponse} object.
     *
     * @param memberResponse the {@link MemberResponse} object containing competitor-related
     *                       information, such as the competitor's first name, last name,
     *                       date of birth, and SAPSA number.
     */
    public void init(MemberResponse memberResponse) {
        if (memberResponse != null) {
            // Initialises competitor details
            this.indexes.add(memberResponse.getMemberId());

            // Initialises competitor attributes
            this.firstName = memberResponse.getFirstName().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX,
                    "").trim();
            this.lastName = memberResponse.getLastName().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX,
                    "").trim();
            if (memberResponse.getDateOfBirth() != null) {
                this.dateOfBirth = memberResponse.getDateOfBirth().toLocalDate();
            }

            // Initialises competitor number and SAPSA number based on the member's ICS alias
            this.competitorNumber = memberResponse.getIcsAlias();
            Integer sapsaNumber = IpscUtil.getValidSapsaNumber(memberResponse.getIcsAlias());
            this.sapsaNumber = ((sapsaNumber != null) ? sapsaNumber : this.sapsaNumber);
        }
    }

    /**
     * Returns a string representation of the competitor's full name.
     *
     * <p>
     * If middle names are present and not blank, they are included
     * between the first name and last name. Otherwise, only the
     * first name and last name are included.
     * </p>
     *
     * @return a string representation of the competitor's full name,
     * which may include middle names if available and non-blank.
     */
    @Override
    public String toString() {
        String firstNameString = ((this.firstName != null) ? this.firstName.trim() : "");
        String lastNameString = ((this.lastName != null) ? this.lastName.trim() : "");
        String middleNamesString = ((this.middleNames != null) ? this.middleNames.trim() : "");

        String result = "";
        if (!middleNamesString.isBlank()) {
            result = firstNameString + " " + middleNamesString + " " + lastNameString;
        } else {
            result = firstNameString + " " + lastNameString;
        }
        return result.trim();
    }
}
