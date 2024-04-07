package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a duplicate review was found.
 * This exception may be used when attempting to add a review that already exists for a book.
 */
public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException(String message) {
        super(message);
    }
}
