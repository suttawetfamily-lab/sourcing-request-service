package com.pantavanij.sourcingreq.services.domain.dto.sourcingapprover;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class SourcingApproverRequestDto {
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
    private DeptApprovalStatusDto deptApprovalStatus;
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
    private List<RequestItemV2Dto> requestItemList;
    private List<RequestAttachmentDto> requestAttachmentList;
    private String refPRNumber;
    private List<InstanceApproverHeaderDto> approverHeaders;
    @JsonIgnore
    private List<RequestDeptApproverDto> requestDeptApproverDtoList;

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

    // additional data
    private String background;
    private OptionDto whtAbsorbedBy;
    private Timestamp evaluationDate;
    private String requester;
    private OptionDto performanceEvaluation;
    private OptionDto pdpaQ01;
    private OptionDto pdpaQ02;
    private OptionDto pdpaQ03;
    private OptionDto pdpaQ04;
    private OptionDto pdpaQ05;
    private OptionDto pdpaQ06;
    private String relatePdpa;
    private String thirdPartyRole;
    private String dpaType;
    private OptionDto outsourceService;
    private String department;
    private String approveNo;
    private String costcenter;
    private OptionDto makingContract;
    private OptionDto makingRptContract;
    private OptionDto needWhtCert;
    private OptionDto vatAbsorbedBy;
    private OptionDto stampDuty;
    private String makingContractReason;
    private String makingRptContractReason;
    private Long formId;
    private FormDTO questionnaire;
    private String delegateActionBy;
    private Timestamp delegateActionDate;

    //Optional properties for Edit mode
    private Boolean isOwnerPurchaser;

}
