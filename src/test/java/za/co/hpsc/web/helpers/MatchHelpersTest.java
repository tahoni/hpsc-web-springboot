package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import za.co.hpsc.web.constants.MatchConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.domain.MatchStage;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.utils.StringUtil;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchHelpersTest {

    @Test
    void testGetMatchDisplayName_withFullDetails_thenReturnsFormattedString() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Lions Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(Division.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setScheduledDate(LocalDate.of(2023, 5, 20));

        // Expected parameters used in the formatted string
        String expectedDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_NAME_FORMAT,
                Map.of(
                        "clubName", "Lions Club",
                        "divisionName", Division.HANDGUN.getDisplayName().toUpperCase(),
                        "categoryName", MatchCategory.CLUB_SHOOT.getDisplayName(),
                        "longDate", "20 May 2023"
                )
        );

        // Verify the method output
        assertEquals(expectedDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchDisplayName(match));
    }

    @Test
    void testGetMatchDisplayName_withNullDivisionAndCategory_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Tigers Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2024, 3, 15));

        // Expected parameters used in the formatted string
        String expectedDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_NAME_FORMAT,
                Map.of(
                        "clubName", "Tigers Club",
                        "divisionName", "",
                        "categoryName", "",
                        "longDate", "15 March 2024"
                )
        );

        // Verify the method output
        assertEquals(expectedDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchDisplayName(match));
    }

    @Test
    void testGetMatchDisplayName_withMinimalClubOnly_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Phoenix Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2025, 11, 12));

        // Expected parameters used in the formatted string
        String expectedDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_NAME_FORMAT,
                Map.of(
                        "clubName", "Phoenix Club",
                        "divisionName", "",
                        "categoryName", "",
                        "longDate", "12 November 2025"
                )
        );

        // Verify the method output
        assertEquals(expectedDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchDisplayName(match));
    }

    @Test
    void testGetMatchOverallDisplayName_withFullDetails_thenReturnsFormattedString() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Lions Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(Division.PCC);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setScheduledDate(LocalDate.of(2023, 5, 20));

        // Expected overall display name
        String expectedOverallDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                Map.of(
                        "matchName", "Lions Club PCC League - 20 May 2023",
                        "isoDate", "2023-05-20"
                )
        );

        // Verify the method output
        assertEquals(expectedOverallDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchOverallDisplayName(match));
    }

    @Test
    void testGetMatchOverallDisplayName_withNullDivisionAndCategory_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Tigers Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2024, 3, 15));

        // Expected overall display name
        String expectedOverallDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                Map.of(
                        "matchName", "Tigers Club - 15 March 2024",
                        "isoDate", "2024-03-15"
                )
        );

        // Verify the method output
        assertEquals(expectedOverallDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchOverallDisplayName(match));
    }

    @Test
    void testGetMatchOverallDisplayName_withMinimalClubOnly_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Phoenix Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2025, 11, 12));

        // Expected overall display name
        String expectedOverallDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                Map.of(
                        "matchName", "Phoenix Club - 12 November 2025",
                        "isoDate", "2025-11-12"
                )
        );

        // Verify the method output
        assertEquals(expectedOverallDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchOverallDisplayName(match));
    }

    @Test
    void testGetMatchOverallDisplayName_withPartialDetails_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Eagles Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(Division.HANDGUN_22);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2026, 8, 3));

        // Expected overall display name
        String expectedOverallDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_OVERALL_NAME_FORMAT,
                Map.of(
                        "matchName", "Eagles Club HANDGUN .22 - 03 August 2026",
                        "isoDate", "2026-08-03"
                )
        );

        // Verify the method output
        assertEquals(expectedOverallDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchOverallDisplayName(match));
    }

    @Test
    void testGetMatchStageDisplayName_withFullDetails_thenReturnsFormattedString() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Lions Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(Division.SHOTGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setScheduledDate(LocalDate.of(2023, 5, 20));

        MatchStage matchStage = new MatchStage();
        matchStage.setMatch(match);
        matchStage.setStageNumber(3);
        matchStage.setRangeNumber(5);

        // Expected stage display name
        String expectedStageDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                Map.of(
                        "matchName", "Lions Club SHOTGUN League - 20 May 2023",
                        "stageNumber", "3",
                        "rangeNumber", "5",
                        "isoDate", "2023-05-20"
                )
        );

        // Verify the method output
        assertEquals(expectedStageDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchStageDisplayName(matchStage));
    }

    @Test
    void testGetMatchStageDisplayName_withPartialDetails_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Tigers Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2024, 3, 15));

        MatchStage matchStage = new MatchStage();
        matchStage.setMatch(match);
        matchStage.setStageNumber(null);
        matchStage.setRangeNumber(2);

        // Expected stage display name
        String expectedStageDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                Map.of(
                        "matchName", "Tigers Club - 15 March 2024",
                        "stageNumber", "0", // Null values default to 0
                        "rangeNumber", "2",
                        "isoDate", "2024-03-15"
                )
        );

        // Verify the method output
        assertEquals(expectedStageDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchStageDisplayName(matchStage));
    }

    @Test
    void testGetMatchStageDisplayName_withMinimalDetails_thenReturnsFormattedStringWithPartialData() {
        // Prepare a mock match object
        Club mockClub = Mockito.mock(Club.class);
        Mockito.when(mockClub.getName()).thenReturn("Phoenix Club");

        Match match = new Match();
        match.setClub(mockClub);
        match.setMatchDivision(null);
        match.setMatchCategory(null);
        match.setScheduledDate(LocalDate.of(2025, 11, 12));

        MatchStage matchStage = new MatchStage();
        matchStage.setMatch(match);
        matchStage.setStageNumber(null);
        matchStage.setRangeNumber(null);

        // Expected stage display name
        String expectedStageDisplayName = StringUtil.formatStringWithNamedParameters(
                MatchConstants.SCHEDULED_MATCH_STAGE_NAME_FORMAT,
                Map.of(
                        "matchName", "Phoenix Club - 12 November 2025",
                        "stageNumber", "0", // Null values default to 0
                        "rangeNumber", "0", // Null values default to 0
                        "isoDate", "2025-11-12"
                )
        );

        // Verify the method output
        assertEquals(expectedStageDisplayName.replaceAll("\\s+", " "),
                MatchHelpers.getMatchStageDisplayName(matchStage));
    }
}