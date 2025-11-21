package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO สำหรับ ExcSourcing search response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcSourcingDto {

    private Long recId;

    private String excSourcingDocNo;

    private Long sourcingTypeId;
    private String sourcingTypeName;

    private SourcingStatusDto excSourcingStatus;
    private String excSourcingStatusName;

    private DeptApprovalStatusDto approvalStatus; // field รวม
    private DeptApprovalStatusDto deptApprovalStatus;
    private DeptApprovalStatusDto purApprovalStatus;

    private String purchaser;

    private String createdBy;
    private String createdByName;
    private Timestamp createdDate;

    private String updatedBy;
    private String updatedByName;
    private Timestamp updatedDate;

    // ===== Fields ที่ได้จาก Request (ผ่าน RequestItem) =====
    private RequestDto request;

    // ใช้เวลาสร้าง label ใน UI
    private String projectCode;
    private String projectName;
    private String projectLabel;

    // organization object สำหรับ dropdown
    private OptionDto organizationObj;

    // background (กรณี BAY tenant)
    private String background;

    // tags (เช่น typeName)
    private String tags;

    private List<InstanceApproverHeaderDto> approverHeaders;

    private boolean canApprove;
    private boolean canReject;
}
