package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.services.UserService;
import com.example.networktechnologiesproject1.services.LoginForm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.getUsername(),
                            loginForm.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername()).orElse(null);

            if (user != null) {
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
            } else {
                return ResponseEntity.status(401).body("User not found.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }
}
