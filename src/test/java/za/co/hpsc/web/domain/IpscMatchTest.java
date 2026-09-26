package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpscMatchTest {

    // IpscMatch()
    @Test
    void testConstructor_whenCreated_thenStagesIsEmptyAndMutable() {
        // Act
        IpscMatch match = new IpscMatch();

        // Assert
        assertNotNull(match.getStages());
        assertTrue(match.getStages().isEmpty());
        assertDoesNotThrow(() -> match.getStages().add(new IpscMatchStage()));
    }

    // equals()
    @Test
    void testEquals_whenOnlyStagesDiffer_thenReturnsTrue() {
        // Arrange
        IpscMatch match = createMatch();
        IpscMatch other = createMatch();
        addStage(other, 1);

        // Act & Assert
        assertEquals(match, other);
    }

    // hashCode()
    @Test
    void testHashCode_whenStageLinksBackToMatch_thenDoesNotRecurse() {
        // Arrange
        IpscMatch match = createMatch();
        IpscMatchStage stage = addStage(match, 1);

        // Act & Assert
        assertDoesNotThrow(match::hashCode);
        assertDoesNotThrow(stage::hashCode);
    }

    // toString()
    @Test
    void testToString_whenStageLinksBackToMatch_thenDoesNotRecurse() {
        // Arrange
        IpscMatch match = createMatch();
        IpscMatchStage stage = addStage(match, 1);

        // Act
        String result = assertDoesNotThrow(match::toString);

        // Assert
        assertTrue(result.contains("Club Championship"));
        assertFalse(result.contains("stages"));
        assertDoesNotThrow(stage::toString);
    }

    // Helpers
    private IpscMatch createMatch() {
        IpscMatch match = new IpscMatch();
        match.setName("Club Championship");
        return match;
    }

    private IpscMatchStage addStage(IpscMatch match, int stageNumber) {
        IpscMatchStage stage = new IpscMatchStage();
        stage.setMatch(match);
        stage.setStageNumber(stageNumber);
        match.getStages().add(stage);
        return stage;
    }
}
