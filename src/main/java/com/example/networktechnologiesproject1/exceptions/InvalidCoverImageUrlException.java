package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that the provided cover image URL is invalid.
 * This exception may be used when attempting to set a cover image URL that does not meet the required format.
 */
public class InvalidCoverImageUrlException extends RuntimeException {
    public InvalidCoverImageUrlException(String url) {
        super("Invalid cover image URL: " + url);
    }
}
