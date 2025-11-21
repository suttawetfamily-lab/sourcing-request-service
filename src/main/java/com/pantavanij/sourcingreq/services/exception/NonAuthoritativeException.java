package com.pantavanij.sourcingreq.services.exception;

public class NonAuthoritativeException extends RuntimeException  {
    public NonAuthoritativeException(String msg) {
        super(msg);
    }

    public NonAuthoritativeException(Throwable ex) {
        super(ex);
    }
}

