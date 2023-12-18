package com.example.exceptions;

public class BookAlreadyPresentException extends RuntimeException {
    public BookAlreadyPresentException(String message) {
        super(message);
    }
}
