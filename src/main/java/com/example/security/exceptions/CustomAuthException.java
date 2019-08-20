package com.example.security.exceptions;

public class CustomAuthException extends RuntimeException {

    public CustomAuthException(String message) {
        super(message);
    }
}
