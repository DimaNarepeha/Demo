package com.softserve.demo.controller;

import com.softserve.demo.dto.CustomerDTO;
import com.softserve.demo.service.CustomerService;
import com.softserve.demo.service.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService customerService;
    private final FilesStorageService fileStorageService;


    public CustomerController(final CustomerService customerService, final FilesStorageService fileStorageService) {
        this.customerService = customerService;
        this.fileStorageService = fileStorageService;
    }


    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public CustomerDTO getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);

    }

    @GetMapping("list")
    public Page<CustomerDTO> getCustomersByPage(@PageableDefault Pageable pageable) {
        return customerService.getCustomersByPage(pageable);
    }

    @DeleteMapping("{id}")
    public void deleteCustomerById(@PathVariable("id") Integer id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomerDTO updateCustomer(
            @RequestBody CustomerDTO customer
    ) {
        return customerService.updateCustomer(customer);
    }

    @PostMapping("{id}/image")
    public void uploadImage(
            @PathVariable("id") Integer id,
            @RequestParam("imageFile") MultipartFile file
    ) {

        fileStorageService.storeFile(file);
        customerService.addImageToCustomer(id, file.getOriginalFilename());
    }


    @GetMapping("image/{imageName}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getImage(
            @PathVariable("imageName") String name,
            final HttpServletRequest servletRequest
    ) {

        Resource resource = fileStorageService.loadFile(name);
        String contentType = fileStorageService.getContentType(servletRequest, resource, name);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("find-by-userId/{id}")
    public CustomerDTO findCustomerByUserId(@PathVariable("id") Integer id) {
        return customerService.findCustomerByUserId(id);
    }

}
