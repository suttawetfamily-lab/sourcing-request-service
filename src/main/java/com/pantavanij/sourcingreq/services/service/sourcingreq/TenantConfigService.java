package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface TenantConfigService {
    String getWorkflowTemplateId(Integer tenanId);

    String getWorkflowTemplateIdForERFX(Integer tenanId);

    String getProjectListURL(Integer tenanId);

    String getCreateToPrURL(Integer tenanId);
    String getCreateSupplier(Integer tenanId);
    String getGetSupplier(Integer tenanId);

    String getEPAuthPrivilegeCode(Integer tenantId, String section);

    String getWorkflowApprovalType(Integer tenantId);

    Long getEmailTemplateIdByName(String name, Integer tenantId);

    String getEmailAddressDefault(Integer tenantId);

    Long getMaxSessionTimeout(Integer tenantId);

    String getEmailImageByName(String name, Integer tenantId);

    String getEmailLoginUrlEPByName(String name, Integer tenantId);

    String getLimitSearchSupplierWebwork(Integer tenantId);

    String getRequestItemTemplate(Integer tenantId);

    String getRequestItemReportName(Integer tenantId);

    boolean getBorgAllConfig(Integer tenantId);

    boolean getEPTimeoutConfig(Integer tenantId);

    boolean getApprovalAllTapConfig(Integer tenantId);

    boolean getApprovalSourcingTapConfig(Integer tenantId);

    String getApproveSourcingPrivilegeCodes(Integer tenantId);

    String getExcludeOrganizations(Integer tenantId);

    String getCopyToPrPrivilegeCodes(Integer tenantId);

    String getCopyToPrOrganizations(Integer tenantId);

    boolean getForwardApprovalWorkflow(Integer tenantId);

    boolean getExistingPriceOptionConfig(Integer tenantId);
//    boolean getConfigurationExistingPriceItemMenu(Integer tenantId);
    String getEmailImageUrl(Integer tenantId);

    String getERFXNoMaxItems(Integer tenantId);

    String getERFXAuthCode(Integer tenantId);
    boolean setERFXAuthCode(Integer tenantId, String eRFXAuthCode);

    String getSRLogoImageFileId(Integer tenantId);
    boolean setSRLogoImageFileId(Integer tenantId, String fileId);
    String getSRLogoImageStyles(Integer tenantId);
    boolean setSRLogoImageStyles(Integer tenantId, String logoImageStyles);

    String getEPLogoImageFileId(Integer tenantId);
    boolean setEPLogoImageFileId(Integer tenantId, String fileId);
    String getEPLogoImageStyles(Integer tenantId);
    boolean setEPLogoImageStyles(Integer tenantId, String logoImageStyles);

    String getSELogoImageFileId(Integer tenantId);
    boolean setSELogoImageFileId(Integer tenantId, String fileId);
    String getSELogoImageStyles(Integer tenantId);
    boolean setSELogoImageStyles(Integer tenantId, String logoImageStyles);

    String getERFXLogoImageFileId(Integer tenantId);
    boolean setERFXLogoImageFileId(Integer tenantId, String fileId);
    String getERFXLogoImageStyles(Integer tenantId);
    boolean setERFXLogoImageStyles(Integer tenantId, String logoImageStyles);

    String getUAMAdminLogoImageFileId(Integer tenantId);
    boolean setUAMAdminLogoImageFileId(Integer tenantId, String fileId);
    String getUAMAdminLogoImageStyles(Integer tenantId);
    boolean setUAMAdminLogoImageStyles(Integer tenantId, String logoImageStyles);

    String getDashboardLogoImageFileId(Integer tenantId);
    boolean setDashboardLogoImageFileId(Integer tenantId, String fileId);
    String getDashboardLogoImageStyles(Integer tenantId);
    boolean setDashboardLogoImageStyles(Integer tenantId, String logoImageStyles);

    boolean getFilterReportConfiguration(Integer tenantId);

    boolean getStep3(Integer tenantId);

    boolean getIsShowServices(Integer tenantId);

    boolean getIsShowViewRequestDetail(Integer tenantId);

    boolean getIsShowMenuAssignToMe(Integer tenantId);

    boolean getIsShowMenuEditRequestForApprover(Integer tenantId);

    boolean getIsShowApprovalReportLine(Integer tenantId);

    boolean getIsShowRemoveFromMyTaskActionMenu(Integer tenantId);

    boolean getIsAllowDeleteItemForApprover(Integer tenantId);

    String getHistoryModalRequestNameLabel(Integer tenantId);

    String getHistoryModalProjectNameLabel(Integer tenantId);

    String getHistoryModalProjectNameFormat(Integer tenantId);

    String getRequestNoteLabel(Integer tenantId);

    String getReasonModalTitle(Integer tenantId);

    String getViewApproveRequestItemTableTemplate(Integer tenantId);

    String getDefaultApproverHeadersTemplate(Integer tenantId);

    String getDefaultSourcingItemApproverHeaders(Integer tenantId);

    String getDefaultPhoneFieldName(Integer tenantId);

    SourcingRequestDisplayDto getDisplayConfiguration(Tenant tenant);

    SourcingRequestLogicDto getLogicConfiguration(Tenant tenant);

    List<TenantConfigDto> getOurService(Integer tenantId, String timeZone);

    Integer getMaximumUploadItem(Integer  tenantId);

    boolean isAutoAssignToPurchaser(Integer tenantId);

    boolean isEnableWorkflowEngine(Integer tenantId);

    Integer getExportReportYear(Integer tenantId);

    boolean getExportReportOffsetCurrentYear(Integer tenantId);

    String getInvalidErrorMessage(Integer tenantId);

    String getExportReportTitleSelectDate(Integer tenantId);

    boolean getExportReportLockDateRange(Integer tenantId);

    boolean isEnableDeptApprover(Integer tenantId);

    boolean getIsShowApprovalApproverGroup(Integer tenantId);

    boolean getIsSearchByInvitationCode(Integer tenantId);

    String getApprovalSectionTemplate(Integer tenantId);

    String[] getCheckWaringChangeFields(Integer tenantId);

    String getDeptApproverSectionLabel(Integer tenantId);

    String getPurchaserSectionLabel(Integer tenantId);

    String getDueDiligenceCheckListForm(Integer tenantId);

    boolean getUserLoginRedundancyCheck(Integer tenantId);

    boolean IsShowItemNameForFreeItem(Integer tenantId);

    TenantConfigDto createTenantConfig(TenantConfigRequest request);

    TenantConfigSearchDto searchTenantConfigByCondition(TenantConfigSearchRequest request, Pageable pageable, String timeZone);

    TenantConfigDto getByTenantConfigIdAndTenant(Integer tenantConfigId, String timeZone);

    TenantConfigDto updateTenantConfig(TenantConfigRequest request);

    boolean deleteTenantConfig(Integer tenantConfigId);

    TenantConfigDto updateTenantConfigSequence(SequenceRequest request);

    boolean isSendOrganizationToERFX(Integer tenantId);

    boolean isDisabledTypeForPurchaserEdit(Integer tenantId);

    String getRequestItemReportLabel(Integer tenantId);

    boolean isShowSupplierLocalLanguage(Integer tenantId);

    String getReportProjectFormat(Integer tenantId);

    String getSupplierFieldName(Integer tenantId);

    boolean getVisibleSourcingDocNo(Integer tenantId);

    boolean getForceSelectAllItemCopyToPR(Integer tenantId);

    boolean getAllowRepeateCopyToPR(Integer tenantId);

    boolean getCopyToPRViaERP(Integer tenantId);

}
