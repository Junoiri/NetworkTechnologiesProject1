package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a duplicate user was found.
 * This exception may be used when attempting to register a user with a username that already exists in the system.
 */
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String username) {
        super("User with username " + username + " already exists.");
    }
}
