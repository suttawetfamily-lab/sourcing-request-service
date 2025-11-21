package com.pantavanij.sourcingreq.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
