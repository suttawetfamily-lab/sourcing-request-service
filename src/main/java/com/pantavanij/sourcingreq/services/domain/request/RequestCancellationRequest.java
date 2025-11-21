package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class RequestCancellationRequest {
    private Long recId;
    private String reason;
}
