package com.example.networktechnologiesproject1.exceptions;

public class InvalidCoverImageUrlException extends RuntimeException {
    public InvalidCoverImageUrlException(String url) {
        super("Invalid cover image URL: " + url);
    }
}
