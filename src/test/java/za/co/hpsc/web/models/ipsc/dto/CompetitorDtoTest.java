package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: add tests for other methods
class CompetitorDtoTest {

    @Test
    void testToString_withMiddleNames_thenReturnsFullName() {
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
    void testToString_withoutMiddleNames_thenReturnsFirstAndLastName() {
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
    void testToString_withNullMiddleNames_thenReturnsFirstAndLastName() {
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
    void testToString_withBlankMiddleNames_thenReturnsFirstAndLastName() {
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
    void testToString_withEmptyMiddleNames_thenReturnsFirstAndLastName() {
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
    void testToString_withMultipleMiddleNames_thenReturnsFullName() {
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
}
