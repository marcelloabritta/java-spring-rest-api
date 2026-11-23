package com.marcelloabritta.restapi.controllers;

import com.marcelloabritta.restapi.data.dto.v1.security.AccountCredentialsDTO;
import com.marcelloabritta.restapi.data.dto.v1.security.TokenDTO;
import com.marcelloabritta.restapi.exception.BadRequestException;
import com.marcelloabritta.restapi.services.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenDTO> signin(@RequestBody AccountCredentialsDTO credentials) {
        if (credentialsIsInvalid(credentials))
            throw new BadRequestException("Invalid client request!");

        var token = service.signIn(credentials);

        return ResponseEntity.ok(token);
    }

    @PutMapping("/refresh/{username}")
    public ResponseEntity<TokenDTO> refreshToken(@PathVariable("username") String username,
                                                 @RequestHeader("Authorization") String refreshToken) {
        if (parametersAreInvalid(username, refreshToken))
            throw new BadRequestException("Invalid client request!");

        var token = service.refreshToken(username, refreshToken);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/createUser")
    public ResponseEntity<AccountCredentialsDTO> create(@RequestBody AccountCredentialsDTO credentials) {
        var dto = service.create(credentials);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    private static boolean parametersAreInvalid(String username, String refreshToken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
    }

    private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null || StringUtils.isBlank(credentials.getPassword())
                || StringUtils.isBlank(credentials.getUsername());
    }
}
