package com.cultegroup.localexperiences.exceptions;

public class InvalidActivationToken extends RuntimeException {
    public InvalidActivationToken(String message) {
        super(message);
    }
}
