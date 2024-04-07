package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a user already exists.
 * This exception may be used when attempting to register a user with a username that is already taken.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String username) {
        super(String.format("User with username %s already exists", username));
    }
}
