package com.example.networktechnologiesproject1.exceptions;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(Integer bookId) {
        super("Book with id " + bookId + " is not available for loan");
    }
}
