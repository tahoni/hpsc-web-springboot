package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscServiceImplTest {

    @InjectMocks
    private IpscServiceImpl ipscService;

    @Test
    void testReadIpscRequests_withValidJson_thenReturnsIpscRequestHolder() throws JsonProcessingException {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data><row StageId='200' StageName='Test Stage' MatchId='100'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='50' Firstname='John' Lastname='Doe' Register='True' DOB='1973-02-17T00:00:00'/></data></xml>",
                    "classification": "<xml><data><row MemberId='50' DivisionId='1' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='50' CompId='500' MatchId='100'/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='100'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='100'/></data></xml>",
                    "score": "<xml><data><row MemberId='50' StageId='200' MatchId='100' HitFactor='6.08433734939759' FinalScore='101'/></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getClubs().size());
        assertEquals("ABC", result.getClubs().getFirst().getClubCode());
        assertEquals(1, result.getMatches().size());
        assertEquals(1, result.getStages().size());
        assertEquals(1, result.getTags().size());
        assertEquals(1, result.getMembers().size());
        assertEquals(1, result.getClassifications().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getTeams().size());
        assertEquals(1, result.getScores().size());

        // Assert the club section
        ClubRequest clubRequest = result.getClubs().getFirst();
        assertEquals(1, clubRequest.getClubId());
        assertEquals("ABC", clubRequest.getClubCode());
        assertEquals("Test Club", clubRequest.getClubName());
        // Assert the match section
        MatchRequest matchRequest = result.getMatches().getFirst();
        assertEquals(100, matchRequest.getMatchId());
        assertEquals("Test Match", matchRequest.getMatchName());
        // Assert the stage section
        StageRequest stageRequest = result.getStages().getFirst();
        assertEquals(200, stageRequest.getStageId());
        assertEquals("Test Stage", stageRequest.getStageName());
        assertEquals(100, stageRequest.getMatchId());
        // Assert the tag section
        TagRequest tagRequest = result.getTags().getFirst();
        assertEquals(10, tagRequest.getTagId());
        assertEquals("Test Tag", tagRequest.getTagName());
        // Assert the member section
        MemberRequest memberRequest = result.getMembers().getFirst();
        assertEquals(50, memberRequest.getMemberId());
        assertEquals("John", memberRequest.getFirstName());
        assertEquals("Doe", memberRequest.getLastName());
        assertEquals(true, memberRequest.getIsRegisteredForMatch());
        assertEquals(LocalDateTime.of(1973, 2, 17, 0, 0, 0), memberRequest.getDateOfBirth());
        // Assert the enrolled section
        EnrolledRequest enrolledRequest = result.getEnrolledMembers().getFirst();
        assertEquals(50, enrolledRequest.getMemberId());
        assertEquals(500, enrolledRequest.getCompetitorId());
        assertEquals(100, enrolledRequest.getMatchId());
        // Assert the classification section
        ClassificationRequest classificationRequest = result.getClassifications().getFirst();
        assertEquals(50, classificationRequest.getMemberId());
        assertEquals(1, classificationRequest.getDivisionId());
        assertEquals(5000, classificationRequest.getInternationalClassificationId());
        assertEquals(500, classificationRequest.getNationalClassificationId());
        // Assert the squad section
        SquadRequest squadRequest = result.getSquads().getFirst();
        assertEquals(20, squadRequest.getSquadId());
        assertEquals("Squad A", squadRequest.getSquadName());
        assertEquals(100, squadRequest.getMatchId());
        // Assert the team section
        TeamRequest teamRequest = result.getTeams().getFirst();
        assertEquals(20, teamRequest.getTeamId());
        assertEquals("Team A", teamRequest.getTeamName());
        assertEquals(100, teamRequest.getMatchId());
        // Assert the score section
        ScoreRequest scoreRequest = result.getScores().getFirst();
        assertEquals(100, scoreRequest.getMatchId());
        assertEquals(200, scoreRequest.getStageId());
        assertEquals(50, scoreRequest.getMemberId());
        assertEquals(101, scoreRequest.getFinalScore());
        assertEquals(BigDecimal.valueOf(6.08433734939759), scoreRequest.getHitFactor());
    }

    @Test
    void testReadIpscRequests_withEmptyXmlSectionData_thenReturnsEmptyLists() throws JsonProcessingException {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "match": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertTrue(result.getClubs().isEmpty());
        assertTrue(result.getMatches().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getMembers().isEmpty());
        assertTrue(result.getClassifications().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getSquads().isEmpty());
        assertTrue(result.getTeams().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    void testReadIpscRequests_withEmptyAndNotEmptyXml_thenReturnsIpscRequestHolder() throws JsonProcessingException {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data><row CompId='5' MemberId='50' MatchId='2' SquadId='4'/></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data><row MatchId='2' StageId='5' MemberId='50'/></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getClubs().size());
        assertEquals(1, result.getMatches().size());
        assertEquals(0, result.getStages().size());
        assertEquals(1, result.getTags().size());
        assertEquals(0, result.getMembers().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getScores().size());

        assertEquals(0, result.getClassifications().size());
        assertEquals(0, result.getTeams().size());
        assertEquals(0, result.getSquads().size());

        // Assert the match section
        MatchRequest matchRequest = result.getMatches().getFirst();
        assertEquals(100, matchRequest.getMatchId());
        assertEquals("Test Match", matchRequest.getMatchName());
        // Assert the tag section
        TagRequest tagRequest = result.getTags().getFirst();
        assertEquals(10, tagRequest.getTagId());
        assertEquals("Test Tag", tagRequest.getTagName());
        // Assert the enrolled section
        EnrolledRequest enrolledRequest = result.getEnrolledMembers().getFirst();
        assertEquals(5, enrolledRequest.getCompetitorId());
        assertEquals(50, enrolledRequest.getMemberId());
        assertEquals(2, enrolledRequest.getMatchId());
        assertEquals(4, enrolledRequest.getSquadId());
        // Assert the score section
        ScoreRequest scoreRequest = result.getScores().getFirst();
        assertEquals(2, scoreRequest.getMatchId());
        assertEquals(5, scoreRequest.getStageId());
        assertEquals(50, scoreRequest.getMemberId());
    }

    @Test
    void testReadIpscRequests_withEmptyXmlSections_thenReturnsEmptyLists() throws JsonProcessingException {
        // Arrange
        String cabFileContent = """
                {
                    "club": "",
                    "stage": "",
                    "match": "",
                    "tag": "",
                    "member": "",
                    "classification": "",
                    "enrolled": "",
                    "squad": "",
                    "team": "",
                    "score": ""
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertTrue(result.getClubs().isEmpty());
        assertTrue(result.getMatches().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getMembers().isEmpty());
        assertTrue(result.getClassifications().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getTeams().isEmpty());
        assertTrue(result.getSquads().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    void testReadIpscRequests_withInvalidJson_thenThrowsException() {
        // Arrange
        String cabFileContent = "Invalid JSON Content";

        // Act & Assert
        assertThrows(FatalException.class, () ->
                ipscService.readIpscRequests(cabFileContent)
        );
    }

    @Test
    void testReadIpscRequests_withNullContent_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.readIpscRequests(null)
        );
    }

    @Test
    void testReadRequests_withValidXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"
                                Address1="123 Main St" Address2="Suite 100" City="Cape Town"
                                Province="Western Cape" CountryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                Email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <row ClubId="2" ClubCode="DEF" Club="Beta Club" Contact="Jane Smith"
                                Address1="456 Oak Ave" Address2="" City="Johannesburg"
                                Province="Gauteng" CountryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                Email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(2, clubs.size());

        // Assert first club
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertEquals("John Doe", firstClub.getContact());
        assertEquals("123 Main St", firstClub.getAddress1());
        assertEquals("Suite 100", firstClub.getAddress2());
        assertEquals("Cape Town", firstClub.getCity());
        assertEquals("Western Cape", firstClub.getProvince());
        assertEquals("ZA", firstClub.getCountryId());
        assertEquals("8001", firstClub.getPostalCode());
        assertEquals("0211234567", firstClub.getOfficePhoneNumber());
        assertEquals("0827654321", firstClub.getAlternativePhoneNumber());
        assertEquals("0211234568", firstClub.getFaxNumber());
        assertEquals("info@alphaclub.co.za", firstClub.getEmail());
        assertEquals("https://alphaclub.co.za", firstClub.getWebsite());

        // Assert second club
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("DEF", secondClub.getClubCode());
        assertEquals("Beta Club", secondClub.getClubName());
    }

    @Test
    void testReadRequests_withValidNamespaceClubXml_thenReturnsClubRequest() {
        // Arrange
        String xmlData = """
                    <xml xmlns:s='uuid:BDC6E3F0-6DA3-11d1-A2A3-00AA00C14882'
                      xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882'
                      xmlns:rs='urn:schemas-microsoft-com:rowset'
                      xmlns:z='#RowsetSchema'>
                        <rs:data>
                            <z:row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"
                                Address1="123 Main St" Address2="Suite 100" City="Cape Town"
                                Province="Western Cape" CountryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                Email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <z:row ClubId="2" ClubCode="DEF" Club="Beta Club" Contact="Jane Smith"
                                Address1="456 Oak Ave" Address2="" City="Johannesburg"
                                Province="Gauteng" CountryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                Email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </rs:data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(2, clubs.size());

        // Assert first club
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertEquals("John Doe", firstClub.getContact());
        assertEquals("123 Main St", firstClub.getAddress1());
        assertEquals("Suite 100", firstClub.getAddress2());
        assertEquals("Cape Town", firstClub.getCity());
        assertEquals("Western Cape", firstClub.getProvince());
        assertEquals("ZA", firstClub.getCountryId());
        assertEquals("8001", firstClub.getPostalCode());
        assertEquals("0211234567", firstClub.getOfficePhoneNumber());
        assertEquals("0827654321", firstClub.getAlternativePhoneNumber());
        assertEquals("0211234568", firstClub.getFaxNumber());
        assertEquals("info@alphaclub.co.za", firstClub.getEmail());
        assertEquals("https://alphaclub.co.za", firstClub.getWebsite());

        // Assert second club
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("DEF", secondClub.getClubCode());
        assertEquals("Beta Club", secondClub.getClubName());
    }

    @Test
    void testReadRequests_withEmptyXmlData_thenReturnsEmptyList() throws JsonProcessingException {
        // Arrange
        String xmlData = """
                <xml>
                    <data/>
                </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(0, clubs.size());
    }

    @Test
    void testReadRequests_withInvalidXml_thenThrowsException() {
        // Arrange
        String xmlData = "Invalid XML Content";

        // Act & Assert
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertTrue(clubs.isEmpty());
    }

    @Test
    void testReadRequests_withEmptyXml_thenReturnsEmptyList() throws JsonProcessingException {
        // Arrange
        String xmlData = "";

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(0, clubs.size());
    }

    @Test
    void testReadRequests_withNullXml_thenReturnsEmptyList() {
        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(null,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertTrue(clubs.isEmpty());
    }

    @Test
    void testReadRequests_withValidClubXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        ClubRequest clubRequest = requestList.getFirst();
        assertEquals(1, clubRequest.getClubId());
        assertEquals("ABC", clubRequest.getClubCode());
        assertEquals("Alpha Club", clubRequest.getClubName());
        assertEquals("John Doe", clubRequest.getContact());
    }

    @Test
    void testReadRequests_withValidMatchXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='1' MatchName='Eeufees HG and PCC Club Shoot September 2025' MatchDt='2025-09-06T00:00:00' Chrono='True' MatchLevel='1' CountryId='CAN' FirearmId='7' SquadCount='9'/>
                        </data>
                    </xml>
                """;

        // Act
        List<MatchRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                MatchRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        MatchRequest firstRequest = requestList.getFirst();
        assertEquals(1, firstRequest.getMatchId());
        assertEquals("Eeufees HG and PCC Club Shoot September 2025", firstRequest.getMatchName());
        assertEquals(true, firstRequest.getChrono());
        assertEquals(LocalDateTime.of(2025, 9, 6, 0, 0, 0), firstRequest.getMatchDate());
    }

    @Test
    void testReadRequests_withValidStageXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='1' StageId='1' StageName='Stage 1' FirearmId='7' CourseId='2' ScoringId='1' TrgtTypeId='2' IcsStageId='0' Remove='False' TrgtPaper='9' TrgtPopper='3' TrgtPlates='0' TrgtVanish='0' TrgtPenlty='32' MinRounds='21' ReportOn='False' MaxPoints='105'/>
                        </data>
                    </xml>
                """;

        // Act
        List<StageRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                StageRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        StageRequest firstRequest = requestList.getFirst();
        assertEquals(1, firstRequest.getMatchId());
        assertEquals(1, firstRequest.getStageId());
        assertEquals("Stage 1", firstRequest.getStageName());
    }

    @Test
    void testReadRequests_withValidTagXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row TagId='1' Tag='RO'/>
                        </data>
                    </xml>
                """;

        // Act
        List<TagRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                TagRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        TagRequest firstRequest = requestList.getFirst();
        assertEquals(1, firstRequest.getTagId());
        assertEquals("RO", firstRequest.getTagName());
    }

    @Test
    void testReadRequests_withValidMemberXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MemberId='105' Lastname='Anthony' Firstname='Charlton' InActive='False' Female='false' PrintLabel='False' RegionId='CAN' Province='' ClassId='U' DfltDivId='0' DfltCatId='0' DfltTagId='0' QualId='0' IcsAlias='15000' Register='False'/>
                        </data>
                    </xml>
                """;

        // Act
        List<MemberRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                MemberRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        MemberRequest firstRequest = requestList.getFirst();
        assertEquals(105, firstRequest.getMemberId());
        assertEquals("Anthony", firstRequest.getLastName());
        assertEquals("Charlton", firstRequest.getFirstName());
        assertEquals("15000", firstRequest.getIcsAlias());
    }

    @Test
    void testReadRequests_withValidClassificationXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MemberId='50' DivisionId='1' IntlId='5000' NatlId='500'/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClassificationRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClassificationRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        ClassificationRequest classificationRequest = requestList.getFirst();
        assertEquals(50, classificationRequest.getMemberId());
        assertEquals(1, classificationRequest.getDivisionId());
        assertEquals(5000, classificationRequest.getInternationalClassificationId());
        assertEquals(500, classificationRequest.getNationalClassificationId());
    }

    @Test
    void testReadRequests_withValidEnrolledXml_thenReturnsRequest() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='1' MemberId='105' CompId='105' DivId='29' CatId='2'  SquadId='7' TagId='0' MajorPF='True' IsDisq='False' DisqRuleId='0'   StageDisq='False' RefNo='AAA'/>
                        </data>
                    </xml>
                """;

        // Act
        List<EnrolledRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                EnrolledRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        EnrolledRequest firstRequest = requestList.getFirst();
        assertEquals(105, firstRequest.getMemberId());
        assertEquals(1, firstRequest.getMatchId());
        assertEquals(105, firstRequest.getCompetitorId());
    }

    @Test
    void testReadRequests_withValidSquadXml_thenReturnsRequest() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='1' SquadId='4' Squad='4'/>
                        </data>
                    </xml>
                """;

        // Act
        List<SquadRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                SquadRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        SquadRequest firstRequest = requestList.getFirst();
        assertEquals(1, firstRequest.getMatchId());
        assertEquals(4, firstRequest.getSquadId());
        assertEquals("4", firstRequest.getSquadName());
    }

    @Test
    void testReadRequests_withValidTeamXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='2' TeamId='10' Team='Test Team'/>
                        </data>
                    </xml>
                """;

        // Act
        List<TeamRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                TeamRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        TeamRequest firstRequest = requestList.getFirst();
        assertEquals(2, firstRequest.getMatchId());
        assertEquals(10, firstRequest.getTeamId());
        assertEquals("Test Team", firstRequest.getTeamName());
    }

    @Test
    void testReadRequests_withValidScoreXml_thenReturnsRequest() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row MatchId='1' StageId='8' MemberId='105' ScoreA='0' ScoreB='0' ScoreC='0' ScoreD='0' Misses='0' Penalties='0' ProcError='0' ShootTime='0' Remove='True' Deduction='False' DedPctg='0' FlagDelete='False' IsDisq='False' ExtraShot='0' OverTime='0' HitFactor='0' FinalScore='0' LastModify='2025-09-10T15:54:03' NoVerify='True'/>
                        </data>
                    </xml>
                """;

        // Act
        List<ScoreRequest> requestList = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ScoreRequest.class));

        // Assert
        assertNotNull(requestList);
        assertEquals(1, requestList.size());

        // Assert the first row
        ScoreRequest firstRequest = requestList.getFirst();
        assertEquals(1, firstRequest.getMatchId());
        assertEquals(105, firstRequest.getMemberId());
        assertEquals(8, firstRequest.getStageId());
    }

    @Test
    void testMapIpscRequests_withValidData_thenReturnsIpscResponses() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        clubRequest.setClubName("Test Club");
        holder.setClubs(List.of(clubRequest));

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        StageRequest stageRequest = new StageRequest();
        stageRequest.setStageId(200);
        stageRequest.setStageName("Test Stage");
        stageRequest.setMatchId(100);
        holder.setStages(List.of(stageRequest));

        TagRequest tagRequest = new TagRequest();
        tagRequest.setTagId(10);
        tagRequest.setTagName("RO");
        holder.setTags(List.of(tagRequest));

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberId(50);
        memberRequest.setFirstName("John");
        memberRequest.setLastName("Doe");
        holder.setMembers(List.of(memberRequest));

        EnrolledRequest enrolledRequest = new EnrolledRequest();
        enrolledRequest.setMemberId(50);
        enrolledRequest.setMatchId(100);
        enrolledRequest.setCompetitorId(500);
        holder.setEnrolledMembers(List.of(enrolledRequest));

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchId(100);
        scoreRequest.setStageId(200);
        scoreRequest.setMemberId(50);
        scoreRequest.setFinalScore(95);
        holder.setScores(List.of(scoreRequest));

        holder.setClassifications(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());

        // Act
        List<IpscResponse> responses = ipscService.mapIpscRequests(holder);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getMatch());
        assertEquals(100, response.getMatch().getMatchId());
        assertEquals("Test Match", response.getMatch().getMatchName());

        assertNotNull(response.getClub());
        assertEquals(1, response.getClub().getClubId());
        assertEquals("ABC", response.getClub().getClubCode());

        assertNotNull(response.getStages());
        assertEquals(1, response.getStages().size());
        assertEquals(200, response.getStages().getFirst().getStageId());

        assertNotNull(response.getTags());
        assertEquals(1, response.getTags().size());
        assertEquals(10, response.getTags().getFirst().getTagId());

        assertNotNull(response.getEnrolledMembers());
        assertEquals(1, response.getEnrolledMembers().size());
        assertEquals(50, response.getEnrolledMembers().getFirst().getMemberId());

        assertNotNull(response.getScores());
        assertEquals(1, response.getScores().size());
        assertEquals(95, response.getScores().getFirst().getFinalScore());

        assertNotNull(response.getMembers());
        assertEquals(1, response.getMembers().size());
        assertEquals("John", response.getMembers().getFirst().getFirstName());
    }

    @Test
    void testMapIpscRequests_withMultipleMatches_thenReturnsMultipleResponses() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        clubRequest.setClubCode("ABC");
        holder.setClubs(List.of(clubRequest));

        MatchRequest match1 = new MatchRequest();
        match1.setMatchId(100);
        match1.setMatchName("Match 1");
        match1.setClubId(1);

        MatchRequest match2 = new MatchRequest();
        match2.setMatchId(200);
        match2.setMatchName("Match 2");
        match2.setClubId(1);
        holder.setMatches(List.of(match1, match2));

        StageRequest stage1 = new StageRequest();
        stage1.setStageId(10);
        stage1.setMatchId(100);

        StageRequest stage2 = new StageRequest();
        stage2.setStageId(20);
        stage2.setMatchId(200);
        holder.setStages(List.of(stage1, stage2));

        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        List<IpscResponse> responses = ipscService.mapIpscRequests(holder);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(100, responses.getFirst().getMatch().getMatchId());
        assertEquals(1, responses.get(0).getStages().size());
        assertEquals(10, responses.get(0).getStages().getFirst().getStageId());

        assertEquals(200, responses.get(1).getMatch().getMatchId());
        assertEquals(1, responses.get(1).getStages().size());
        assertEquals(20, responses.get(1).getStages().getFirst().getStageId());
    }

    @Test
    void testMapIpscRequests_withNoMatchingClub_thenCreatesClubWithIdOnly() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setMatchName("Test Match");
        matchRequest.setClubId(999);
        holder.setMatches(List.of(matchRequest));

        holder.setClubs(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        List<IpscResponse> responses = ipscService.mapIpscRequests(holder);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getClub());
        assertEquals(999, response.getClub().getClubId());
    }

    @Test
    void testMapIpscRequests_withEmptyRequestHolder_thenReturnsEmptyList() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();
        holder.setClubs(new ArrayList<>());
        holder.setMatches(new ArrayList<>());
        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        List<IpscResponse> responses = ipscService.mapIpscRequests(holder);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testMapIpscRequests_withMatchButNoMembers_thenReturnsResponseWithEmptyMembers() {
        // Arrange
        IpscRequestHolder holder = new IpscRequestHolder();

        ClubRequest clubRequest = new ClubRequest();
        clubRequest.setClubId(1);
        holder.setClubs(List.of(clubRequest));

        MatchRequest matchRequest = new MatchRequest();
        matchRequest.setMatchId(100);
        matchRequest.setClubId(1);
        holder.setMatches(List.of(matchRequest));

        holder.setStages(new ArrayList<>());
        holder.setTags(new ArrayList<>());
        holder.setMembers(new ArrayList<>());
        holder.setClassifications(new ArrayList<>());
        holder.setEnrolledMembers(new ArrayList<>());
        holder.setSquads(new ArrayList<>());
        holder.setTeams(new ArrayList<>());
        holder.setScores(new ArrayList<>());

        // Act
        List<IpscResponse> responses = ipscService.mapIpscRequests(holder);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());

        IpscResponse response = responses.getFirst();
        assertNotNull(response.getMembers());
        assertTrue(response.getMembers().isEmpty());
    }
}
