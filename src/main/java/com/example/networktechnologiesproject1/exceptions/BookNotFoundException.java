package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a book was not found.
 * This exception may be used when attempting to retrieve, update, or delete a book by its ID.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Integer id) {
        super("Book with id " + id + " not found");
    }
}
