package com.softserve.demo.controller;

import com.softserve.demo.dto.EmailDTO;
import com.softserve.demo.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String sendEmailTo(@RequestBody final EmailDTO emailDTO) {
        emailService.sendEmailTo(emailDTO.getTo(), emailDTO.getSubject(), emailDTO.getText());
        return "Successfully sent!";
    }
}
