package com.pantavanij.sourcingreq.services.exception;

import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public class BadRequestException extends RuntimeException {

    private final ApiMessage apiMessage;
    private final String description;

    public BadRequestException(String message) {
        super(message);
        this.description = message;
        this.apiMessage = ApiMessage.E1002;
    }

    public BadRequestException(ApiMessage apiMessage) {
        super(apiMessage.toString());
        this.apiMessage = apiMessage;
        this.description = apiMessage.description();
    }

    public BadRequestException(ApiMessage apiMessage, String description) {
        super(apiMessage.name() + ": " + description);
        this.apiMessage = apiMessage;
        this.description = description;
    }

    public BadRequestException(ApiMessage apiMessage, String description, Throwable cause) {
        super(apiMessage.name() + ": " + description, cause);
        this.apiMessage = apiMessage;
        this.description = description;
    }


    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.apiMessage = ApiMessage.E1002;
        this.description = apiMessage.description();
    }

    public ApiMessage getApiMessage() {
        return apiMessage;
    }

    public String getDescription() {
        return description;
    }
}