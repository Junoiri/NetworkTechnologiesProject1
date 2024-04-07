package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that the provided password is incorrect.
 * This exception may be used when attempting to authenticate a user with an incorrect password.
 */
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String username) {
        super("Incorrect password for username " + username);
    }
}
