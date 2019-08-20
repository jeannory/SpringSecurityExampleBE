package com.example.security.exceptions;

public class CustomMalformedClaimException extends RuntimeException{

    public CustomMalformedClaimException(String message) {
        super(message);
    }
}
