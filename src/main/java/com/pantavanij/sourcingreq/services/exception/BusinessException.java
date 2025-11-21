package com.pantavanij.sourcingreq.services.exception;

import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.*;

@Getter
public class BusinessException extends RuntimeException {
    private final ApiMessage apiMessage;
    private final String description;

    public BusinessException(String message) {
        super(message);
        this.description = message;
        this.apiMessage = ApiMessage.E1003;
    }

    public BusinessException(ApiMessage apiMessage) {
        super(apiMessage.toString());
        this.apiMessage = apiMessage;
        this.description = apiMessage.description();
    }

    public BusinessException(ApiMessage apiMessage, String description) {
        super(apiMessage.name() + ": " + description);
        this.apiMessage = apiMessage;
        this.description = description;
    }

    public BusinessException(ApiMessage apiMessage, String description, Throwable cause) {
        super(apiMessage.name() + ": " + description, cause);
        this.apiMessage = apiMessage;
        this.description = description;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.apiMessage = ApiMessage.E1003;
        this.description = apiMessage.description();
    }

}
