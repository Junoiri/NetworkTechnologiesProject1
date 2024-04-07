package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a user with a specific ID was not found.
 * This exception may be used when attempting to retrieve, update, or delete a user by its ID.
 */
public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(Integer id) {
        super("User with Id " + id + " not found.");
    }
}
