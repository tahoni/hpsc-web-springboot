package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {
    @Test
    void testDefaultConstructor_thenGeneratesUuid() {
        // Act
        Response response = new Response();

        // Assert
        assertNotNull(response.getUuid());
    }

    @Test
    void testConstructor_withUuid_thenUsesProvidedValue() {
        // Arrange
        UUID expectedUuid = UUID.randomUUID();

        // Act
        Response response = new Response(expectedUuid);

        // Assert
        assertEquals(expectedUuid, response.getUuid());
    }

    @Test
    void testConstructor_withNullUuid_thenGeneratesNewUuid() {
        // Act
        Response response = new Response(null);

        // Assert
        assertNotNull(response.getUuid());
    }

    @Test
    void testFullConstructor_thenInitializesAllFields() {
        // Arrange
        UUID expectedUuid = UUID.randomUUID();
        String title = "Test Title";
        String summary = "Test Summary";
        String description = "Test Description";
        String category = "Test Category";
        List<String> tags = List.of("tag1", "tag2");

        // Act
        Response response = new Response(expectedUuid, title, summary, description, category, tags);

        // Assert
        assertEquals(expectedUuid, response.getUuid());
        assertEquals(title, response.getTitle());
        assertEquals(summary, response.getSummary());
        assertEquals(description, response.getDescription());
        assertEquals(category, response.getCategory());
        assertEquals(tags, response.getTags());
    }

    @Test
    void testShortConstructor_thenInitializesUuidAndTitle() {
        // Arrange
        UUID expectedUuid = UUID.randomUUID();
        String title = "Brief Title";

        // Act
        Response response = new Response(expectedUuid, title);

        // Assert
        assertEquals(expectedUuid, response.getUuid());
        assertEquals(title, response.getTitle());
    }

    @Test
    void testDefaultConstructor_thenInstancesHaveUniqueUuids() {
        // Act
        Response response1 = new Response();
        Response response2 = new Response();

        // Assert
        assertNotEquals(response1.getUuid(), response2.getUuid());
    }
}
