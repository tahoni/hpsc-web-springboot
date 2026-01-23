package za.co.hpsc.web.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.hpsc.web.models.matches.MatchResultLogResponseHolder;

public interface IpscService {
    MatchResultLogResponseHolder processWinMssCabFile(MultipartFile cabFile);
}
