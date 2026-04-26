package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    @Test
    void testDefaultConstructor_whenInvoked_thenInitializesWithEmptyTagsAndNullTextFields() {
        // Arrange & Act
        Request request = new Request();

        // Assert
        assertNull(request.getTitle());
        assertNull(request.getSummary());
        assertNull(request.getDescription());
        assertNull(request.getCategory());
        assertNotNull(request.getTags());
        assertTrue(request.getTags().isEmpty());
    }

    @Test
    void testConstructorWithTitle_whenTitleProvided_thenSetsTitleAndInitializesEmptyTags() {
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

    @Test
    void testFullConstructor_whenAllFieldsProvided_thenMapsAllValues() {
        // Arrange
        String title = "Feature Request";
        String summary = "Short summary";
        String description = "Detailed description";
        String category = "Enhancement";
        List<String> tags = new ArrayList<>(List.of("api", "backend"));

        // Act
        Request request = new Request(title, summary, description, category, tags);

        // Assert
        assertEquals(title, request.getTitle());
        assertEquals(summary, request.getSummary());
        assertEquals(description, request.getDescription());
        assertEquals(category, request.getCategory());
        assertEquals(tags, request.getTags());
    }

    @Test
    void testFullConstructor_whenTagsNull_thenInitializesTagsAsEmptyList() {
        // Arrange
        String title = "Title";

        // Act
        Request request = new Request(title, "summary", "description", "category", null);

        // Assert
        assertNotNull(request.getTags());
        assertTrue(request.getTags().isEmpty());
    }

    @Test
    void testFullConstructor_whenTagsProvided_thenKeepsSameListReference() {
        // Arrange
        List<String> tags = new ArrayList<>(List.of("one", "two"));

        // Act
        Request request = new Request("Title", "summary", "description", "category", tags);

        // Assert
        assertSame(tags, request.getTags());
    }

    @Test
    void testConstructors_whenTitleIsBlankOrNull_thenRetainProvidedValueWithoutRuntimeValidation() {
        // Arrange & Act
        Request blankTitleRequest = new Request(" ");
        Request nullTitleRequest = new Request(null);
        Request fullConstructorNullTitleRequest =
                new Request(null, "summary", "description", "category", List.of("tag"));

        // Assert
        assertEquals(" ", blankTitleRequest.getTitle());
        assertNull(nullTitleRequest.getTitle());
        assertNull(fullConstructorNullTitleRequest.getTitle());
    }
}
