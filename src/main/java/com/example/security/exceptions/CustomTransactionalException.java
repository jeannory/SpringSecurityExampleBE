package com.example.security.exceptions;
//https://o7planning.org/fr/11661/tutoriel-spring-boot-jpa-et-spring-transaction
public class CustomTransactionalException extends RuntimeException {

    public CustomTransactionalException() {
    }

    public CustomTransactionalException(String message) {
        super(message);
    }
}
