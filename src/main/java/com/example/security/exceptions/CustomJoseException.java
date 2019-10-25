package com.example.security.exceptions;

public class CustomJoseException extends RuntimeException {

//    public CustomJoseException() {
//    }

    public CustomJoseException(String message) {
        super(message);
    }
}
