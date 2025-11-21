package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomSseEmitter extends SseEmitter {
    public CustomSseEmitter(String uuid, Long timeout){
        super(timeout);
        this.uuid = uuid;
    }
    private String uuid;
}

