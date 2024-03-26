package com.example.networktechnologiesproject1.exceptions;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String username) {
        super("Incorrect password for username " + username);
    }
}
