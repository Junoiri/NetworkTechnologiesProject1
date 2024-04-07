package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating an error related to loan dates.
 * This exception may be used when encountering issues with loan dates during book lending operations.
 */
public class LoanDateException extends RuntimeException {
    public LoanDateException(String message) {
        super("Loan date error: " + message);
    }
}
