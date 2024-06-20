package com.example.networktechnologiesproject1.controllers;

import com.example.networktechnologiesproject1.entities.Book;
import com.example.networktechnologiesproject1.entities.User;
import com.example.networktechnologiesproject1.exceptions.*;
import com.example.networktechnologiesproject1.repositories.BookRepository;
import com.example.networktechnologiesproject1.repositories.LoanRepository;
import com.example.networktechnologiesproject1.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "Endpoints for managing users within the library system, including creating, retrieving, updating, and deleting user records.")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    @PostMapping("/add")
//    public ResponseEntity<User> addUser(@RequestBody User user) {
//        try {
//            logger.info("Adding user with details: {}", user);
//            User newUser = userService.addUser(user);
//            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
//        } catch (InvalidUserDetailsException e) {
//            logger.error("Error occurred while adding user: {}", e.getMessage());
//            throw e;
//        }
//    }
    @GetMapping("/current")
@Operation(summary = "Get current user's ID", description = "Retrieves the ID of the currently logged in user.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user's ID", content = @Content(schema = @Schema(implementation = Integer.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
})
public ResponseEntity<Integer> getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserName = authentication.getName();

    User currentUser = userRepository.findByUsername(currentUserName)
            .orElseThrow(() -> new UserNotFoundException(currentUserName));

    return ResponseEntity.ok(currentUser.getId());
}
@GetMapping("/{id}/current")
@Operation(summary = "Get current user object", description = "Retrieves the current user object based on the user ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user object", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
})
public ResponseEntity<User> getCurrentUser(@PathVariable @Parameter(description = "Unique identifier of the user") Integer id) {
    User currentUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));

    return ResponseEntity.ok(currentUser);
}

//@PostMapping("/add")
//@Operation(summary = "Add a user", description = "Registers a new user to the library system. Encrypts the password before saving and ensures the username is unique.")
//@ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "User successfully added", content = @Content(schema = @Schema(implementation = User.class))),
//        @ApiResponse(responseCode = "400", description = "Invalid user details or duplicate username")
//})
//@PreAuthorize("hasAuthority('ROLE_STAFF')")
//public User addUser(@RequestBody @Parameter(description = "User object containing new user details such as username, password, email, name, and role") User user) {
//    logger.info("Attempting to add user: {}", user.toString());
//
//    userRepository.findByUsername(user.getUsername())
//            .ifPresent(u -> {
//                logger.error("Duplicate username: {}", user.getUsername());
//                throw new DuplicateUserException(user.getUsername());
//            });
//    if (user.getPassword() == null || user.getUsername() == null || user.getEmail() == null) {
//        logger.error("Invalid user details for username: {}", user.getUsername());
//        throw new InvalidUserDetailsException("User details are invalid.");
//    }
//    user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
//    User savedUser = userRepository.save(user);
//    logger.info("User with username: {} added successfully", savedUser.getUsername());
//    return savedUser;
//}

@PostMapping("/add")
@ResponseStatus(HttpStatus.CREATED)
@Operation(summary = "Add a new user", description = "Registers a new user to the library system. Encrypts the password before saving and ensures the username is unique.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully added", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user details supplied"),
        @ApiResponse(responseCode = "409", description = "Duplicate username provided")
})
public User addUser(@RequestBody User user, HttpServletRequest request) throws IOException {
    String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    logger.info("Raw JSON body: {}", body);
    logger.debug("Received user details: " + user.toString());

    if (user.getPassword() == null) {
        throw new IllegalArgumentException("Password cannot be null");
    }
        logger.info("Attempting to add user: {}", user.toString());

    userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
        logger.error("Duplicate username: {}", user.getUsername());
        throw new DuplicateUserException(user.getUsername());
    });

    user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password before saving
    User savedUser = userRepository.save(user);
    logger.info("User with username: {} added successfully", savedUser.getUsername());
    return savedUser;
}

    @PostMapping("/testAdd")
    public ResponseEntity<String> testAddUser(@RequestBody String rawJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(rawJson, User.class);
            return ResponseEntity.ok("User parsed: " + user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error parsing JSON: " + e.getMessage());
        }
    }



    @PostMapping("/manualAdd")
    public ResponseEntity<User> manualAddUser(@RequestBody String rawJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(rawJson, User.class);
        logger.debug("Manually parsed user: " + user.toString());
        return ResponseEntity.ok(user);
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

//    @PutMapping("/update/{id}")
//    @Operation(summary = "Update a user", description = "Updates the details of an existing user. Passwords are encrypted upon updating. Only staff can perform updates.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "User successfully updated", content = @Content(schema = @Schema(implementation = User.class))),
//            @ApiResponse(responseCode = "400", description = "Invalid user details supplied for update"),
//            @ApiResponse(responseCode = "404", description = "User not found with the specified ID")
//    })
//    @PreAuthorize("hasAuthority('ROLE_STAFF')")
//    public ResponseEntity<User> updateUser(@PathVariable @Parameter(description = "Unique identifier of the user to update") Integer id,
//                                           @RequestBody(description = "User object with updated details") User userDetails) {
//        User existingUser = userRepository.findById(id)
//                .orElseThrow(() -> new UserIdNotFoundException(id));
//
//        if (userDetails.getPassword() != null) {
//            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Encrypt new password
//        }
//        existingUser.setUsername(userDetails.getUsername());
//        existingUser.setEmail(userDetails.getEmail());
//        existingUser.setName(userDetails.getName());
//        existingUser.setRole(userDetails.getRole());
//
//        User updatedUser = userRepository.save(existingUser);
//
//        // Idea for manual authorization check
//        /*
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!authentication.getName().equals(existingUser.getUsername())) {
//            throw new UserAuthorizationException("Not authorized to update this user.");
//        }
//        */
//
//        return ResponseEntity.ok(updatedUser);
//    }

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

    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/{id}/loanCount")
    @Operation(summary = "Get loan count for a specific user", description = "Retrieves the count of all loans for a specific user by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the loan count for the user"),
            @ApiResponse(responseCode = "404", description = "User not found with the provided ID")
    })
    public ResponseEntity<Long> getLoanCountByUserId(@PathVariable @Parameter(description = "Unique identifier of the user to retrieve loan count for") Integer id) {
        long count = loanRepository.countByUserId(id);
        return ResponseEntity.ok(count);
    }
}
