package com.marcelloabritta.restapi.controllers;

import com.marcelloabritta.restapi.data.dto.v1.request.EmailRequestDTO;
import com.marcelloabritta.restapi.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    @Autowired
    private EmailService service;

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        service.sendSimpleEmail(emailRequest);
        return ResponseEntity.ok("E-mail sent with success");
    }

    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("emailRequest") String emailRequest,
                                                          @RequestParam("attachment") MultipartFile attachment) {
        service.sendEmailWithAttachment(emailRequest, attachment);
        return ResponseEntity.ok("E-mail with attachment sent with success");
    }
}
