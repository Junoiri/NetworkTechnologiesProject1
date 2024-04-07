package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a book detail was not found.
 * This exception may be used when attempting to retrieve, update, or delete a book detail by its ID.
 */
public class BookDetailNotFoundException extends RuntimeException {
    public BookDetailNotFoundException(Integer id) {
        super("Book detail with id " + id + " not found");
    }
}
