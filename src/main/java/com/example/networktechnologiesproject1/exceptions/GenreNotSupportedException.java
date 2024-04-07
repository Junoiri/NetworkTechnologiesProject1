package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a genre is not supported.
 * This exception may be used when attempting to perform an operation with an unsupported genre.
 */
public class GenreNotSupportedException extends RuntimeException {
    public GenreNotSupportedException(String genre) {
        super("Genre '" + genre + "' is not supported");
    }
}
