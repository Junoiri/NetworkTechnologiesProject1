package com.example.networktechnologiesproject1.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String username) {
        super(String.format("User with username %s already exists", username));
    }
}
