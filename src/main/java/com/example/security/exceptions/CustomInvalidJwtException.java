package com.example.security.exceptions;

public class CustomInvalidJwtException extends RuntimeException{

    public CustomInvalidJwtException(String message) {
        super(message);
    }
}
