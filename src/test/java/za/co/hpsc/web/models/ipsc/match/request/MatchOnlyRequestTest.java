package za.co.hpsc.web.models.ipsc.match.request;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MatchOnlyRequestTest {

    @Test
    void testNoArgsConstructor_withNoArguments_thenCreatesInstanceWithNullFields() {
        // Act
        MatchOnlyRequest request = new MatchOnlyRequest();

        // Assert
        assertNull(request.getMatchId());
        assertNull(request.getMatchName());
        assertNull(request.getMatchDate());
        assertNull(request.getClub());
        assertNull(request.getFirearm());
        assertEquals(0, request.getSquadCount());
    }

    @Test
    void testAllArgsConstructor_withAllFieldsProvided_thenCopiesAllFieldsExactly() {
        // Arrange
        LocalDateTime matchDate = LocalDateTime.of(2026, 5, 10, 9, 0);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(1L, "Spring Classic", matchDate, "Test Club", "Handgun", 8);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals("Spring Classic", request.getMatchName());
        assertEquals(matchDate, request.getMatchDate());
        assertEquals("Test Club", request.getClub());
        assertEquals("Handgun", request.getFirearm());
        assertEquals(8, request.getSquadCount());
    }

    @Test
    void testAllArgsConstructor_withOptionalFieldsNull_thenSetsNulls() {
        // Arrange
        LocalDateTime matchDate = LocalDateTime.of(2026, 6, 1, 10, 0);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(2L, null, matchDate, null, null, null);

        // Assert
        assertEquals(2L, request.getMatchId());
        assertNull(request.getMatchName());
        assertNull(request.getClub());
        assertNull(request.getFirearm());
        assertNull(request.getSquadCount());
    }

    @Test
    void testMatchDtoConstructor_withClubAndFirearmTypeProvided_thenMapsAllFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC Club");

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("Regional Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        matchOnlyDto.setClub(clubDto);
        matchOnlyDto.setMatchFirearmType(FirearmType.HANDGUN);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(42L, matchOnlyDto);

        // Assert
        assertEquals(42L, request.getMatchId());
        assertEquals("Regional Match", request.getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 15, 10, 0), request.getMatchDate());
        assertEquals("HPSC Club", request.getClub());
        assertEquals("Handgun", request.getFirearm());
    }

    @Test
    void testMatchDtoConstructor_withNullClub_thenKeepsClubNull() {
        // Arrange
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("No Club Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 7, 1, 8, 0));
        matchOnlyDto.setClub(null);
        matchOnlyDto.setMatchFirearmType(FirearmType.RIFLE);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(10L, matchOnlyDto);

        // Assert
        assertEquals(10L, request.getMatchId());
        assertNull(request.getClub());
        assertEquals("Rifle", request.getFirearm());
    }

    @Test
    void testMatchDtoConstructor_withNullFirearmType_thenKeepsFirearmNull() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Open Club");

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("Open Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 8, 20, 14, 0));
        matchOnlyDto.setClub(clubDto);
        matchOnlyDto.setMatchFirearmType(null);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(5L, matchOnlyDto);

        // Assert
        assertEquals(5L, request.getMatchId());
        assertEquals("Open Club", request.getClub());
        assertNull(request.getFirearm());
    }

    @Test
    void testMatchDtoConstructor_withNullClubAndFirearmType_thenKeepsBothNull() {
        // Arrange
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("Minimal Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 9, 5, 11, 0));
        matchOnlyDto.setClub(null);
        matchOnlyDto.setMatchFirearmType(null);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(7L, matchOnlyDto);

        // Assert
        assertEquals(7L, request.getMatchId());
        assertEquals("Minimal Match", request.getMatchName());
        assertNull(request.getClub());
        assertNull(request.getFirearm());
    }

    @Test
    void testMatchDtoConstructor_withFirearmTypeHavingMultipleNames_thenUsesFirstName() {
        // Arrange
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("PCC Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 10, 1, 10, 0));
        matchOnlyDto.setMatchFirearmType(FirearmType.PCC);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(15L, matchOnlyDto);

        // Assert
        assertEquals("PCC", request.getFirearm());
    }

    @Test
    void testMatchDtoConstructor_withNewInstance_thenSetsSquadCountToDefault() {
        // Arrange
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("Default Squad Count Match");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 11, 10, 9, 0));

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(20L, matchOnlyDto);

        // Assert
        assertEquals(0, request.getSquadCount());
    }

    @Test
    void testCopyConstructor_withFullyPopulatedSource_thenCopiesAllFieldsFromSource() {
        // Arrange
        LocalDateTime matchDate = LocalDateTime.of(2026, 5, 20, 13, 0);
        MatchOnlyRequest original = new MatchOnlyRequest(99L, "Copy Match", matchDate, "Copy Club", "Handgun", 6);

        // Act
        MatchOnlyRequest copy = new MatchOnlyRequest(original);

        // Assert
        assertEquals(original.getMatchId(), copy.getMatchId());
        assertEquals(original.getMatchName(), copy.getMatchName());
        assertEquals(original.getMatchDate(), copy.getMatchDate());
        assertEquals(original.getClub(), copy.getClub());
        assertEquals(original.getFirearm(), copy.getFirearm());
        assertEquals(original.getSquadCount(), copy.getSquadCount());
    }

    @Test
    void testCopyConstructor_withOptionalFieldsNull_thenCopiesNulls() {
        // Arrange
        MatchOnlyRequest original = new MatchOnlyRequest();
        original.setMatchId(3L);
        original.setMatchDate(LocalDateTime.of(2026, 11, 1, 9, 0));

        // Act
        MatchOnlyRequest copy = new MatchOnlyRequest(original);

        // Assert
        assertEquals(3L, copy.getMatchId());
        assertNull(copy.getMatchName());
        assertNull(copy.getClub());
        assertNull(copy.getFirearm());
    }

    @Test
    void testCopyConstructor_withSourceModifiedAfterCopy_thenProducesIndependentInstance() {
        // Arrange
        LocalDateTime matchDate = LocalDateTime.of(2026, 6, 1, 10, 0);
        MatchOnlyRequest original = new MatchOnlyRequest(50L, "Independence Test", matchDate, "Club A", "Rifle", 4);

        // Act
        MatchOnlyRequest copy = new MatchOnlyRequest(original);
        original.setMatchName("Modified Name");

        // Assert
        assertEquals("Independence Test", copy.getMatchName());
    }

    @Test
    void testCopyConstructor_withZeroSquadCount_thenCopiesZero() {
        // Arrange
        MatchOnlyRequest original = new MatchOnlyRequest(
                8L, "Zero Squad Match", LocalDateTime.of(2026, 12, 1, 10, 0), "Club B", "Shotgun", 0);

        // Act
        MatchOnlyRequest copy = new MatchOnlyRequest(original);

        // Assert
        assertEquals(0, copy.getSquadCount());
    }
}

