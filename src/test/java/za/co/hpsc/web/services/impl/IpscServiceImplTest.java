package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscServiceImplTest {

    @InjectMocks
    private IpscServiceImpl ipscService;

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
    void testReadIpscRequests_withValidJson_thenReturnsIpscRequestHolder() throws JsonProcessingException {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row clubId='100' clubCode='ABC'/></data></xml>",
                    "match": "<xml><data><row matchId='100' matchName='Test Match'/></data></xml>",
                    "stage": "<xml><data><row stageId='200' stageName='Test Stage'/></data></xml>",
                    "tag": "<xml><data><row tagId='10' tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row memberId='50' firstName='John' lastName='Doe'/></data></xml>",
                    "enrolled": "<xml><data><row enrolledId='5' memberId='50'/></data></xml>",
                    "squad": "<xml><data><row squadId='20' squadName='Squad A'/></data></xml>",
                    "score": "<xml><data><row scoreId='30' memberId='50'/></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // TODO: test values
        // Assert
        assertNotNull(result);
        assertNotNull(result.getClubs());
        assertNotNull(result.getMatches());
        assertNotNull(result.getStages());
        assertNotNull(result.getTags());
        assertNotNull(result.getMembers());
        assertNotNull(result.getEnrolledMembers());
        assertNotNull(result.getSquads());
        assertNotNull(result.getScores());
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
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getSquads().isEmpty());
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
                    "enrolled": "<xml><data><row CompId='5' MemberId='50'/>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data><row ScoreId='30' MemberId='50'/></data></xml>"
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
        assertEquals(0, result.getSquads().size());
        assertEquals(1, result.getScores().size());
    }

    @Test
    void testReadIpscRequests_withEmptyXmlSections_thenReturnsEmptyLists() throws JsonProcessingException {
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
        assertTrue(result.getEnrolledMembers().isEmpty());
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
}
