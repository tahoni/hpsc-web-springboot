package za.co.hpsc.web.services.impl;

public class IpscMatchIntegrationTest {
/*
    @Test
    void testUpdateMatch_withExistingMatchAndFullUpdate_thenOverwritesPersistedValues() throws Exception {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(21L, "Existing Match", "Existing Club",
                FirearmType.HANDGUN, 2026, 8, 15, 10, 0);
        when(matchEntityService.findMatchById(21L)).thenReturn(Optional.of(existingMatch));
        when(transformationService.mapMatchOnly(any(MatchOnlyRequest.class))).thenReturn(Optional.empty());

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(null);
        incoming.setMatchDate(LocalDateTime.of(2026, 8, 15, 10, 0));
        incoming.setClub(null);
        incoming.setFirearm("Shotgun");
        incoming.setSquadCount(null);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.updateMatch(21L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(21L, result.get().getMatchId());
        assertNull(result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 8, 15, 10, 0), result.get().getMatchDate());
        assertNull(result.get().getClub());
        assertEquals("Shotgun", result.get().getFirearm());
        assertEquals(0, result.get().getSquadCount());

        ArgumentCaptor<MatchOnlyRequest> captor = ArgumentCaptor.forClass(MatchOnlyRequest.class);
        verify(transformationService).mapMatchOnly(captor.capture());
        assertNull(captor.getValue().getMatchName());
        assertNull(captor.getValue().getClub());
        assertEquals("Shotgun", captor.getValue().getFirearm());
    }

    @Test
    void testInsertMatch_withMappedAndInitialisedEntities_thenSavesAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(7L, "Inserted Match", "HPSC", "Handgun", 6);

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(7L);
        MatchOnlyResultsDto matchOnlyResultsDto = new MatchOnlyResultsDto(matchOnlyDto, null);

        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.of(matchOnlyDto));
        when(domainService.initMatchOnlyEntities(matchOnlyDto)).thenReturn(Optional.of(matchOnlyResultsDto));

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getMatchId());
        assertEquals("Inserted Match", result.get().getMatchName());
        assertEquals("HPSC", result.get().getClub());
        assertEquals("Handgun", result.get().getFirearm());
        assertEquals(6, result.get().getSquadCount());
        verify(transformationService).mapMatchOnly(request);
        verify(domainService).initMatchOnlyEntities(matchOnlyDto);
        verify(transactionService).saveMatch(matchOnlyResultsDto);
    }

    @Test
    void testInsertMatch_withMapMatchOnlyEmpty_thenSkipsDomainAndTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(8L, "No Mapping Match", "HPSC", "Rifle", 4);
        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.empty());

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(8L, result.get().getMatchId());
        verify(transformationService).mapMatchOnly(request);
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void testInsertMatch_withDomainInitialisationEmpty_thenSkipsTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(9L, "No Domain Match", "HPSC", "Shotgun", 2);
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(9L);
        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.of(matchOnlyDto));
        when(domainService.initMatchOnlyEntities(matchOnlyDto)).thenReturn(Optional.empty());

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(9L, result.get().getMatchId());
        verify(transformationService).mapMatchOnly(request);
        verify(domainService).initMatchOnlyEntities(matchOnlyDto);
        verifyNoInteractions(transactionService);
    }

*/
}
