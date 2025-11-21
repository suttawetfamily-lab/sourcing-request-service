package com.pantavanij.sourcingreq.services.domain.request;

import lombok.Data;

@Data
public class RequestDeptApprovalRequest {
    private Long requestId;
    private String reason;
}
