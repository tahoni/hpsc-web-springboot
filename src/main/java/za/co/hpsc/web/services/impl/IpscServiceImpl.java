package za.co.hpsc.web.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;
import za.co.hpsc.web.services.IpscService;

import java.util.ArrayList;

@Service
public class IpscServiceImpl implements IpscService {
    @Override
    public MatchResultLogResponseHolder processWinMssCabFile(MultipartFile cabFile) {
        return new MatchResultLogResponseHolder(new ArrayList<>());
    }
}
