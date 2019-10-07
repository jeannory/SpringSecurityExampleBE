package com.example.security.exceptions;

public class CustomTransactionalException extends RuntimeException {

    public CustomTransactionalException() {
    }

    public CustomTransactionalException(String message) {
        super(message);
    }
}
