package com.example.networktechnologiesproject1.exceptions;

public class LoanDateException extends RuntimeException {
    public LoanDateException(String message) {
        super("Loan date error: " + message);
    }
}
