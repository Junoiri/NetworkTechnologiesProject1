/**
 * Controller class for handling user authentication and registration.
 */
package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.exceptions.IncorrectPasswordException;
import com.example.networktechnologiesproject1.exceptions.UserAlreadyExistsException;
import com.example.networktechnologiesproject1.exceptions.UserNotFoundException;
import com.example.networktechnologiesproject1.services.UserService;
import com.example.networktechnologiesproject1.services.LoginForm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration in the library system.")
public class LoginController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Handles user login and generates JWT token upon successful authentication.
     * @param loginForm The login form containing username and password.
     * @return ResponseEntity containing JWT token upon successful login.
     * @throws UserNotFoundException If the user is not found.
     * @throws IncorrectPasswordException If the password is incorrect.
     */
    @SecurityRequirements
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and generates a JWT token for session management.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not found or incorrect password"),
    })
    public ResponseEntity<String> login(@RequestBody @Parameter(description = "Login form with username and password") LoginForm loginForm) {
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
            String roles = userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .claim("role", roles)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 20 * 60 * 1000)) // 20 minutes
                    .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(StandardCharsets.UTF_8))
                    .compact();

            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            throw new IncorrectPasswordException(loginForm.getUsername());
        }
    }

    /**
     * Registers a new user.
     * @param newUser The user object containing registration details.
     * @return ResponseEntity containing the registered user object.
     * @throws UserAlreadyExistsException If the user already exists.
     */
    @SecurityRequirements
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user to the library system and encrypts the password before saving.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registration successful", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, user details not valid or user already exists")
    })
    public ResponseEntity<?> register(@RequestBody @Parameter(description = "New user object with registration details") User newUser) {
        userService.findByUsername(newUser.getUsername())
                .ifPresent(s -> {
                    throw new UserAlreadyExistsException(newUser.getUsername());
                });

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User user = userService.saveUser(newUser);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


@GetMapping("/isLoggedIn")
@Operation(summary = "Check if user is logged in", description = "Checks if the user is logged in by verifying the authentication token.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked the login status", content = @Content(schema = @Schema(implementation = Boolean.class))),
})
public ResponseEntity<Boolean> isLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
    return ResponseEntity.ok(isLoggedIn);
}
}
