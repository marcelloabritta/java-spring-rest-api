package com.marcelloabritta.restapi.services;

import com.marcelloabritta.restapi.data.dto.v1.security.AccountCredentialsDTO;
import com.marcelloabritta.restapi.data.dto.v1.security.TokenDTO;
import com.marcelloabritta.restapi.exception.RequiredObjectIsNullException;
import com.marcelloabritta.restapi.model.User;
import com.marcelloabritta.restapi.repository.UserRepository;
import com.marcelloabritta.restapi.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.marcelloabritta.restapi.mapper.ObjectMapper.parseObject;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository repository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenDTO signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()
                )
        );

        var user = repository.findByUsername(credentials.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");
        }

        return tokenProvider.createAccessToken(
                credentials.getUsername(), user.getRoles()
        );
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one new User!");

        var entity = new User();
        entity.setFullName(user.getFullName());
        entity.setUserName(user.getUsername());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        return parseObject(repository.save(entity), AccountCredentialsDTO.class);
    }

    public TokenDTO refreshToken(String username, String refreshToken) {
        var user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }

        return tokenProvider.refreashToken(refreshToken);
    }

}
