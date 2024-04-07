package com.example.networktechnologiesproject1.exceptions;

public class UnauthorizedDetailChangeException extends RuntimeException {
    public UnauthorizedDetailChangeException() {
        super("Unauthorized attempt to change book detail");
    }
}
