package com.cultegroup.localexperience.exceptions;

public class InvalidActivationToken extends RuntimeException {
    public InvalidActivationToken(String message) {
        super(message);
    }
}
