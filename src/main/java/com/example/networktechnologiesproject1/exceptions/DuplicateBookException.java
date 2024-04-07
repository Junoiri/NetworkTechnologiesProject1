package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a duplicate book was found.
 * This exception may be used when attempting to add a book with an ISBN that already exists in the repository.
 */
public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String isbn) {
        super("A book with ISBN " + isbn + " already exists");
    }
}
