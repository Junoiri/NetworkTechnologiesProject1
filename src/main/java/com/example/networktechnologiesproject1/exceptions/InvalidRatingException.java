package com.example.networktechnologiesproject1.exceptions;

public class InvalidRatingException extends IllegalArgumentException {
    public InvalidRatingException(String message) {
        super(message);
    }
}
