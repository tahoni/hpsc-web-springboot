package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.request.ImageRequest;
import za.co.hpsc.web.models.image.request.ImageRequestForCsv;
import za.co.hpsc.web.models.image.response.ImageResponse;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

import java.io.IOException;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public ImageResponseHolder processCsv(String csvData)
            throws ValidationException, FatalException {
        if (csvData == null || csvData.isBlank()) {
            throw new ValidationException("CSV data cannot be null or blank.");
        }

        List<ImageRequest> imageRequestList = readImages(csvData);
        List<ImageResponse> imageResponseList = mapImages(imageRequestList);
        return new ImageResponseHolder(imageResponseList);
    }

    /**
     * Reads image data from a CSV-formatted string and converts it into a list of
     * {@link ImageRequest} objects.
     *
     * <p>
     * The method uses a {@link CsvMapper} and a custom {@link CsvSchema} configuration
     * to read, map, and convert the input CSV data into instances of {@link ImageRequest}.
     * It ensures that CSV headers are correctly processed and supports reordering of columns.
     * </p>
     *
     * @param csvData the CSV data containing information about image requests.
     *                Each row in the CSV should represent an image request with fields
     *                such as title, ceremony title, first place recipient, second place recipient, and
     *                third place recipient, and optional meta-data.
     *                Must not be null or blank.
     * @return a list of {@link ImageRequest} objects parsed from the provided CSV data.
     * @throws ValidationException if the CSV data format is invalid or contains mismatched input.
     * @throws FatalException      if an I/O error occurs while processing the CSV data.
     */
    protected List<ImageRequest> readImages(@NotNull @NotBlank String csvData)
            throws ValidationException, FatalException {
        // Prepare the CSV mapper and schema
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(ImageRequestForCsv.class)
                .withArrayElementSeparator("|")
                .withColumnReordering(true)
                .withHeader();
        csvMapper.addMixIn(ImageRequest.class, ImageRequestForCsv.class);

        // Read the CSV data using the mapper and schema
        try (MappingIterator<ImageRequest> requestMappingIterator =
                     csvMapper.readerFor(ImageRequest.class)
                             .with(csvSchema)
                             .readValues(csvData)) {
            return requestMappingIterator.readAll();

        } catch (MismatchedInputException | IllegalArgumentException | CsvReadException e) {
            throw new ValidationException("Invalid CSV data format.", e);
        } catch (IOException e) {
            throw new FatalException("Error reading CSV data.", e);
        }
    }

    /**
     * Maps a list of {@link ImageRequest} objects to a list of {@link ImageResponse} objects.     * Each {@link ImageRequest} object in the input list is transformed into a corresponding
     *
     * <p>
     * If the input list is null, an empty list is returned.
     * </p>
     *
     * @param imageRequestList the list of {@link ImageRequest} objects to be mapped.
     * @return a list of {@link ImageResponse} objects resulting from mapping the input list.
     * It will never be null, but it may be empty.
     * @throws ValidationException if the input list is null.
     */
    protected List<ImageResponse> mapImages(List<ImageRequest> imageRequestList)
            throws ValidationException {
        if (imageRequestList == null) {
            throw new ValidationException("Image request list cannot be null.");
        }

        // Map each request to a response
        return imageRequestList.stream()
                .map(ImageResponse::new)
                .toList();
    }
}
