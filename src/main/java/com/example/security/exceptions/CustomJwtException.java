package com.example.security.exceptions;
//if exception in TokenUtility.validateToken, TokenUtilityProvider.validateJsonWebKey, TokenUtilityProvider.getStringFromJwtNode
public class CustomJwtException extends RuntimeException{

    public CustomJwtException(String message) {
        super(message);
    }
}
