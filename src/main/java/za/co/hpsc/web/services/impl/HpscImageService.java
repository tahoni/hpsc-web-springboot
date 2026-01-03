package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.model.ImageRequest;
import za.co.hpsc.web.model.ImageRequestForCsv;
import za.co.hpsc.web.model.ImageResponse;
import za.co.hpsc.web.model.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HpscImageService implements ImageService {
    @Override
    public ImageResponseHolder processCsv(String csvData) throws IOException {
        List<ImageRequest> imageRequestList = readImages(csvData);
        List<ImageResponse> imageResponseList = mapImages(imageRequestList);
        return new ImageResponseHolder(imageResponseList);
    }

    protected List<ImageRequest> readImages(String csvData) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(ImageRequestForCsv.class).withHeader();
        csvMapper.addMixIn(ImageRequest.class, ImageRequestForCsv.class);

        try (MappingIterator<ImageRequest> requestMappingIterator =
                     csvMapper.readerFor(ImageRequest.class)
                             .with(csvSchema)
                             .readValues(csvData)) {
            return requestMappingIterator.readAll();
        }
    }

    protected List<ImageResponse> mapImages(List<ImageRequest> imageRequestList) {
        if (imageRequestList == null) {
            return new ArrayList<>();
        }

        return imageRequestList.stream()
                .map(ImageResponse::new)
                .toList();
    }
}
