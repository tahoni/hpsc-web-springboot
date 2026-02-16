package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: add tests for other methods
public class ClubDtoTest {

    @Test
    void testToString_withAbbreviation_thenReturnsNameWithAbbreviation() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_withoutAbbreviation_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_withNullAbbreviation_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_withBlankAbbreviation_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_withEmptyAbbreviation_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }
}
