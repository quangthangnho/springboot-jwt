package com.learnudemy.controller;

import com.learnudemy.config.JwtTokenProvider;
import com.learnudemy.constant.SecurityConstant;
import com.learnudemy.entity.Users;
import com.learnudemy.exception.EmailExistException;
import com.learnudemy.exception.ExceptionHandling;
import com.learnudemy.exception.UsernameExistException;
import com.learnudemy.model.UserPrincipal;
import com.learnudemy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/", "/user"})
public class UserController extends ExceptionHandling {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public UserController(UserService service, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody Users user) {
        authenticate(user.getUsername(), user.getPassword());
        Users loginUser = service.findByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstant.JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) throws EmailExistException, UsernameExistException {
        return new ResponseEntity<>(service.register(user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmail(), user.getPassword()), HttpStatus.OK);
    }
}
