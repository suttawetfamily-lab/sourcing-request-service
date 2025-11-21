package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.Data;

@Data
public class DefaultApprovalDto {

    private Integer sequence;
    private Integer approvalTypeId;
    private String approvalUserName;
}
