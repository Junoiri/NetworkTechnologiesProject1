package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.exceptions.IncorrectPasswordException;
import com.example.networktechnologiesproject1.exceptions.UserAlreadyExistsException;
import com.example.networktechnologiesproject1.exceptions.UserNotFoundException;
import com.example.networktechnologiesproject1.services.UserService;
import com.example.networktechnologiesproject1.services.LoginForm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    @Value("${jwt.token}")
    private String jwtSecret;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginForm loginForm) {
        userService.findByUsername(loginForm.getUsername())
                .orElseThrow(() -> new UserNotFoundException(loginForm.getUsername()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.getUsername(),
                            loginForm.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            long now = System.currentTimeMillis();
            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .claim("authorities", userDetails.getAuthorities().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()))
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 5 * 60 * 1000)) // 5 minutes
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();

            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            throw new IncorrectPasswordException(loginForm.getUsername());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        userService.findByUsername(newUser.getUsername())
                .ifPresent(s -> {
                    throw new UserAlreadyExistsException(newUser.getUsername());
                });

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User user = userService.saveUser(newUser);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}

