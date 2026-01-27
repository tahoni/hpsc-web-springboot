package za.co.hpsc.web.services.impl;

import de.morihofi.cab4j.extract.CabExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;
import za.co.hpsc.web.services.IpscService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
public class IpscServiceImpl implements IpscService {
    @Override
    public MatchResultLogResponseHolder processWinMssCabFile(MultipartFile cabFile) throws IOException {
        if ((cabFile == null) || (cabFile.isEmpty())) {
            throw new ValidationException("The provided CAB file is null or empty.");
        }

        Map<String, ByteBuffer> content = CabExtractor.extract(ByteBuffer.wrap(cabFile.getBytes()));

        return new MatchResultLogResponseHolder(new ArrayList<>());
    }
}
