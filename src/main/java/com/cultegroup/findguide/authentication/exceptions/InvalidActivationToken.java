package com.cultegroup.findguide.authentication.exceptions;

import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class InvalidActivationToken extends HttpStatusException {
    public InvalidActivationToken(String message, HttpStatus status) {
        super(message, status);
    }
}
