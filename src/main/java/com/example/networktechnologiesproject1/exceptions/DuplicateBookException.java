package com.example.networktechnologiesproject1.exceptions;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String isbn) {
        super("A book with ISBN " + isbn + " already exists");
    }
}
