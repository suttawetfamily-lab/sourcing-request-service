package com.pantavanij.sourcingreq.services.domain.dto.approver;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ForwardedApprover {
    private String fromApprover;
    private String forwardedApprover;
    private Timestamp forwaredDate;
}
