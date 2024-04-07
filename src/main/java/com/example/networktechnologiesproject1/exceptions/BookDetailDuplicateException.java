package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a duplicate book detail was found.
 * This exception may be used when attempting to add or update a book detail with a field that must be unique,
 * such as ISBN.
 */
public class BookDetailDuplicateException extends RuntimeException {
    public BookDetailDuplicateException(String field) {
        super("Duplicate book detail found for " + field);
    }
}
