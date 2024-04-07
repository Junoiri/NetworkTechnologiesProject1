package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.exceptions.*;
import com.example.networktechnologiesproject1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "Endpoints for managing users within the library system, including creating, retrieving, updating, and deleting user records.")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a user", description = "Registers a new user to the library system. Encrypts the password before saving and ensures the username is unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully added", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details or duplicate username")
    })
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public User addUser(@RequestBody(description = "User object containing new user details such as username, password, email, name, and role") User user) {

        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new DuplicateUserException(user.getUsername());
                });
        if (user.getPassword() == null || user.getUsername() == null || user.getEmail() == null) {
            throw new InvalidUserDetailsException("User details are invalid.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
        return userRepository.save(user);
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users registered in the library system, including their details such as username, email, and role.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all users", content = @Content(schema = @Schema(implementation = User.class)))
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID", description = "Retrieves detailed information of a specific user by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user details", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<User> getUserById(@PathVariable @Parameter(description = "Unique identifier of the user to retrieve") Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a user", description = "Updates the details of an existing user. Passwords are encrypted upon updating. Only staff can perform updates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details supplied for update"),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<User> updateUser(@PathVariable @Parameter(description = "Unique identifier of the user to update") Integer id,
                                           @RequestBody(description = "User object with updated details") User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));

        if (userDetails.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Encrypt new password
        }
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setName(userDetails.getName());
        existingUser.setRole(userDetails.getRole());

        User updatedUser = userRepository.save(existingUser);

        // Idea for manual authorization check
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals(existingUser.getUsername())) {
            throw new UserAuthorizationException("Not authorized to update this user.");
        }
        */

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a user", description = "Removes a user record from the system. This action is irreversible and should be performed with caution.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
    })
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(description = "Unique identifier of the user to delete") Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));

        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
