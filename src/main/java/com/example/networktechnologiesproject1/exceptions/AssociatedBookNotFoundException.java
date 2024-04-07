package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that no associated book was found.
 * This exception may be used when attempting to find a book based on its detail with a specific ID.
 */
public class AssociatedBookNotFoundException extends RuntimeException {
    public AssociatedBookNotFoundException(Integer bookId) {
        super("No book found for book detail with id " + bookId);
    }
}
