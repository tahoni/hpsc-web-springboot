package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;
import za.co.hpsc.web.services.IpscService;

import java.util.ArrayList;

@Slf4j
@Service
public class IpscServiceImpl implements IpscService {
    @Override
    public MatchResultLogResponseHolder processWinMssCabFile(String cabFileContent) {
        if ((cabFile == null) || (cabFile.isEmpty())) {
            throw new ValidationException("The provided CAB file is null or empty.");
        }

        return new MatchResultLogResponseHolder(new ArrayList<>());
    }
}
