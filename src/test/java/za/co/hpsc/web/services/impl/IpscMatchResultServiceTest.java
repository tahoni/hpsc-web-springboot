package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
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

        when(matchEntityService.findMatch("Existing Match"))
                .thenReturn(Optional.of(existingMatch));

        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Test Club");
        clubResponse.setClubCode("TC");
        ClubDto clubDto = new ClubDto(clubResponse);

        // Act
        Optional<MatchDto> result = ipscMatchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isEmpty());
        verify(matchEntityService, times(1)).findMatch("Existing Match");
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

        when(matchEntityService.findMatch("Existing Match"))
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
        assertEquals(clubDto, matchDto.getClub());
        verify(matchEntityService, times(1)).findMatch("Existing Match");
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

        when(matchEntityService.findMatch("New Match"))
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
        assertEquals(clubDto, matchDto.getClub());
        verify(matchEntityService, times(1)).findMatch("New Match");
    }
}
