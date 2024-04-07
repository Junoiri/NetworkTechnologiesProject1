package com.example.networktechnologiesproject1.exceptions;

public class BookDetailNotFoundException extends RuntimeException {
    public BookDetailNotFoundException(Integer id) {
        super("Book detail with id " + id + " not found");
    }
}
