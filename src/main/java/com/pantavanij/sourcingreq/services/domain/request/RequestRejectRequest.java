package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class RequestRejectRequest {
    private Long requestId;
    private String reason;
}
