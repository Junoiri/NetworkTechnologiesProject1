package com.example.networktechnologiesproject1.exceptions;

public class AssociatedBookNotFoundException extends RuntimeException {
    public AssociatedBookNotFoundException(Integer bookId) {
        super("No book found for book detail with id " + bookId);
    }
}
