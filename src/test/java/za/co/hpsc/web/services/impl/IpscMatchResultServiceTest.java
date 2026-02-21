package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;
import za.co.hpsc.web.services.MatchEntityService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IpscMatchResultServiceTest {
    @Mock
    private MatchEntityService matchEntityService;

    @InjectMocks
    private IpscMatchResultServiceImpl ipscMatchResultService;

    private MatchResultsDto matchResultsDto;
    private MatchDto matchDto;
    private Club clubEntity;
    private IpscMatch matchEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        ClubDto clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        clubEntity = new Club();
        clubEntity.setId(1L);
        clubEntity.setName("Test Club");
        clubEntity.setAbbreviation("TC");

        matchDto = new MatchDto();
        matchDto.setId(100L);
        matchDto.setName("Test Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchDto.setClub(clubDto);

        matchEntity = new IpscMatch();
        matchEntity.setId(100L);
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchEntity.setClub(clubEntity);

        matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setClub(clubDto);
    }

    @Test
    public void testInitMatch_withExistingMatchAndNoNewerScores_thenReturnsEmptyOptional() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("Existing Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 10, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

        when(matchEntityService.findMatchByName("Existing Match"))
                .thenReturn(Optional.of(existingMatch));

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Test Club");
        clubResponse.setClubCode("TC");
        ClubDto clubDto = new ClubDto(clubResponse);

        // Act
        Optional<MatchDto> result = ipscMatchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isEmpty());
        verify(matchEntityService, times(1)).findMatchByName("Existing Match");
    }

    @Test
    public void testInitMatch_withExistingMatchAndNewerScores_thenReturnsPopulatedMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("Existing Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 25, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

        when(matchEntityService.findMatchByName("Existing Match"))
                .thenReturn(Optional.of(existingMatch));

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(1);
        clubResponse.setClubName("Test Club");
        clubResponse.setClubCode("TC");
        ClubDto clubDto = new ClubDto(clubResponse);

        // Act
        Optional<MatchDto> result = ipscMatchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent());
        MatchDto matchDto = result.get();
        assertEquals(1L, matchDto.getId());
        assertEquals(clubDto.getId(), matchDto.getClub().getId());
        verify(matchEntityService, times(1)).findMatchByName("Existing Match");
    }

    @Test
    public void testInitMatch_withNonExistingMatch_thenReturnsNewMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("New Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 2, 1).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 1, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        when(matchEntityService.findMatchByName("New Match"))
                .thenReturn(Optional.empty());

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubId(0);
        clubResponse.setClubName("New Club");
        clubResponse.setClubCode("NC");
        ClubDto clubDto = new ClubDto(clubResponse);

        // Act
        Optional<MatchDto> result = ipscMatchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent());
        MatchDto matchDto = result.get();
        assertNull(matchDto.getId());
        assertEquals(clubDto.getId(), matchDto.getClub().getId());
        verify(matchEntityService, times(1)).findMatchByName("New Match");
    }
}
