package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    // Request(String)
    @Test
    void testShortConstructor_whenTitleProvided_thenSetsTitleAndInitialisesEmptyTags() {
        // Arrange
        String title = "My Request";

        // Act
        Request request = new Request(title);

        // Assert
        assertEquals("My Request", request.getTitle());
        assertNull(request.getSummary());
        assertNull(request.getDescription());
        assertNull(request.getCategory());
        assertNotNull(request.getTags());
        assertTrue(request.getTags().isEmpty());
    }

    // Request(String, String, String, String, List<String>)
    @Test
    void testFullConstructor_whenTagsNull_thenInitialisesTagsAsEmptyList() {
        // Arrange
        String title = "Title";

        // Act
        Request request = new Request(title, "summary", "description", "category", null);

        // Assert
        assertNotNull(request.getTags());
        assertTrue(request.getTags().isEmpty());
    }
}
