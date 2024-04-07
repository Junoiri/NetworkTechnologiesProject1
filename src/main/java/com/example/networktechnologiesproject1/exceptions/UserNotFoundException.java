package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a user with a specific username was not found.
 * This exception may be used when attempting to retrieve, update, or delete a user by its username.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with username " + username + " not found.");
    }
}
