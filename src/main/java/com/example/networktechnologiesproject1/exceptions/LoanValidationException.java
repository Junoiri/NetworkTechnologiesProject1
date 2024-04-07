package com.example.networktechnologiesproject1.exceptions;

public class LoanValidationException extends RuntimeException {
    public LoanValidationException(String message) {
        super("Loan validation failed: " + message);
    }
}
