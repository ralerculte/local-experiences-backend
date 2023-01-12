package com.cultegroup.localexperiences.data.exceptions;

import com.cultegroup.localexperiences.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;

public class InvalidUpdateToken extends HttpStatusException {
    public InvalidUpdateToken(String message, HttpStatus status) {
        super(message, status);
    }
}
