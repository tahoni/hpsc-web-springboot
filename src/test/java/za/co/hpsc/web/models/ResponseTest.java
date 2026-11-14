package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {
    // Response()
    @Test
    void testDefaultConstructor_thenGeneratesUuid() {
        // Act
        Response response = new Response();

        // Assert
        assertNotNull(response.getUuid());
    }

    @Test
    void testDefaultConstructor_thenInstancesHaveUniqueUuids() {
        // Act
        Response response1 = new Response();
        Response response2 = new Response();

        // Assert
        assertNotEquals(response1.getUuid(), response2.getUuid());
    }

    // Response(UUID)
    @Test
    void testShortConstructor_withUuid_thenUsesProvidedValue() {
        // Arrange
        UUID expectedUuid = UUID.randomUUID();

        // Act
        Response response = new Response(expectedUuid);

        // Assert
        assertEquals(expectedUuid, response.getUuid());
    }

    @Test
    void testShortConstructor_withNullUuid_thenGeneratesNewUuid() {
        // Act
        Response response = new Response(null);

        // Assert
        assertNotNull(response.getUuid());
    }
}
