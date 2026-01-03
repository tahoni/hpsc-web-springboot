package za.co.hpsc.web.services;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.model.ImageResponseHolder;

import java.io.IOException;

@Service
public interface ImageService {
    ImageResponseHolder processCsv(String csvData) throws IOException;
}
