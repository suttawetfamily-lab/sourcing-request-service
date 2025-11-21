package com.pantavanij.sourcingreq.services.domain.dto.requester;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class RequesterRequestSearchDataDto {
    private Long recId;
    private RequestTenantDto tenant;
    private String requestNo;
    private Long workflowInstanceId;
    private Integer organizationId;
    private Integer requestTypeId;
    private String requestName;
    private Timestamp expectedDate;
    private CurrencyDto currencyObj;
    private BigDecimal budget;
    private Timestamp requestDate;
    private String phone;
    private String mobile;
    private String email;
    private RequestStatusDto requestStatus;
    private ApprovalStatusDto approvalStatus;
    private String noteToApprovers;
    private String approvalType;
    private Timestamp approvalDate;
    private String approvedReason;
    private String rejectedReason;
    private String cancellationReason;
    private String assignedBy;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private DepartmentDto departmentDto;
    private String refPRNumber;

    private String objective;
    private String budgetRefNo;
    private String projectCode;
    private String projectName;
    private String projectLabel;
    private OptionDto requestTypeObj;
    private OptionDto objectiveObj;
    private OptionDto categoryObj;
    private OptionDto subCategoryObj;
    private OptionDto purchaserObj;
    private OptionDto projectObj;
    private OptionDto typeObj;
    private OptionDto departmentObj;
    private OptionDto budgetRefNoObj;
    private OptionDto organizationObj;
    private OptionDto vatTypeId;
    private LocationDto deliveryLocation;
    private String location;
    private String contactName;
    private String contactPhone;

    private String assignedByName;
    private String createdByName;
    private String updatedByName;

    private String tags;
}
