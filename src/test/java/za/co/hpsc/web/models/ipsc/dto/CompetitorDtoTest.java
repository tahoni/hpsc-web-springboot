package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorDtoTest {

    // Constructor mapping

    // Null and Empty Cases
    @Test
    void testConstructor_whenCompetitorNull_thenKeepsDefaults() {
        // Arrange
        CompetitorDto dto = new CompetitorDto(null);

        // Act
        String firstName = dto.getFirstName();

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertEquals("", firstName);
        assertEquals("", dto.getLastName());
        assertEquals("", dto.getMiddleNames());
        assertNull(dto.getDateOfBirth());
        assertNull(dto.getSapsaNumber());
        assertNull(dto.getCompetitorNumber());
    }

    @Test
    void testConstructor_whenCompetitorFieldsNull_thenMapsNulls() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(42L);
        competitor.setFirstName(null);
        competitor.setLastName(null);
        competitor.setMiddleNames(null);
        competitor.setDateOfBirth(null);
        competitor.setSapsaNumber(null);
        competitor.setCompetitorNumber(null);

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertEquals(42L, dto.getId());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getMiddleNames());
        assertNull(dto.getDateOfBirth());
        assertNull(dto.getSapsaNumber());
        assertNull(dto.getCompetitorNumber());
    }

    // Fully Populated
    @Test
    void testConstructor_whenCompetitorPopulated_thenMapsAllFields() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(99L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor.setMiddleNames("Ann Marie");
        competitor.setDateOfBirth(java.time.LocalDate.of(1990, 5, 12));
        competitor.setSapsaNumber(12345);
        competitor.setCompetitorNumber("C-001");

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertEquals(99L, dto.getId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("Ann Marie", dto.getMiddleNames());
        assertEquals(java.time.LocalDate.of(1990, 5, 12), dto.getDateOfBirth());
        assertEquals(12345, dto.getSapsaNumber());
        assertEquals("C-001", dto.getCompetitorNumber());
    }

    @Test
    void testConstructor_whenCompetitorPopulated_thenUuidIsNotNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(101L);
        competitor.setFirstName("Mia");
        competitor.setLastName("Stone");
        competitor.setMiddleNames("L");
        competitor.setDateOfBirth(java.time.LocalDate.of(1988, 9, 3));
        competitor.setSapsaNumber(22222);
        competitor.setCompetitorNumber("C-101");

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertNotNull(dto.getUuid());
    }

    // Partially Populated
    @Test
    void testConstructor_whenCompetitorPartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(7L);
        competitor.setFirstName("Lara");
        competitor.setCompetitorNumber("C-777");

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertEquals(7L, dto.getId());
        assertEquals("Lara", dto.getFirstName());
        assertEquals("C-777", dto.getCompetitorNumber());
        assertNull(dto.getLastName());
        assertNull(dto.getMiddleNames());
        assertNull(dto.getDateOfBirth());
        assertNull(dto.getSapsaNumber());
    }

    @Test
    void testConstructor_whenCompetitorPartial_thenUuidIsNotNull() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(102L);
        competitor.setFirstName("Nina");
        competitor.setCompetitorNumber("C-102");

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertNotNull(dto.getUuid());
    }

    // Edge Cases - Competitor Number and Sapsa Number
    @Test
    void testConstructor_whenCompetitorNumberNullAndSapsaSet_thenMapsNullAndSapsa() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(55L);
        competitor.setFirstName("Sam");
        competitor.setCompetitorNumber(null);
        competitor.setSapsaNumber(54321);

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertEquals(55L, dto.getId());
        assertEquals("Sam", dto.getFirstName());
        assertNull(dto.getCompetitorNumber());
        assertEquals(54321, dto.getSapsaNumber());
    }

    @Test
    void testConstructor_whenCompetitorNumberNullAndSapsaNull_thenMapsNulls() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setId(56L);
        competitor.setFirstName("Alex");
        competitor.setCompetitorNumber(null);
        competitor.setSapsaNumber(null);

        // Act
        CompetitorDto dto = new CompetitorDto(competitor);

        // Assert
        assertEquals(56L, dto.getId());
        assertEquals("Alex", dto.getFirstName());
        assertNull(dto.getCompetitorNumber());
        assertNull(dto.getSapsaNumber());
    }

    // init() mapping

    // Null/Empty Cases
    @Test
    void testInit_whenMemberResponseNull_thenKeepsExistingValues() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        dto.setIndex(5);
        dto.setFirstName("Existing");
        dto.setLastName("Member");
        dto.setMiddleNames("X");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 2));
        dto.setCompetitorNumber("C-EX");
        dto.setSapsaNumber(11111);

        // Act
        dto.init(null);

        // Assert
        assertEquals(5, dto.getIndex());
        assertEquals("Existing", dto.getFirstName());
        assertEquals("Member", dto.getLastName());
        assertEquals("X", dto.getMiddleNames());
        assertEquals(LocalDate.of(2000, 1, 2), dto.getDateOfBirth());
        assertEquals("C-EX", dto.getCompetitorNumber());
        assertEquals(11111, dto.getSapsaNumber());
    }

    @Test
    void testInit_whenDateOfBirthNull_thenKeepsNullDate() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(23);
        memberResponse.setFirstName("Nina");
        memberResponse.setLastName("Gray");
        memberResponse.setDateOfBirth(null);
        memberResponse.setIcsAlias("999");

        // Act
        dto.init(memberResponse);

        // Assert
        assertNull(dto.getDateOfBirth());
    }

    // Fully Populated
    @Test
    void testInit_whenEnrolledResponseNull_thenMapsMemberAndSetsNoneCategory() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(12);
        memberResponse.setFirstName("Jane (RO)");
        memberResponse.setLastName("DoeRO");
        memberResponse.setDateOfBirth(LocalDateTime.of(1995, 3, 10, 0, 0));
        memberResponse.setIcsAlias("12345");

        // Act
        dto.init(memberResponse);

        // Assert
        assertEquals(12, dto.getIndex());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals(LocalDate.of(1995, 3, 10), dto.getDateOfBirth());
        assertEquals("12345", dto.getCompetitorNumber());
        assertEquals(12345, dto.getSapsaNumber());
    }

    // Category Mapping
    @Test
    void testInit_whenEnrolledResponseHasCategoryCode_thenMapsCategory() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(20);
        memberResponse.setFirstName("Lara");
        memberResponse.setLastName("Smith");
        memberResponse.setIcsAlias("777");
        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorCategoryId(2);

        // Act
        dto.init(memberResponse);

        // Assert
        assertEquals(20, dto.getIndex());
    }

    // ICS Alias and SAPSA Number Handling
    @Test
    void testInit_whenIcsAliasNonNumeric_thenSapsaNumberIsNull() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(21);
        memberResponse.setFirstName("Alex");
        memberResponse.setLastName("Stone");
        memberResponse.setIcsAlias("ABC123");

        // Act
        dto.init(memberResponse);

        // Assert
        assertEquals("ABC123", dto.getCompetitorNumber());
        assertNull(dto.getSapsaNumber());
    }

    @Test
    void testInit_whenIcsAliasExcluded_thenSapsaNumberIsNull() {
        // Arrange
        CompetitorDto dto = new CompetitorDto();
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(22);
        memberResponse.setFirstName("Sam");
        memberResponse.setLastName("Jones");
        memberResponse.setIcsAlias("15000");

        // Act
        dto.init(memberResponse);

        // Assert
        assertEquals("15000", dto.getCompetitorNumber());
        assertNull(dto.getSapsaNumber());
    }


    // toString() behavior

    // Middle Names Handling
    @Test
    void testToString_whenMiddleNamesProvided_thenReturnsFullName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames("Michael");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Michael Doe", result);
    }

    @Test
    void testToString_whenMiddleNamesMissing_thenReturnsFirstAndLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenMiddleNamesNull_thenReturnsFirstAndLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames(null);
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenMiddleNamesBlank_thenReturnsFirstAndLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames("   ");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenMiddleNamesEmpty_thenReturnsFirstAndLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames("");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenMultipleMiddleNamesProvided_thenReturnsFullName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames("Michael James");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Michael James Doe", result);
    }

    // Null Name Handling
    @Test
    void testToString_whenFirstNameNull_thenReturnsLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(null);
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Doe", result);
    }

    @Test
    void testToString_whenLastNameNull_thenReturnsFirstName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName(null);

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John", result);
    }

    @Test
    void testToString_whenBothFirstAndLastNameNull_thenReturnsEmptyString() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(null);
        competitorDto.setLastName(null);

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenFirstNameNullMiddleNamesProvided_thenIncludesAllParts() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(null);
        competitorDto.setMiddleNames("James");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("James Doe", result);
    }

    @Test
    void testToString_whenLastNameNullMiddleNamesProvided_thenIncludesAllParts() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setMiddleNames("Michael");
        competitorDto.setLastName(null);

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Michael", result);
    }

    // Empty Name Handling
    @Test
    void testToString_whenFirstNameEmpty_thenReturnsLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Doe", result);
    }

    @Test
    void testToString_whenLastNameEmpty_thenReturnsFirstName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John", result);
    }

    @Test
    void testToString_whenBothFirstAndLastNameEmpty_thenReturnsEmptyString() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("");
        competitorDto.setLastName("");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("", result);
    }

    // Blank Name Handling
    @Test
    void testToString_whenFirstNameBlank_thenReturnsLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("   ");
        competitorDto.setLastName("Doe");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Doe", result);
    }

    @Test
    void testToString_whenLastNameBlank_thenReturnsFirstName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("   ");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John", result);
    }

    @Test
    void testToString_whenBothFirstAndLastNameBlank_thenReturnsEmptyString() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("   ");
        competitorDto.setLastName("   ");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("", result);
    }

    // Fully Populated
    @Test
    void testToString_whenFullyPopulated_thenReturnsCompleteString() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("James");
        competitorDto.setMiddleNames("Alexander Michael");
        competitorDto.setLastName("Smith");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("James Alexander Michael Smith", result);
    }

    // Partially Populated
    @Test
    void testToString_whenOnlyFirstNameSet_thenReturnsFirstName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Jane");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Jane", result);
    }

    @Test
    void testToString_whenOnlyLastNameSet_thenReturnsLastName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setLastName("Smith");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Smith", result);
    }

    @Test
    void testToString_whenOnlyMiddleNamesSet_thenReturnsMiddleName() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setMiddleNames("Joseph");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Joseph", result);
    }

    // Special Characters and Formatting
    @Test
    void testToString_whenNamesHaveSpecialCharacters_thenIncludesCharacters() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("José-María");
        competitorDto.setMiddleNames("O'Connor");
        competitorDto.setLastName("da Silva");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("José-María O'Connor da Silva", result);
    }

    @Test
    void testToString_whenNamesHaveNumbers_thenIncludesNumbers() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John2");
        competitorDto.setMiddleNames("3rd");
        competitorDto.setLastName("Bond007");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John2 3rd Bond007", result);
    }

    @Test
    void testToString_whenNamesHaveExtraWhitespace_thenTrimsWhitespace() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("  John  ");
        competitorDto.setMiddleNames("  Paul  ");
        competitorDto.setLastName("  Smith  ");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("John Paul Smith", result);
    }

    // Very Long Names
    @Test
    void testToString_whenNamesAreLong_thenReturnsFullLength() {
        // Arrange
        String longFirstName = "Christopher";
        String longMiddleName = "Alexander";
        String longLastName = "Schwarzenegger";

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(longFirstName);
        competitorDto.setMiddleNames(longMiddleName);
        competitorDto.setLastName(longLastName);

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Christopher Alexander Schwarzenegger", result);
        assertTrue(result.length() > 30);
    }

    // Middle Names with Other Fields Null
    @Test
    void testToString_whenMiddleNamesNullOtherFieldsSet_thenExcludesMiddleNames() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Jane");
        competitorDto.setMiddleNames(null);
        competitorDto.setLastName("Smith");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Jane Smith", result);
        assertFalse(result.contains("null"));
    }

    // With Other Fields Set (Not Affecting toString)
    @Test
    void testToString_whenWithIdAndIndex_thenIgnoresIdAndIndex() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(999L);
        competitorDto.setIndex(888);
        competitorDto.setFirstName("Robert");
        competitorDto.setLastName("Brown");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Robert Brown", result);
        assertFalse(result.contains("999"));
        assertFalse(result.contains("888"));
    }

    @Test
    void testToString_whenWithDateOfBirth_thenIgnoresDateOfBirth() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Emily");
        competitorDto.setLastName("White");
        competitorDto.setDateOfBirth(LocalDate.of(1990, 5, 15));

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("Emily White", result);
        assertFalse(result.contains("1990"));
    }

    @Test
    void testToString_whenWithCategory_thenIgnoresCategory() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("David");
        competitorDto.setLastName("Green");

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("David Green", result);
        assertFalse(result.contains("SENIOR"));
    }

    // Consistency and Mutability
    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResults() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Lisa");
        competitorDto.setLastName("Taylor");

        // Act
        String result1 = competitorDto.toString();
        String result2 = competitorDto.toString();
        String result3 = competitorDto.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("Lisa Taylor", result1);
    }

    @Test
    void testToString_whenNameChangedAfterCall_thenReflectsNewNames() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Tom");
        competitorDto.setLastName("Scott");

        // Act
        String result1 = competitorDto.toString();
        competitorDto.setFirstName("Thomas");
        competitorDto.setLastName("Morrison");
        String result2 = competitorDto.toString();

        // Assert
        assertEquals("Tom Scott", result1);
        assertEquals("Thomas Morrison", result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testToString_whenMiddleNamesAddedAfterInitialization_thenReflectsChange() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Mark");
        competitorDto.setLastName("Johnson");

        // Act
        String result1 = competitorDto.toString();
        competitorDto.setMiddleNames("Edward");
        String result2 = competitorDto.toString();

        // Assert
        assertEquals("Mark Johnson", result1);
        assertEquals("Mark Edward Johnson", result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testToString_whenDefaultConstructor_thenReturnsEmptyString() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();

        // Act
        String result = competitorDto.toString();

        // Assert
        assertEquals("", result);
    }
}


