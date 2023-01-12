package com.cultegroup.localexperiences.authentication.exceptions;

import com.cultegroup.localexperiences.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class InvalidEmailException extends HttpStatusException {

    public InvalidEmailException(String message, HttpStatus status) {
        super(message, status);
    }
}
