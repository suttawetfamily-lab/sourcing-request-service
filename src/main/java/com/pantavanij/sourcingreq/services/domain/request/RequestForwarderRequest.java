package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class RequestForwarderRequest {
    private Long requestId;
    private String requestApproveName;
    private String requestForwarderName;
}
