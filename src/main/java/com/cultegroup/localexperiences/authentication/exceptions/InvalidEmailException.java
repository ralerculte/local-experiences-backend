package com.cultegroup.localexperiences.authentication.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends RuntimeException {

    private final HttpStatus httpStatus;

    public InvalidEmailException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
