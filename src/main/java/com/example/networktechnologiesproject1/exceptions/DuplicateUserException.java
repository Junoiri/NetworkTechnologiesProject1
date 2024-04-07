package com.example.networktechnologiesproject1.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String username) {
        super("User with username " + username + " already exists.");
    }
}
