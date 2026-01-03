package za.co.hpsc.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.hpsc.web.model.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

import java.io.IOException;

@Controller
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/processCsv")
    ImageResponseHolder processCsv(@RequestBody String csvData) throws IOException {
        return imageService.processCsv(csvData);
    }
}
