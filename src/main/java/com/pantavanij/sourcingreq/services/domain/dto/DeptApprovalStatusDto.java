package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeptApprovalStatusDto {
    private Integer recId;
    private String name;
    private String description;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canDuplicate;
    private boolean canCancel;
    private boolean canCopyToPr;
    private boolean canViewHistory;
    private boolean canApprove;
    private boolean canReject;
    private String createdBy;
    private Timestamp createdDate;
}
