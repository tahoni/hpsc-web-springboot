package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IpscServiceImpl implements IpscService {
    @Override
    public IpscResponseHolder importWinMssCabFile(String cabFileContent) throws JsonProcessingException {
        if ((cabFileContent == null) || (cabFileContent.isBlank())) {
            throw new ValidationException("The provided CAB file is null or empty.");
        }

        IpscRequestHolder ipscRequestHolder = readIpscRequests(cabFileContent);
        List<IpscResponse> ipscResponses = mapIpscRequests(ipscRequestHolder);
        saveIpscResponse(ipscResponses);

        return new IpscResponseHolder(ipscResponses);
    }

    /**
     * Deserializes the provided CAB file content into an {@link IpscRequest} object and populates
     * an {@link IpscRequestHolder} with various request data, such as clubs, matches, tags, members,
     * enrolled members, squads, and scores.
     *
     * @param cabFileContent The CAB file content as a string to be deserialized.
     * @return An {@link IpscRequest} object containing the deserialized request data.
     * @throws JsonProcessingException If an error occurs during the deserialization process.
     */
    protected IpscRequestHolder readIpscRequests(String cabFileContent) throws JsonProcessingException {
        // Deserializes CAB file content into typed IPSC requests
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        IpscRequest ipscRequest = objectMapper.readValue(cabFileContent, IpscRequest.class);

        IpscRequestHolder ipscRequestHolder = new IpscRequestHolder();
        ipscRequestHolder.setClubs(readRequests(ipscRequest.getClub(), ClubRequest.class));
        ipscRequestHolder.setMatches(readRequests(ipscRequest.getMatch(), MatchRequest.class));
        ipscRequestHolder.setTags(readRequests(ipscRequest.getTag(), TagRequest.class));
        ipscRequestHolder.setMembers(readRequests(ipscRequest.getMember(), MemberRequest.class));
        ipscRequestHolder.setEnrolledMembers(readRequests(ipscRequest.getEnrolled(), EnrolledRequest.class));
        ipscRequestHolder.setSquads(readRequests(ipscRequest.getSquad(), SquadRequest.class));
        ipscRequestHolder.setScores(readRequests(ipscRequest.getScore(), ScoreRequest.class));

        return ipscRequestHolder;
    }

    protected List<IpscResponse> mapIpscRequests(IpscRequestHolder ipscRequestHolder) {
        return null;
    }

    protected void saveIpscResponse(List<IpscResponse> ipscResponse) {
    }

    /**
     * Generic method to parse XML content into a list of request objects.
     *
     * @param xmlContent The XML content to parse
     * @param clazz      The class type of the request objects
     * @param <T>        The type of request objects
     * @return List of parsed request objects
     * @throws JsonProcessingException If parsing fails
     */
    protected <T> List<T> readRequests(String xmlContent, Class<T> clazz) throws JsonProcessingException {
        if (xmlContent == null || xmlContent.isBlank()) {
            throw new ValidationException("The provided IPSC request file is null or empty.");
        }

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        var wrapperType = xmlMapper.getTypeFactory()
                .constructParametricType(XmlDataWrapper.class, clazz);
        XmlDataWrapper<T> wrapper = xmlMapper.readValue(xmlContent, wrapperType);

        // Handles null wrapper or data to avoid errors
        if (wrapper == null || wrapper.getData() == null || wrapper.getData().getRow() == null) {
            return new ArrayList<>();
        }
        return wrapper.getData().getRow();
    }
}
