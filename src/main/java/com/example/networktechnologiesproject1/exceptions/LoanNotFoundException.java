package com.example.networktechnologiesproject1.exceptions;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Integer id) {
        super("Loan with id " + id + " not found");
    }
}
