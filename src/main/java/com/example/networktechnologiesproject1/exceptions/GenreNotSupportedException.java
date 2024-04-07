package com.example.networktechnologiesproject1.exceptions;

public class GenreNotSupportedException extends RuntimeException {
    public GenreNotSupportedException(String genre) {
        super("Genre '" + genre + "' is not supported");
    }
}
