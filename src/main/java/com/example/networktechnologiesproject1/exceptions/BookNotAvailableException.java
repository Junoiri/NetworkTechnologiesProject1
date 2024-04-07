package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a book is not available for loan.
 * This exception may be used when attempting to loan a book that is not currently available.
 */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(Integer bookId) {
        super("Book with id " + bookId + " is not available for loan");
    }
}
