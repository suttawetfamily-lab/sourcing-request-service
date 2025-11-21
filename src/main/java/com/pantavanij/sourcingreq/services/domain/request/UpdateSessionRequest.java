package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class UpdateSessionRequest {
    private String tenantId;
    private String sessionID;
}
