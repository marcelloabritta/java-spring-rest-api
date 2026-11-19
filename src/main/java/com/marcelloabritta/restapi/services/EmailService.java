package com.marcelloabritta.restapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcelloabritta.restapi.config.EmailConfig;
import com.marcelloabritta.restapi.data.dto.v1.request.EmailRequestDTO;
import com.marcelloabritta.restapi.mail.EmailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfigs;

    public EmailService(JavaMailSender mailSender, EmailConfig emailConfigs) {
        this.mailSender = mailSender;
        this.emailConfigs = emailConfigs;
    }

    public void sendSimpleEmail(EmailRequestDTO emailRequest) {
        new EmailSender(mailSender)
                .to(emailRequest.getTo())
                .withSubject(emailRequest.getSubject())
                .withMessage(emailRequest.getBody())
                .send(emailConfigs);
    }

    public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
            attachment.transferTo(tempFile);

            new EmailSender(mailSender)
                    .to(emailRequest.getTo())
                    .withSubject(emailRequest.getSubject())
                    .withMessage(emailRequest.getBody())
                    .attach(tempFile.getAbsolutePath())
                    .send(emailConfigs);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing email request JSON", e);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the attachment", e);
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }
}
