package com.pantavanij.sourcingreq.services.exception;

public class ValidateBodyException extends RuntimeException {
    public ValidateBodyException(String msg) {
        super(msg);
    }

    public ValidateBodyException(Throwable ex) {
        super(ex);
    }
}
