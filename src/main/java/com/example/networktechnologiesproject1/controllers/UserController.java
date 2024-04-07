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

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
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
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
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
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));

        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
