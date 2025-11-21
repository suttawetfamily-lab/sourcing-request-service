package com.pantavanij.sourcingreq.services.exception;


import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {
    private final ApiMessage apiMessage;
    private final String description;

    public AppException(String message) {
        super(message);
        this.description = message;
        this.apiMessage = ApiMessage.E1003;
    }

    public AppException(ApiMessage apiMessage) {
        super(apiMessage.toString());
        this.apiMessage = apiMessage;
        this.description = apiMessage.description();
    }

    public AppException(ApiMessage apiMessage, String description) {
        super(apiMessage.name() + ": " + description);
        this.apiMessage = apiMessage;
        this.description = description;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.apiMessage = ApiMessage.E1003;
        this.description = apiMessage.description();
    }

    public AppException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.apiMessage = ApiMessage.E1001;
        this.description = apiMessage.description();
    }

}
