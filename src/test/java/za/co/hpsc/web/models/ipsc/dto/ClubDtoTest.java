package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClubDtoTest {

    @Test
    void testConstructor_withClubEntity_thenMapsFields() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Alpha Club");
        club.setAbbreviation("AC");

        // Act
        ClubDto dto = new ClubDto(club);

        // Assert
        assertEquals(10L, dto.getId());
        assertEquals("Alpha Club", dto.getName());
        assertEquals("AC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubResponse_thenMapsFields() {
        // Arrange
        ClubResponse response = new ClubResponse();
        response.setClubId(7);
        response.setClubName("Bravo Club");
        response.setClubCode("BC");

        // Act
        ClubDto dto = new ClubDto(response);

        // Assert
        assertEquals(7, dto.getIndex());
        assertEquals("Bravo Club", dto.getName());
        assertEquals("BC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubIdentifier_thenMapsNameAndAbbreviation() {
        // Arrange
        ClubIdentifier identifier = ClubIdentifier.values()[0];
        String expectedName = identifier.getName();

        // Act
        ClubDto dto = new ClubDto(identifier);

        // Assert
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedName, dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubAndIdentifier_whenClubNotNull_thenUsesClub() {
        // Arrange
        Club club = new Club();
        club.setId(22L);
        club.setName("Charlie Club");
        club.setAbbreviation("CC");

        ClubIdentifier identifier = ClubIdentifier.values()[0];

        // Act
        ClubDto dto = new ClubDto(club, identifier);

        // Assert
        assertEquals(22L, dto.getId());
        assertEquals("Charlie Club", dto.getName());
        assertEquals("CC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubAndIdentifier_whenClubNull_thenUsesIdentifier() {
        // Arrange
        ClubIdentifier identifier = ClubIdentifier.values()[0];
        String expectedName = identifier.getName();

        // Act
        ClubDto dto = new ClubDto(null, identifier);

        // Assert
        assertNull(dto.getId());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedName, dto.getAbbreviation());
    }

    @Test
    void testInit_withClubResponse_thenMapsFields() {
        // Arrange
        ClubDto dto = new ClubDto();
        ClubResponse response = new ClubResponse();
        response.setClubId(15);
        response.setClubName("Delta Club");
        response.setClubCode("DC");

        // Act
        dto.init(response);

        // Assert
        assertEquals(15, dto.getIndex());
        assertEquals("Delta Club", dto.getName());
        assertEquals("DC", dto.getAbbreviation());
    }

    @Test
    void testInit_withNullClubResponse_thenNoChanges() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setIndex(3);
        dto.setName("Echo Club");
        dto.setAbbreviation("EC");

        // Act
        dto.init(null);

        // Assert
        assertEquals(3, dto.getIndex());
        assertEquals("Echo Club", dto.getName());
        assertEquals("EC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_withNullClubEntity_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((Club) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getId());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_withNullClubResponse_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((ClubResponse) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getIndex());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_withNullClubIdentifier_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((ClubIdentifier) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubAndIdentifier_whenBothNull_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto(null, null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getId());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubResponse_nullFields_thenKeepsNulls() {
        // Arrange
        ClubResponse response = new ClubResponse();

        // Act
        ClubDto dto = new ClubDto(response);

        // Assert
        assertNull(dto.getIndex());
        assertNull(dto.getName());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testInit_withClubResponse_nullFields_thenOverridesToNulls() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setIndex(9);
        dto.setName("Foxtrot Club");
        dto.setAbbreviation("FC");
        ClubResponse response = new ClubResponse();

        // Act
        dto.init(response);

        // Assert
        assertNull(dto.getIndex());
        assertNull(dto.getName());
        assertNull(dto.getAbbreviation());
    }

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

    @Test
    void testToString_withAbbreviationSameAsName_thenReturnsNameOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_withNullName_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_withEmptyName_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_withBlankName_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_withEmptyNameAndNullAbbreviation_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_withEmptyNameAndEmptyAbbreviation_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_withEmptyNameAndBlankAbbreviation_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_withNullNameAndEmptyAbbreviation_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_withNullNameAndBlankAbbreviation_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_withBlankNameAndNullAbbreviation_thenReturnsBlankName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }
}
