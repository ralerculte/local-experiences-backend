package com.cultegroup.findguide.data.exceptions;

import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class WrongFieldException extends HttpStatusException {
    public WrongFieldException(String message, HttpStatus status) {
        super(message, status);
    }
}
