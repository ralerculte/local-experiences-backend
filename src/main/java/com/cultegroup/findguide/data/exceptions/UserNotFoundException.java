package com.cultegroup.findguide.data.exceptions;

import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends HttpStatusException {
    public UserNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
