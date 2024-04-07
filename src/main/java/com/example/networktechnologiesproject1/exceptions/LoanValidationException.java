package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that loan validation failed.
 * This exception may be used when encountering issues during the validation of loan-related operations.
 */
public class LoanValidationException extends RuntimeException {
    public LoanValidationException(String message) {
        super("Loan validation failed: " + message);
    }
}
