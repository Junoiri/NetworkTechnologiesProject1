package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating an unauthorized attempt to change book detail.
 * This exception may be used when attempting to modify book details without proper authorization.
 */
public class UnauthorizedDetailChangeException extends RuntimeException {
    public UnauthorizedDetailChangeException() {
        super("Unauthorized attempt to change book detail");
    }
}
