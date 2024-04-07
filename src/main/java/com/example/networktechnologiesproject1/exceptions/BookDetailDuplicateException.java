package com.example.networktechnologiesproject1.exceptions;

public class BookDetailDuplicateException extends RuntimeException {
    public BookDetailDuplicateException(String field) {
        super("Duplicate book detail found for " + field);
    }
}
