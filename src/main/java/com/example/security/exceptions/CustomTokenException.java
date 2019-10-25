package com.example.security.exceptions;

public class CustomTokenException extends RuntimeException{

    public CustomTokenException(String message) {
        super(message);
    }
}
