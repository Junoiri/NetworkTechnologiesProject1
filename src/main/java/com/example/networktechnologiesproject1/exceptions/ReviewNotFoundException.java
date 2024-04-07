package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a review was not found.
 * This exception may be used when attempting to retrieve, update, or delete a review that does not exist.
 */
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
