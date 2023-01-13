package com.cultegroup.findguide.authentication.exceptions;

public class InvalidActivationToken extends RuntimeException {
    public InvalidActivationToken(String message) {
        super(message);
    }
}
