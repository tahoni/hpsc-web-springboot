package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.ClubRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IpscServiceImplTest {

    @InjectMocks
    private IpscServiceImpl ipscService;

    @Test
    void testReaRequests_withValidClubXml_thenReturnsClubData() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row clubId="1" clubCode="ABC" Club="Alpha Club" contact="John Doe"
                                address1="123 Main St" address2="Suite 100" city="Cape Town"
                                province="Western Cape" countryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <row clubId="2" clubCode="DEF" Club="Beta Club" contact="Jane Smith"
                                address1="456 Oak Ave" address2="" city="Johannesburg"
                                province="Gauteng" countryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() ->
                ipscService.readRequests(xmlData, ClubRequest.class)
        );

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
    void testReaRequests_withValidNamespaceClubXml_thenReturnsClubData() {
        // Arrange
        String xmlData = """
                    <xml xmlns:s='uuid:BDC6E3F0-6DA3-11d1-A2A3-00AA00C14882'
                      xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882'
                      xmlns:rs='urn:schemas-microsoft-com:rowset'
                      xmlns:z='#RowsetSchema'>
                        <rs:data>
                            <z:row clubId="1" clubCode="ABC" Club="Alpha Club" contact="John Doe"
                                address1="123 Main St" address2="Suite 100" city="Cape Town"
                                province="Western Cape" countryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <z:row clubId="2" clubCode="DEF" Club="Beta Club" contact="Jane Smith"
                                address1="456 Oak Ave" address2="" city="Johannesburg"
                                province="Gauteng" countryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </rs:data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() ->
                ipscService.readRequests(xmlData, ClubRequest.class)
        );

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
    void testReadClubRequests_withEmptyXmlData_thenReturnsEmptyList() throws JsonProcessingException {
        // Arrange
        String xmlData = """
                <root>
                </root>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() ->
                ipscService.readRequests(xmlData, ClubRequest.class)
        );

        // Assert
        assertNotNull(clubs);
        assertTrue(clubs.isEmpty());
    }

    @Test
    void testReadClubRequests_withInvalidXmlData_thenThrowsException() {
        // Arrange
        String xmlData = "Invalid XML Content";

        // Act & Assert
        assertThrows(JsonProcessingException.class, () ->
                ipscService.readRequests(xmlData, ClubRequest.class)
        );
    }

    @Test
    void testReadClubRequests_withNullXmlData_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.readRequests(null, ClubRequest.class)
        );
    }
}
