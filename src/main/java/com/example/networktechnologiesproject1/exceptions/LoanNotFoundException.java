package com.example.networktechnologiesproject1.exceptions;

/**
 * Exception indicating that a loan was not found.
 * This exception may be used when attempting to retrieve, update, or delete a loan by its ID.
 */
public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Integer id) {
        super("Loan with id " + id + " not found");
    }
}
