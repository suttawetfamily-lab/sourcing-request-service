package com.pantavanij.sourcingreq.services.domain.dto;

import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.DeptApprovalStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ExcSourcingDeptApproverDto {
    private Long recId;
    private String approverName;
    private String comment;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String createdByName;
    private DeptApprovalStatus deptApprovalStatus;
    private ApproverDto approverDto;
}
