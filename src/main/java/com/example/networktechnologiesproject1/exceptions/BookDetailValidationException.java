package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating a validation error for book details.
 * This exception may be used when validating book details and encountering invalid data.
 */
public class BookDetailValidationException extends RuntimeException {
    public BookDetailValidationException(String message) {
        super(message);
    }
}
