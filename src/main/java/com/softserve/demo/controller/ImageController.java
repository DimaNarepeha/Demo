package com.softserve.demo.controller;

import com.softserve.demo.service.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Illia Chenchak
 */
@RestController
@RequestMapping("")
@CrossOrigin("*")
public class ImageController {

    private final FilesStorageService fileStorageService;

    public ImageController(FilesStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("service-providers/image/{imageName}")
    public ResponseEntity<?> getImageForProviders(@PathVariable("imageName") String name,
                                                  HttpServletRequest servletRequest) {
        Resource resource = fileStorageService.loadFile(name);
        String contentType = null;
        try {
            contentType = servletRequest
                    .getServletContext()
                    .getMimeType(
                            resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
