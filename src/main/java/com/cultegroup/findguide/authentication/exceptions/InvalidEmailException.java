package com.cultegroup.findguide.authentication.exceptions;

import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class InvalidEmailException extends HttpStatusException {

    public InvalidEmailException(String message, HttpStatus status) {
        super(message, status);
    }
}
