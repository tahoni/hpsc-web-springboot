package za.co.hpsc.web.models.ipsc.match.request;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchOnlyRequestTest {

    // No-args constructor

    @Test
    void noArgsConstructor_createsInstanceWithNullFields() {
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

    // All-args constructor

    @Test
    void allArgsConstructor_copiesAllFieldsExactly() {
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
    void allArgsConstructor_whenOptionalFieldsAreNull_thenSetsNulls() {
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

    // MatchDto constructor

    @Test
    void matchDtoConstructor_whenMatchDtoHasClubAndFirearmType_thenMapsAllFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC Club");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Regional Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        matchDto.setClub(clubDto);
        matchDto.setMatchFirearmType(FirearmType.HANDGUN);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(42L, matchDto);

        // Assert
        assertEquals(42L, request.getMatchId());
        assertEquals("Regional Match", request.getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 15, 10, 0), request.getMatchDate());
        assertEquals("HPSC Club", request.getClub());
        assertEquals("Handgun", request.getFirearm());
    }

    @Test
    void matchDtoConstructor_whenClubIsNull_thenClubIsNull() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("No Club Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 7, 1, 8, 0));
        matchDto.setClub(null);
        matchDto.setMatchFirearmType(FirearmType.RIFLE);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(10L, matchDto);

        // Assert
        assertEquals(10L, request.getMatchId());
        assertNull(request.getClub());
        assertEquals("Rifle", request.getFirearm());
    }

    @Test
    void matchDtoConstructor_whenFirearmTypeIsNull_thenFirearmIsNull() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Open Club");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Open Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 8, 20, 14, 0));
        matchDto.setClub(clubDto);
        matchDto.setMatchFirearmType(null);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(5L, matchDto);

        // Assert
        assertEquals(5L, request.getMatchId());
        assertEquals("Open Club", request.getClub());
        assertNull(request.getFirearm());
    }

    @Test
    void matchDtoConstructor_whenBothClubAndFirearmTypeAreNull_thenBothAreNull() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Minimal Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 9, 5, 11, 0));
        matchDto.setClub(null);
        matchDto.setMatchFirearmType(null);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(7L, matchDto);

        // Assert
        assertEquals(7L, request.getMatchId());
        assertEquals("Minimal Match", request.getMatchName());
        assertNull(request.getClub());
        assertNull(request.getFirearm());
    }

    @Test
    void matchDtoConstructor_whenFirearmTypeHasMultipleNames_thenUsesFirstName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("PCC Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 10, 1, 10, 0));
        matchDto.setMatchFirearmType(FirearmType.PCC);

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(15L, matchDto);

        // Assert
        assertEquals("PCC", request.getFirearm());
    }

    @Test
    void matchDtoConstructor_setsSquadCountToDefault() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Default Squad Count Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 11, 10, 9, 0));

        // Act
        MatchOnlyRequest request = new MatchOnlyRequest(20L, matchDto);

        // Assert
        assertEquals(0, request.getSquadCount());
    }

    // Copy constructor

    @Test
    void copyConstructor_copiesAllFieldsFromSource() {
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
    void copyConstructor_whenOptionalFieldsAreNull_thenCopiesNulls() {
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
    void copyConstructor_producesIndependentInstance() {
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
    void copyConstructor_whenSquadCountIsZero_thenCopiesZero() {
        // Arrange
        MatchOnlyRequest original = new MatchOnlyRequest(
                8L, "Zero Squad Match", LocalDateTime.of(2026, 12, 1, 10, 0), "Club B", "Shotgun", 0);

        // Act
        MatchOnlyRequest copy = new MatchOnlyRequest(original);

        // Assert
        assertEquals(0, copy.getSquadCount());
    }
}

