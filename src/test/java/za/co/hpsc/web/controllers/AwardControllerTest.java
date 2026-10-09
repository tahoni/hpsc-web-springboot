package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.services.AwardService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AwardControllerTest {

    @Mock
    private AwardService awardService;

    @InjectMocks
    private AwardController awardController;

    private static final String VALID_CSV = """
            title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
            Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to/image,Ceremony 1,Ceremony Summary,Ceremony Description,Ceremony Category,tags1,John Doe,Jane Smith,Bob Jones,w1.png,w2.png,w3.png
            """;

    // createAwards()
    @Test
    void testCreateAwards_whenValidCsvData_thenReturns200() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.createAwards(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.createAwards(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateAwards_whenValidCsvData_thenResponseBodyIsReturnedFromService() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.createAwards(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.createAwards(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertSame(holder, response.getBody());
    }

}
