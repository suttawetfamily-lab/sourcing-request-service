package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SourcingRequestDisplayDto {
    private String tenantId;
    private Boolean isShowExistingPriceCreateSourcing;
    private Boolean isShowStep3;
    private Boolean isShowAllApprovalTap;
    private Boolean isShowSourcingApprovalTap;
    private String approveSourcingPrivilegeCodes;
    private Boolean isShowServices;

    private ViewRequestDisplayDto viewRequest;
    private RequestItemDisplayDto requestItem;

    private Boolean isShowMenuAssignToMe;
    private Boolean isShowMenuEditRequestForApprover;
    private Boolean isShowApprovalReportLine;
    private Boolean isShowRemoveFromMyTaskActionMenu;
    private Boolean isShowApprovalApproverGroup;
    private Boolean isShowForwardApprovalWorkflow;

    private String historyModalRequestNameLabel;
    private String historyModalProjectNameLabel;
    private String historyModalProjectNameFormat;
    private String requestNoteLabel;
    private String viewApproveRequestItemTableTemplate;
    private String approvalSectionTemplate;

    private String exportReportErrorMessageInvalidDateRange;
    private String exportReportTitleSelectDate;

    private List<SourcingMenuDto> existingPriceSourcingMenu;
    private String defaultApproverHeadersTemplate;
    private String defaultSourcingItemApproverHeaders;
    private List<InstanceApproverHeaderDto> defaultApproverHeaders;
    private String requestItemReportLabel;
    private String requestItemReportName;
    private Integer requestReportId;

    private String reportProjectFormat;

    private String srLogoImageFileId;
    private String srLogoImageStyles;

    private String epLogoImageFileId;
    private String epLogoImageStyles;

    private String seLogoImageFileId;
    private String seLogoImageStyles;

    private String erfxLogoImageFileId;
    private String erfxLogoImageStyles;

    private String uamAdminLogoImageFileId;
    private String uamAdminLogoImageStyles;

    private String dashboardLogoImageFileId;
    private String dashboardLogoImageStyles;
    private Boolean visibleSourcingDocNo;

    private String excludeOrganizations;

}
