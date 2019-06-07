package com.softserve.demo.controller;

import com.softserve.demo.dto.LocationDTO;
import com.softserve.demo.dto.ProviderAndLocationDTO;
import com.softserve.demo.dto.ProviderDTO;
import com.softserve.demo.model.Provider;
import com.softserve.demo.model.ProviderStatus;
import com.softserve.demo.service.FilesStorageService;
import com.softserve.demo.service.ProvidersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by Illia Chenchak
 */
@RestController
@RequestMapping("service-providers")
@CrossOrigin ("*")
public class ProvidersController {

    private final ProvidersService providersService;

    private final FilesStorageService fileStorageService;


    public ProvidersController(ProvidersService providersService, FilesStorageService fileStorageService) {
        this.providersService = providersService;
        this.fileStorageService = fileStorageService;
    }

    private ProviderDTO getProviderDTO(ProviderAndLocationDTO providerAndLocationDTO) {
        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.setId(providerAndLocationDTO.getIdProvider());
        providerDTO.setName(providerAndLocationDTO.getName());
        providerDTO.setEmail(providerAndLocationDTO.getEmail());
        providerDTO.setDescription(providerAndLocationDTO.getDescription());
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCountry(providerAndLocationDTO.getCountry());
        locationDTO.setCity(providerAndLocationDTO.getCity());
        locationDTO.setRegion(providerAndLocationDTO.getRegion());
        providerDTO.setLocation(locationDTO);
        return providerDTO;
    }

    @PostMapping("save")
    public ResponseEntity<?> saveServiceProvider(@RequestBody ProviderAndLocationDTO providerAndLocationDTO) {
        ProviderDTO providerDTO = getProviderDTO(providerAndLocationDTO);
        return new ResponseEntity<>(providersService.save(providerDTO,providerDTO.getLocation()), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateServiceProviders(@PathVariable("id")Integer id, @RequestBody ProviderAndLocationDTO providerAndLocationDTO) {
        ProviderDTO providerDTO = getProviderDTO(providerAndLocationDTO);
        return new ResponseEntity<>(providersService.update(id,providerDTO,providerDTO.getLocation()), HttpStatus.OK);
    }

    @GetMapping("find-all")
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(providersService.findAll(), HttpStatus.OK);
    }

    @GetMapping("find-all/page")
    public Page<?> getServiceProvidersByPage(@RequestParam(defaultValue = "0") int page) {
        return providersService.getServiceProvidersByPage(page);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteServiceProvidersResponse(@PathVariable("id") Integer id) {
        providersService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("find-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(providersService.findById(id), HttpStatus.OK);
    }

    @PostMapping("{userId}")
    public ResponseEntity<?> uploadImage(
            @PathVariable("userId") Integer id,
            @RequestParam("imageFile") MultipartFile file
    ) {
        fileStorageService.storeFile(file);
        providersService.addImageToProviders(id, file.getOriginalFilename());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/image/{imageName}")
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
            e.printStackTrace();
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("find-all/status")
    public Page<?> getServiceProviderByStatus (@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "4")
            int numberOfProvidersOnPage, @RequestParam(defaultValue = "NOTAPPROVED") String status) {
        return providersService.getServiceProvidersByStatus(page,numberOfProvidersOnPage, ProviderStatus.valueOf(status));
    }

    @PutMapping("update-status/{id}")
    public ResponseEntity<?> updateServiceProvidersStatus(@PathVariable("id") Integer id, @RequestBody String status) {
        return new ResponseEntity<>(providersService.updateStatus(id,status), HttpStatus.OK);
    }

    @GetMapping("by/{name}")
    public ResponseEntity<?> findByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(providersService.findByName(name), HttpStatus.OK);
    }
}
