package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class ExcSourcingApprovalRequest {
    private Long excSourcingId;
    private String reason;
}
