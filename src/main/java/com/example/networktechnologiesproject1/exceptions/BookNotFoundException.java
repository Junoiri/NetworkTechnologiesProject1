package com.example.networktechnologiesproject1.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Integer id) {
        super("Book with id " + id + " not found");
    }
}
