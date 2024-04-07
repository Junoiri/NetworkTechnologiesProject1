package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating an error during user authorization.
 * This exception may be used when encountering issues with user authorization.
 */
public class UserAuthorizationException extends RuntimeException {
    public UserAuthorizationException(String message) {
        super(message);
    }
}
