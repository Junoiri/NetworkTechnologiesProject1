package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that the provided rating is invalid.
 * This exception may be used when attempting to set a rating that does not meet the required criteria.
 */
public class InvalidRatingException extends IllegalArgumentException {
    public InvalidRatingException(String message) {
        super(message);
    }
}
