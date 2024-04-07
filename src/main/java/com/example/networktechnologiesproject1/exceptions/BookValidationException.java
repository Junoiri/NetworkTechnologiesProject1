package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating a validation error for a book.
 * This exception may be used when validating a book entity and encountering invalid data.
 */
public class BookValidationException extends RuntimeException {
    public BookValidationException(String message) {
        super(message);
    }
}
