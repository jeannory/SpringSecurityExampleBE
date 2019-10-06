package com.example.security.exceptions;

public class CustomInitializationException extends RuntimeException{

    public CustomInitializationException() {
    }

    public CustomInitializationException(String message) {
        super(message);
    }
}
