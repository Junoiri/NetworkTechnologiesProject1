package com.example.networktechnologiesproject1.exceptions;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(Integer id) {
        super("User with Id " + id + " not found.");
    }
}
