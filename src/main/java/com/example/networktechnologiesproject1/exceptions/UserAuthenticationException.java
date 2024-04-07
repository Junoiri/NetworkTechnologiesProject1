package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating an error during user authentication.
 * This exception may be used when encountering issues during user authentication.
 */
public class UserAuthenticationException extends RuntimeException {
    public UserAuthenticationException(String message) {
        super(message);
    }
}
