package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that the provided user details are invalid.
 * This exception may be used when attempting to perform an operation with user details that do not meet the required criteria.
 */
public class InvalidUserDetailsException extends RuntimeException {
    public InvalidUserDetailsException(String message) {
        super(message);
    }
}
