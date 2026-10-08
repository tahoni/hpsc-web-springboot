package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponseHolder;
import za.co.hpsc.web.services.IpscCompetitorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscCompetitorControllerTest {

    @Mock
    private IpscCompetitorService ipscCompetitorService;

    @InjectMocks
    private IpscCompetitorController ipscCompetitorController;

    // createCompetitor()
    @Test
    void testCreateCompetitor_whenServiceSucceeds_thenReturns201() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.createCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.createCompetitor(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateCompetitor_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.createCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.createCompetitor(request);

        // Assert
        assertSame(response, result.getBody());
    }

    // createCompetitors()
    private static final String VALID_CSV = """
            FirstName,LastName,MiddleNames,NickName,DateOfBirth,Gender,HomeClub,SapsaNumber,CompetitorNumber,ClubNumber,IdNumber,CellphoneNumber,EmailAddresses,PaidUpSapsa,PaidUpClub
            John,Doe,,,,,,,,CLUB001,,,
            """;

    @Test
    void testCreateCompetitors_whenServiceSucceeds_thenReturns201() throws ValidationException, NonFatalException, FatalException {
        // Arrange
        CompetitorResponseHolder holder = new CompetitorResponseHolder(List.of());
        when(ipscCompetitorService.createCompetitors(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<CompetitorResponseHolder> result = ipscCompetitorController.createCompetitors(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateCompetitors_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws ValidationException, NonFatalException, FatalException {
        // Arrange
        CompetitorResponseHolder holder = new CompetitorResponseHolder(List.of(new CompetitorResponse()));
        when(ipscCompetitorService.createCompetitors(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<CompetitorResponseHolder> result = ipscCompetitorController.createCompetitors(VALID_CSV);

        // Assert
        assertSame(holder, result.getBody());
    }

    // deleteCompetitor()
    @Test
    void testDeleteCompetitor_whenServiceSucceeds_thenReturns204() throws ValidationException, NonFatalException {
        // Act
        ResponseEntity<Void> result = ipscCompetitorController.deleteCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void testDeleteCompetitor_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Act
        ipscCompetitorController.deleteCompetitor(1L);

        // Assert
        verify(ipscCompetitorService).deleteCompetitor(1L);
        verifyNoMoreInteractions(ipscCompetitorService);
    }

    // getAllCompetitors()
    @Test
    void testGetAllCompetitors_whenServiceSucceeds_thenReturns200() {
        // Arrange
        List<CompetitorResponse> response = List.of(new CompetitorResponse());
        when(ipscCompetitorService.getAllCompetitors()).thenReturn(response);

        // Act
        ResponseEntity<List<CompetitorResponse>> result = ipscCompetitorController.getAllCompetitors();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenServiceSucceeds_thenReturns200() throws NonFatalException {
        // Arrange
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.getCompetitor(1L)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.getCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // patchCompetitor()
    @Test
    void testPatchCompetitor_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorPatchRequest request = new CompetitorPatchRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.patchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.patchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.updateCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.updateCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

}
