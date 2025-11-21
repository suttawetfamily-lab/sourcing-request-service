package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

@Data
public class SendMailToRoleDto {
    private boolean purchaser;
    private boolean approver;
    private boolean reportLine;
    private boolean reviewer;
    private boolean requester;
    private boolean excSourcingApprover;
    private boolean excSourcingPurchaser;
}
