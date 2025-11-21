package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.client.ErfxClient;
import com.pantavanij.sourcingreq.services.config.ERFXConfig;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ApproverRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;
import com.pantavanij.sourcingreq.services.domain.dto.deptapprover.DeptApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.eform.*;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.requester.RequesterRequestSearchDto;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.ReviewerRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.sourcingapprover.SourcingApproverRequestDto;
import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceApproverDto;
import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceData;
import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceStageDto;
import com.pantavanij.sourcingreq.services.domain.dto.workflow.WorkflowInstanceStepDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Currency;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.domain.response.eform.EFormViewResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.exception.AppException;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.schedule.ScheduleTask;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.DelegationStatusEnum.DELEGATION_ACTIVE;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_ERFX;
import static com.pantavanij.sourcingreq.services.enums.SourcingType.SOURCING_TYPE_EXCEPTIONAL_SOURCING;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.*;
import static com.pantavanij.sourcingreq.services.enums.ThirdPartyRole.THIRD_PARTY_ROLE_DATA_CONTROLLER;
import static com.pantavanij.sourcingreq.services.enums.ThirdPartyRole.THIRD_PARTY_ROLE_DATA_PROCESSOR;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;
import static com.pantavanij.sourcingreq.services.util.Constant.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@RequiredArgsConstructor
@Service
@Slf4j
public class RequestServiceImpl implements RequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    private final ERFXConfig erfxConfig;
    private final ErfxClient erfxClient;
    private final DelegateClient delegateClient;
    private final RequestRepository requestRepository;
    private final TenantRepository tenantRepository;
    private final RequestAttachmentRepository requestAttachmentRepository;
    private final TenantRequestStatusRepository tenantRequestStatusRepository;
    private final TenantApprovalStatusRepository tenantApprovalStatusRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final CurrencyRepository currencyRepository;
    private final RequestReviewerRepository requestReviewerRepository;
    private final SourcingStatusRepository sourcingStatusRepository;
    private final RequestForwarderRepository requestForwarderRepository;
    private final RequestItemRepository requestItemRepository;
    private final RequestReviewerService requestReviewerService;
    private final RequestForwarderService requestForwarderService;
    private final RequestItemService requestItemService;
    private final UaaService uaaService;
    private final WorkflowService workflowService;
    private final WorkFlowInstanceGenerator workFlowInstanceGenerator;
    private final WorkflowInstanceApprovalService workflowInstanceApprovalService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final RequestHistoryService requestHistoryService;
    private final EmailService emailService;
    private final EPAuthService epAuthService;
    private final ExistingPriceItemService existingPriceItemService;
    private final RequestStatusService requestStatusService;
    private final RequestObjectiveService requestObjectiveService;
    private final RequestCategoryService requestCategoryService;
    private final RequestSubCategoryService requestSubCategoryService;
    private final RequestProjectService requestProjectService;
    private final RequestTypeService requestTypeService;
    private final RequestDepartmentService requestDepartmentService;
    private final RequestBudgetRefNoService requestBudgetRefNoService;
    private final RequestLocationService requestLocationService;
    private final RequestObjectiveRepository requestObjectiveRepository;
    private final RequestBudgetRefNoRepository requestBudgetRefNoRepository;
    private final RequestCategoryRepository requestCategoryRepository;
    private final RequestSubCategoryRepository requestSubCategoryRepository;
    private final RequestProjectRepository requestProjectRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestDepartmentRepository requestDepartmentRepository;
    private final RequestLocationRepository requestLocationRepository;
    private final RequestItemPurposeRepository requestItemPurposeRepository;
    private final RequestItemSubCategoryRepository requestItemSubCategoryRepository;
    private final RequestItemCurrencyRepository requestItemCurrencyRepository;
    private final RequestItemLocationRepository requestItemLocationRepository;
    private final LocationRepository locationRepository;
    private final RequestAdditionalService requestAdditionalService;
    private final RequestReportLineService requestReportLineService;
    private final RequestAdditionalRepository requestAdditionalRepository;
    private final TenantSubCategoryRepository tenantSubCategoryRepository;
    private final TenantSectionService tenantSectionService;
    private final RequestQuestionnaireRepository requestQuestionnaireRepository;
    private final EFormService eFormService;
    private final RequestQuestionnaireFormFieldRepository requestQuestionnaireFormFieldRepository;
    private final RequestQuestionnaireFormFieldOptionChoiceRepository requestQuestionnaireFormFieldOptionChoiceRepository;
    private final TenantSubCategoryService tenantSubCategoryService;
    private final RequestAttachmentService requestAttachmentService;
    private final TenantRequestStatusService tenantRequestStatusService;
    private final DelegationService delegationService;
    private final RequestReportLineRepository requestReportLineRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;
    private final RequestApproverRepository requestApproverRepository;
    private final RequestPurchaserService requestPurchaserService;
    private final RequestPurchaserRepository requestPurchaserRepository;
    private final PurchaserRepository purchaserRepository;
    private final RequestDeptApproverService requestDeptApproverService;
    private final ApproverRepository approverRepository;
    private final ExistingPriceItemRepository existingPriceItemRepository;
    private final SupplierNotificationService supplierNotificationService;
    private final ReportLineRepository reportLineRepository;
    private final ReviewerRepository reviewerRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final SourcingReferenceService sourcingReferenceService;
    private final ExcSourcingApproverRepository excSourcingApproverRepository;
    private final ExcSourcingPurchaserRepository excSourcingPurchaserRepository;

    @Override
    public RequestDto findRequestSourcingForEditingByRecId(Long recId) {
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(recId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, requestApprovers, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_EDIT)) {
            return prepareRequestSourcingByRecId(recId);
        }
        return null;
    }

    public RequestDto prepareRequestSourcingByRecId (Long recId) {
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);

        Request request = requestRepository.findRequestsByRecId(recId);
        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request, timeZone);

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            isDelegationActive = true;
            requestDto.setEnableForwardButton(false);
        }

        boolean allowUpdateRequest = (AppUtil.getPrivilegeScopes().get("SQN") != null ||
                AppUtil.getPrivilegeScopes().get("SQP") != null) &&
                !isDelegationActive; // If delegation is active, So this Purchaser can't edit this request.

        boolean allowCopyToPR = AppUtil.getPrivilegeScopes().get("SQR") != null;

        List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
        InstanceApproverHeaderDto InstanceApproverHeaderDto = this.getDeptApproverHeaders(request);
        approverHeaders.add(InstanceApproverHeaderDto);
        InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
        approverHeaders.add(InstancePurchaserHeaderDto);
        requestDto.setApproverHeaders(approverHeaders);

        RequestStatusDto requestStatusByPrivilegeCode = updateRequestStatusByPrivilegeCode(requestDto.getRequestStatus(), allowUpdateRequest, allowCopyToPR);

        if(requestStatusByPrivilegeCode != null) {
            requestDto.setRequestStatus(requestStatusByPrivilegeCode);
        }

        ApprovalStatusDto approvalStatusByPrivilegeCode = updateApprovalStatusByPrivilegeCode(requestDto.getApprovalStatus(), allowUpdateRequest, allowCopyToPR);

        if(approvalStatusByPrivilegeCode != null) {
            requestDto.setApprovalStatus(approvalStatusByPrivilegeCode);
        }

        requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
        requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
        requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
        if (requestDto.getRequestAttachmentList() != null) {
            requestDto.getRequestAttachmentList().stream()
                    .peek(requestAttachmentDto -> {
                        if (requestAttachmentDto.getAttachment().getFileURL() != null) {
                            String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(), requestAttachmentDto.getAttachment().getFileURL());
                            try {
                                if (requestAttachmentDto.getAttachment().getFileSize() == null) {
                                    requestAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            requestAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                        }
                        requestAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestAttachmentDto.getAttachment().getCreatedBy()));
                    }).collect(Collectors.toList());
        }

        Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
        requestAdditionalOptional.ifPresent(additional -> {
            requestDto.setBackground(additional.getBackground());
            requestDto.setEvaluationDate(additional.getEvaluationDate());
            requestDto.setDepartment(additional.getDepartment());
            requestDto.setApproveNo(additional.getApproveNo());
            requestDto.setCostcenter(additional.getCostcenter());
            requestDto.setRequester(requestDto.getCreatedByName());
            requestDto.setMakingContractReason(additional.getMakingContractReason());
            requestDto.setMakingRptContractReason(additional.getMakingRptContractReason());

            requestDto.setPerformanceEvaluation(getOptionByName(YES_NO_OPTION, additional.getPerformanceEvaluation()));
            requestDto.setPdpaQ01(getOptionByName(PDPA_QUESTION, additional.getPdpaQ01()));
            requestDto.setPdpaQ02(getOptionByName(PDPA_QUESTION, additional.getPdpaQ02()));
            requestDto.setPdpaQ03(getOptionByName(PDPA_QUESTION, additional.getPdpaQ03()));
            requestDto.setPdpaQ04(getOptionByName(PDPA_QUESTION, additional.getPdpaQ04()));
            requestDto.setPdpaQ05(getOptionByName(PDPA_QUESTION, additional.getPdpaQ05()));
            requestDto.setPdpaQ06(getOptionByName(PDPA_QUESTION, additional.getPdpaQ06()));
            requestDto.setRelatePdpa(additional.getRelatePdpa());
            requestDto.setThirdPartyRole(additional.getThirdPartyRole());
            requestDto.setDpaType(additional.getDpaType());
            requestDto.setOutsourceService(getOptionByName(YES_NO_OPTION, additional.getOutsourceService()));
            requestDto.setMakingContract(getOptionByName(YES_NO_OPTION, additional.getMakingContract()));
            requestDto.setMakingRptContract(getOptionByName(YES_NO_OPTION, additional.getMakingRptContract()));
            requestDto.setNeedWhtCert(getOptionByName(YES_NO_OPTION, additional.getNeedWhtCert()));

            requestDto.setWhtAbsorbedBy(getOptionByName(WHT_ABSORBED_BY, additional.getWhtAbsorbedBy()));
            requestDto.setVatAbsorbedBy(getOptionByName(VAT_ABSORBED_BY, additional.getVatAbsorbedBy()));
            requestDto.setStampDuty(getOptionByName(STAMP_DUTY, additional.getStampDuty()));
        });

        boolean isOwnerPurchaser = false;
        if (request.getDelegateActionBy() != null) {
            isOwnerPurchaser = AppUtil.isPurchaser() && request.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
        } else {
            if(isDelegationActive) {
                isOwnerPurchaser = AppUtil.isPurchaser() && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code());
            } else {
                isOwnerPurchaser = AppUtil.isPurchaser() &&
                        ((request.getAssignedBy() != null && request.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
            }
        }
        requestDto.setIsOwnerPurchaser(isOwnerPurchaser);


        return this.setRequestDetailsFromRepositories(requestDto, recId, tenantId);
    }

    @Override
    public RequesterRequestDto findRequestSourcingByRecId(Long recId) {
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(recId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
            isDelegationActive = true;
        }

        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        String idp = AppUtil.getIdp();
        RequestAdditional requestAdditional = null;

        if (null != request) {
            Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
            if (requestAdditionalOptional.isPresent()) {
                requestAdditional = requestAdditionalOptional.get();
            }
        }

        boolean allowUpdateRequest = (AppUtil.getPrivilegeScopes().get("SQN") != null ||
                AppUtil.getPrivilegeScopes().get("SQP") != null) &&
                !isDelegationActive; // If delegation is active, So this Purchaser can't edit this request.

        boolean allowCopyToPR = AppUtil.getPrivilegeScopes().get("SQR") != null;

        if (request != null && AppUtil.isAllowedAction(user, request, RequestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            String copyToPrPrivilegeCodesConfig = tenantConfigService.getCopyToPrPrivilegeCodes(tenant.getRecId());
            String copyToPrOrganizationsConfig = tenantConfigService.getCopyToPrOrganizations(tenant.getRecId());
            Boolean forceSelectAllItemCopyToPR = tenantConfigService.getForceSelectAllItemCopyToPR(tenant.getRecId());
            List<String> copyToPrPrivilegeCodes =
                    StringUtils.isNotBlank(copyToPrPrivilegeCodesConfig)
                            ? Arrays.stream(copyToPrPrivilegeCodesConfig.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList())
                            : Collections.emptyList();

            List<String> copyToPrOrganizations =
                    StringUtils.isNotBlank(copyToPrOrganizationsConfig)
                            ? Arrays.stream(copyToPrOrganizationsConfig.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList())
                            : Collections.emptyList();
            RequesterRequestDto requestDto = RequestMapper.INSTANCE.toRequesterRequestDto(request, timeZone, copyToPrPrivilegeCodes, forceSelectAllItemCopyToPR, copyToPrOrganizations);
            RequestDto requestDto_ = new RequestDto();
            requestDto_.setEnableForwardButton(!isDelegationActive);
            requestDto.setRequestDto(requestDto_);

            boolean allowCancel = requestDto.getRequestDeptApproverDtoList() != null && !requestDto.getRequestDeptApproverDtoList().stream()
                    .anyMatch(i -> i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code())
                            || i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()));

            List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
            InstanceApproverHeaderDto InstanceApproverHeaderDto = this.getDeptApproverHeaders(request);
            approverHeaders.add(InstanceApproverHeaderDto);
            InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
            approverHeaders.add(InstancePurchaserHeaderDto);
            requestDto.setApproverHeaders(approverHeaders);

            RequestStatusDto requestStatusByPrivilegeCode = updateRequestStatusByPrivilegeCode(
                    requestDto.getRequestStatus(),
                    allowUpdateRequest,
                    requestDto.getRequestStatus().isCanCopyToPR(),
                    allowCancel);
            requestDto.setRequestStatus(requestStatusByPrivilegeCode);

            ApprovalStatusDto approvalStatusByPrivilegeCode = updateApprovalStatusByPrivilegeCode(
                    requestDto.getApprovalStatus(),
                    allowUpdateRequest,
                    allowCopyToPR);

            if(approvalStatusByPrivilegeCode != null) {
                requestDto.setApprovalStatus(approvalStatusByPrivilegeCode);
            }

            Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
            boolean hasApprovalService = false;
            if(approver.isPresent()) {
                Optional<RequestApprover> requestApprover = requestApproverRepository.findRequestApproversByRequestAndApprover(request, approver.get());
                if (requestApprover.isPresent()) {
                    RequestApprover requestApproverObj = requestApprover.get();
                    hasApprovalService = requestDeptApproverService.hasApprovalPermission(requestApproverObj);
                }
            }
            requestDto.getDeptApprovalStatus().setCanApprove(hasApprovalService);
            requestDto.getDeptApprovalStatus().setCanReject(hasApprovalService);

            requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
            requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
            requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
            if (requestDto.getRequestAttachmentList() != null) {
                requestDto.getRequestAttachmentList().stream()
                        .peek(requestAttachmentDto -> {
                            if (requestAttachmentDto.getAttachment().getFileURL() != null) {
                                String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(), requestAttachmentDto.getAttachment().getFileURL());
                                try {
                                    if (requestAttachmentDto.getAttachment().getFileSize() == null) {
                                        requestAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                requestAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                            }
                            requestAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestAttachmentDto.getAttachment().getCreatedBy()));
                        }).collect(Collectors.toList());
            }

            // Add additional data //
            if (null != requestAdditional) {
                requestDto.setBackground(requestAdditional.getBackground());
                requestDto.setEvaluationDate(requestAdditional.getEvaluationDate());
                requestDto.setDepartment(requestAdditional.getDepartment());
                requestDto.setApproveNo(requestAdditional.getApproveNo());
                requestDto.setCostcenter(requestAdditional.getCostcenter());
                requestDto.setRequester(requestDto.getCreatedByName());
                requestDto.setMakingContractReason(requestAdditional.getMakingContractReason());
                requestDto.setMakingRptContractReason(requestAdditional.getMakingRptContractReason());

                requestDto.setPerformanceEvaluation(getOptionByName(YES_NO_OPTION, requestAdditional.getPerformanceEvaluation()));
                requestDto.setPdpaQ01(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ01()));
                requestDto.setPdpaQ02(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ02()));
                requestDto.setPdpaQ03(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ03()));
                requestDto.setPdpaQ04(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ04()));
                requestDto.setPdpaQ05(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ05()));
                requestDto.setPdpaQ06(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ06()));
                requestDto.setRelatePdpa(requestAdditional.getRelatePdpa());
                requestDto.setThirdPartyRole(requestAdditional.getThirdPartyRole());
                requestDto.setDpaType(requestAdditional.getDpaType());
                requestDto.setOutsourceService(getOptionByName(YES_NO_OPTION, requestAdditional.getOutsourceService()));
                requestDto.setMakingContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingContract()));
                requestDto.setMakingRptContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingRptContract()));
                requestDto.setNeedWhtCert(getOptionByName(YES_NO_OPTION, requestAdditional.getNeedWhtCert()));

                requestDto.setWhtAbsorbedBy(getOptionByName(WHT_ABSORBED_BY, requestAdditional.getWhtAbsorbedBy()));
                requestDto.setVatAbsorbedBy(getOptionByName(VAT_ABSORBED_BY, requestAdditional.getVatAbsorbedBy()));
                requestDto.setStampDuty(getOptionByName(STAMP_DUTY, requestAdditional.getStampDuty()));
            }

            String username = AppUtil.getUserName();
            OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
            List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

            OptionDto organizationOption = organizationList.stream()
                    .filter(organization -> requestDto.getOrganizationId() != null &&
                            organization.getValue().equals(requestDto.getOrganizationId().toString()))
                    .findFirst()
                    .orElse(null);
            requestDto.setOrganizationObj(organizationOption);

            return this.setRequesterRequestDetailsFromRepositories(requestDto, recId, tenantId, true);
        }
        return null;
    }

    private OptionDto getOptionByName(String name, String value) {
        List<OptionDto> result = tenantSectionService.getOptionList(name);
        return result.stream().filter(it -> (it != null && value != null && !value.isEmpty()) && it.getValue().equals(value.trim())).findFirst().orElse(null);
    }

    @Override
    public DefaultApproverDto getDefaultApprovers() {
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        DefaultApproverDto defaultApproverDto = new DefaultApproverDto();
        defaultApproverDto.setApproverHeaders(this.getDefaultApproverHeaders(tenantId, idp));

        return defaultApproverDto;
    }

    @Override
    public ApproverRequestDto findRequestApprovalByRecId(Long recId) {
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(recId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
            isDelegationActive = true;
        }

        boolean allowUpdateRequest = (AppUtil.getPrivilegeScopes().get("SQN") != null ||
                AppUtil.getPrivilegeScopes().get("SQP") != null) &&
                !isDelegationActive; // If delegation is active, So this Purchaser can't edit this request.
        boolean allowCopyToPR = AppUtil.getPrivilegeScopes().get("SQR") != null;


        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        String idp = AppUtil.getIdp();

        if (request != null && AppUtil.isAllowedAction(user, request, RequestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            ApproverRequestDto requestDto = RequestMapper.INSTANCE.toApproverRequestDto(request, timeZone);

            List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
            InstanceApproverHeaderDto InstanceApproverHeaderDto = this.getDeptApproverHeaders(request);
            approverHeaders.add(InstanceApproverHeaderDto);
            InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
            approverHeaders.add(InstancePurchaserHeaderDto);
            requestDto.setApproverHeaders(approverHeaders);
            requestDto.setEnableForwardButton(!isDelegationActive);

            boolean allowCancel = requestDto.getRequestDeptApproverDtoList() != null && !requestDto.getRequestDeptApproverDtoList().stream()
                    .anyMatch(i -> i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code())
                            || i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()));

            RequestStatusDto requestStatusByPrivilegeCode = updateRequestStatusByPrivilegeCode(
                    requestDto.getRequestStatus(),
                    allowUpdateRequest,
                    allowCopyToPR,
                    allowCancel);
            requestDto.setRequestStatus(requestStatusByPrivilegeCode);

            ApprovalStatusDto approvalStatusByPrivilegeCode = updateApprovalStatusByPrivilegeCode(
                    requestDto.getApprovalStatus(),
                    allowUpdateRequest,
                    allowCopyToPR);

            if(approvalStatusByPrivilegeCode != null) {
                requestDto.setApprovalStatus(approvalStatusByPrivilegeCode);
            }

            Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
            boolean hasApprovalService = false;
            if(approver.isPresent()) {
                Optional<RequestApprover> requestApprover = requestApproverRepository.findRequestApproversByRequestAndApprover(request, approver.get());
                if (requestApprover.isPresent()) {
                    RequestApprover requestApproverObj = requestApprover.get();
                    hasApprovalService = requestDeptApproverService.hasApprovalPermission(requestApproverObj);
                }
            }
            requestDto.getDeptApprovalStatus().setCanApprove(hasApprovalService);
            requestDto.getDeptApprovalStatus().setCanReject(hasApprovalService);

            String assignBy = StringUtils.isNotBlank(request.getAssignedBy()) ? request.getAssignedBy() : "";

            long awaitingSourcing = request.getRequestItemList().stream().filter(
                    r -> r.getSourcingStatus().getRecId().equals(SOURCING_NONE.id()) ||
                            r.getSourcingStatus().getRecId().equals(SOURCING_DELETED.id())).count();

            if (awaitingSourcing != request.getRequestItemList().size()) {
                requestDto.getApprovalStatus().setCanForwardApprovalWorkflow(false);
            } else {
                boolean canForwardApprovalWorkflow = requestForwarderService.isCurrentForwarder(request.getRecId(), assignBy);
                requestDto.getApprovalStatus().setCanForwardApprovalWorkflow(canForwardApprovalWorkflow);
                log.info("Request : {} can forward approval workflow : {}", request.getRequestNo(), canForwardApprovalWorkflow);
            }

            requestDto.getApprovalStatus().setCanAssignToMe(StringUtils.isEmpty(requestDto.getAssignedBy()) &&
                    requestDto.getApprovalStatus().getName().equals(TENANT_REQUEST_AWAITING.code()));
            requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
            requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
            requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
            requestDto.setRequester(requestDto.getCreatedByName());

            if (requestDto.getRequestAttachmentList() != null) {
                requestDto.getRequestAttachmentList().stream()
                        .peek(requestAttachmentDto -> {
                            if (requestAttachmentDto.getAttachment().getFileURL() != null) {
                                String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(), requestAttachmentDto.getAttachment().getFileURL());
                                try {
                                    if (requestAttachmentDto.getAttachment().getFileSize() == null) {
                                        requestAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                requestAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                            }
                            requestAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestAttachmentDto.getAttachment().getCreatedBy()));
                        }).collect(Collectors.toList());
            }

            RequestAdditional requestAdditional = null;
            Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
            if (requestAdditionalOptional.isPresent()) {
                requestAdditional = requestAdditionalOptional.get();
            }

            // Add additional data //
            if (requestAdditional != null) {
                requestDto.setBackground(requestAdditional.getBackground());
                requestDto.setEvaluationDate(requestAdditional.getEvaluationDate());
                requestDto.setDepartment(requestAdditional.getDepartment());
                requestDto.setApproveNo(requestAdditional.getApproveNo());
                requestDto.setCostcenter(requestAdditional.getCostcenter());
                requestDto.setRequester(requestDto.getCreatedByName());
                requestDto.setMakingContractReason(requestAdditional.getMakingContractReason());
                requestDto.setMakingRptContractReason(requestAdditional.getMakingRptContractReason());

                requestDto.setPerformanceEvaluation(getOptionByName(YES_NO_OPTION, requestAdditional.getPerformanceEvaluation()));
                requestDto.setPdpaQ01(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ01()));
                requestDto.setPdpaQ02(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ02()));
                requestDto.setPdpaQ03(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ03()));
                requestDto.setPdpaQ04(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ04()));
                requestDto.setPdpaQ05(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ05()));
                requestDto.setPdpaQ06(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ06()));
                requestDto.setRelatePdpa(requestAdditional.getRelatePdpa());
                requestDto.setThirdPartyRole(requestAdditional.getThirdPartyRole());
                requestDto.setDpaType(requestAdditional.getDpaType());
                requestDto.setOutsourceService(getOptionByName(YES_NO_OPTION, requestAdditional.getOutsourceService()));
                requestDto.setMakingContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingContract()));
                requestDto.setMakingRptContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingRptContract()));
                requestDto.setNeedWhtCert(getOptionByName(YES_NO_OPTION, requestAdditional.getNeedWhtCert()));

                requestDto.setWhtAbsorbedBy(getOptionByName(WHT_ABSORBED_BY, requestAdditional.getWhtAbsorbedBy()));
                requestDto.setVatAbsorbedBy(getOptionByName(VAT_ABSORBED_BY, requestAdditional.getVatAbsorbedBy()));
                requestDto.setStampDuty(getOptionByName(STAMP_DUTY, requestAdditional.getStampDuty()));
            }

            String username = AppUtil.getUserName();
            OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
            List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

            OptionDto organizationOption = organizationList.stream()
                    .filter(organization -> requestDto.getOrganizationId() != null &&
                            organization.getValue().equals(requestDto.getOrganizationId().toString()))
                    .findFirst()
                    .orElse(null);
            requestDto.setOrganizationObj(organizationOption);

            boolean isOwnerPurchaser = false;
            if (request.getDelegateActionBy() != null) {
                isOwnerPurchaser = AppUtil.isPurchaser() && request.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
            } else {
                if(isDelegationActive) {
                    isOwnerPurchaser = AppUtil.isPurchaser() && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code());
                } else {
                    isOwnerPurchaser = AppUtil.isPurchaser() &&
                            ((request.getAssignedBy() != null && request.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
                }
            }
            requestDto.setIsOwnerPurchaser(isOwnerPurchaser);

            return this.setApproverRequestDetailsFromRepositories(requestDto, recId, true);
        }
        return null;
    }

    @Override
    @Transactional
    public ReviewerRequestDto findRequestReviewerByRecId(Long recId) {
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestsByRecId(recId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
            isDelegationActive = true;
        }

        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        String idp = AppUtil.getIdp();

        List<ExcSourcingApprover> excSourcingApprovers = excSourcingApproverRepository
                .findExcSourcingApproversByRequestId(request.getRecId());
        List<ExcSourcingPurchaser> excSourcingPurchasers = excSourcingPurchaserRepository
                .findExcSourcingPurchasersByRequestId(request.getRecId());

        boolean hasExcSourcingPermission = false;

        if (excSourcingApprovers != null) {
            hasExcSourcingPermission = excSourcingApprovers.stream()
                    .anyMatch(a -> a.getApprover().getLoginId().equalsIgnoreCase(AppUtil.getUserName()));
        }

        if (!hasExcSourcingPermission && excSourcingPurchasers != null) {
            hasExcSourcingPermission = excSourcingPurchasers.stream()
                    .anyMatch(p -> p.getApprover().getLoginId().equalsIgnoreCase(AppUtil.getUserName()));
        }

        if (request != null && (
                hasExcSourcingPermission ||
                        AppUtil.isAllowedAction(user, request, RequestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_UNKNOWN)
        )) {

            // Stamp [Read] flag for notification count
            if (AppUtil.isReviewer()) {
                Optional<RequestReviewer> requestReviewer = requestReviewers.stream().filter(r -> r.getReviewer().getLoginId().equalsIgnoreCase(AppUtil.getUserName())).findFirst();

                if (requestReviewer.isPresent()) {
                    RequestReviewer currentRequestReviewer = requestReviewer.get();
                    currentRequestReviewer.setRead(true);
                    requestReviewerRepository.save(currentRequestReviewer);
                }
            }

            // Stamp [Read] flag to true when user read this request.
            if (AppUtil.isReportLine()) {
                Optional<RequestReportLine> requestReportLine = reportLines.stream().filter(r -> (r != null &&
                        r.getReportLine().getLoginId() != null) &&
                        r.getReportLine().getLoginId().equalsIgnoreCase(AppUtil.getUserName())).findFirst();

                if (requestReportLine.isPresent()) {
                    RequestReportLine currentRequestReportLine = requestReportLine.get();
                    currentRequestReportLine.setRead(true);
                    requestReportLineRepository.save(currentRequestReportLine);
                }
            }

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            ReviewerRequestDto requestDto = RequestMapper.INSTANCE.toReviewerRequestDto(request, timeZone);

            List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
            InstanceApproverHeaderDto InstanceApproverHeaderDto = this.getDeptApproverHeaders(request);
            approverHeaders.add(InstanceApproverHeaderDto);
            InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
            approverHeaders.add(InstancePurchaserHeaderDto);

            requestDto.setApproverHeaders(approverHeaders);
            requestDto.getRequestStatus().setCanEdit(false);
            requestDto.getRequestStatus().setCanDelete(false);
            requestDto.getRequestStatus().setCanDuplicate(false);
            requestDto.getRequestStatus().setCanCancel(false);
            requestDto.getRequestStatus().setCanCopyToPR(false);
            requestDto.getRequestStatus().setCanAssignToMe(false);
            if (requestDto.getRequestAttachmentList() != null) {
                requestDto.getRequestAttachmentList().stream()
                        .peek(requestAttachmentDto -> {
                            if (requestAttachmentDto.getAttachment().getFileURL() != null) {
                                String fullyFileURL = String.format("%s/%s", erfxConfig.getHostName(), requestAttachmentDto.getAttachment().getFileURL());
                                try {
                                    if (requestAttachmentDto.getAttachment().getFileSize() == null) {
                                        requestAttachmentDto.getAttachment().setFileSize(FileUtil.getFileSize(new URL(fullyFileURL)));
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                requestAttachmentDto.getAttachment().setFileURL(fullyFileURL);
                            }
                            requestAttachmentDto.getAttachment().setCreatedByName(UserDetailServiceUtil.getFullName(requestAttachmentDto.getAttachment().getCreatedBy()));
                        }).collect(Collectors.toList());
            }

            Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
            boolean hasApprovalService = false;
            if(approver.isPresent()) {
                Optional<RequestApprover> requestApprover = requestApproverRepository.findRequestApproversByRequestAndApprover(request, approver.get());
                if (requestApprover.isPresent()) {
                    RequestApprover requestApproverObj = requestApprover.get();
                    hasApprovalService = requestDeptApproverService.hasApprovalPermission(requestApproverObj);
                }
            }
            requestDto.getDeptApprovalStatus().setCanApprove(hasApprovalService);
            requestDto.getDeptApprovalStatus().setCanReject(hasApprovalService);

            RequestAdditional requestAdditional = null;
            Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
            if (requestAdditionalOptional.isPresent()) {
                requestAdditional = requestAdditionalOptional.get();
            }

            requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
            requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
            requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
            requestDto.setRequester(requestDto.getCreatedByName());

            // Add additional data //
            if (requestAdditional != null) {
                requestDto.setBackground(requestAdditional.getBackground());
                requestDto.setEvaluationDate(requestAdditional.getEvaluationDate());
                requestDto.setDepartment(requestAdditional.getDepartment());
                requestDto.setApproveNo(requestAdditional.getApproveNo());
                requestDto.setCostcenter(requestAdditional.getCostcenter());
                requestDto.setRequester(requestDto.getCreatedByName());
                requestDto.setMakingContractReason(requestAdditional.getMakingContractReason());
                requestDto.setMakingRptContractReason(requestAdditional.getMakingRptContractReason());

                requestDto.setPerformanceEvaluation(getOptionByName(YES_NO_OPTION, requestAdditional.getPerformanceEvaluation()));
                requestDto.setPdpaQ01(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ01()));
                requestDto.setPdpaQ02(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ02()));
                requestDto.setPdpaQ03(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ03()));
                requestDto.setPdpaQ04(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ04()));
                requestDto.setPdpaQ05(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ05()));
                requestDto.setPdpaQ06(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ06()));
                requestDto.setRelatePdpa(requestAdditional.getRelatePdpa());
                requestDto.setThirdPartyRole(requestAdditional.getThirdPartyRole());
                requestDto.setDpaType(requestAdditional.getDpaType());
                requestDto.setOutsourceService(getOptionByName(YES_NO_OPTION, requestAdditional.getOutsourceService()));
                requestDto.setMakingContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingContract()));
                requestDto.setMakingRptContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingRptContract()));
                requestDto.setNeedWhtCert(getOptionByName(YES_NO_OPTION, requestAdditional.getNeedWhtCert()));

                requestDto.setWhtAbsorbedBy(getOptionByName(WHT_ABSORBED_BY, requestAdditional.getWhtAbsorbedBy()));
                requestDto.setVatAbsorbedBy(getOptionByName(VAT_ABSORBED_BY, requestAdditional.getVatAbsorbedBy()));
                requestDto.setStampDuty(getOptionByName(STAMP_DUTY, requestAdditional.getStampDuty()));
            }

            String username = AppUtil.getUserName();
            OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
            List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

            OptionDto organizationOption = organizationList.stream()
                    .filter(organization -> requestDto.getOrganizationId() != null &&
                            organization.getValue().equals(requestDto.getOrganizationId().toString()))
                    .findFirst()
                    .orElse(null);
            requestDto.setOrganizationObj(organizationOption);

            boolean isOwnerPurchaser = false;
            if (request.getDelegateActionBy() != null) {
                isOwnerPurchaser = AppUtil.isPurchaser() && request.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
            } else {
                if(isDelegationActive) {
                    isOwnerPurchaser = AppUtil.isPurchaser() && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code());
                } else {
                    isOwnerPurchaser = AppUtil.isPurchaser() &&
                            ((request.getAssignedBy() != null && request.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
                }
            }
            requestDto.setIsOwnerPurchaser(isOwnerPurchaser);

            return this.setReviewerRequestDetailsFromRepositories(requestDto, recId, true);
        }
        return null;
    }

    @Override
    public RequestDto findByRecIdForDuplicate(Long requestId) {
        UserDto user = AppUtil.getUser();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        String idp = AppUtil.getIdp();

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_DUPLICATE)) {
            TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(TENANT_REQUEST_DRAFT.code(), request.getTenant());
            RequestStatusDto requestStatusDto = RequestStatusMapper.INSTANCE.toRequestStatusDto(tenantRequestStatus, timeZone);
            TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_DRAFT.code(), request.getTenant());
            ApprovalStatusDto approvalStatusDto = ApprovalStatusMapper.INSTANCE.toApprovalStatusDto(tenantApprovalStatus, timeZone);
            SourcingStatus sourcingStatus = sourcingStatusRepository.findSourcingStatusByRecId(SOURCING_NONE.id());
            SourcingStatusDto sourcingStatusDto = SourcingStatusMapper.INSTANCE.toSourcingStatusDto(sourcingStatus, timeZone);

            List<RequestItemV2Dto> requestItemList = new ArrayList<>();

            RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request, timeZone);

            List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();

            if(tenantConfigService.isEnableDeptApprover(tenant.getRecId())) {
                InstanceApproverHeaderDto InstanceApproverHeaderDto = new InstanceApproverHeaderDto();
                approverHeaders.add(InstanceApproverHeaderDto);
            }
            requestDto.setApproverHeaders(approverHeaders);

            requestDto.setRecId(0L);
            requestDto.setRequestNo(null);
            requestDto.setRequestStatus(requestStatusDto);
            requestDto.setApprovalStatus(approvalStatusDto);
            requestDto.setAssignedBy(null);
            requestDto.setWorkflowInstanceId(0L);
            setRequestDetailsFromRepositories(requestDto, requestId, request.getTenant().getCode());


            InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
            if(requestCategoryRepository.findByRequest(request).isEmpty() &&
                    requestSubCategoryRepository.findByRequest(request).isEmpty()) {
                InstancePurchaserHeaderDto.getApproverSections().get(0).setNumberOfApproverRequired(0);
                InstancePurchaserHeaderDto.getApproverSections().get(0).setApprovers(new ArrayList<>());
                requestDto.setPurchaserObj(null);
            }
            approverHeaders.add(InstancePurchaserHeaderDto);

            // Validate expected date, and set value to be null in case the expected date is before tomorrow //
            if (requestDto.getExpectedDate() != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (requestDto.getExpectedDate().before(c.getTime())) {
                    requestDto.setExpectedDate(null);
                }
            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setRecId(0L);
                requestItemDto.setSourcingDocNo(null);
                requestItemDto.setSourcingDocId(null);
                requestItemDto.setSourcingTypeId(1);
                requestItemDto.setSourcingStatus(sourcingStatusDto);
                requestItemDto.setCreatedBy(AppUtil.getUserName());
                requestItemDto.setCreatedDate(DateTimeUtil.getTimestampUTC());
                requestItemDto.setUpdatedBy(AppUtil.getUserName());
                requestItemDto.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestItemDto.setRequestItemAttachmentList(new ArrayList<>());
                requestItemList.add(requestItemDto);
            }
            requestDto.setRequestItemList(requestItemList);
            requestDto.setRequestAttachmentList(new ArrayList<>());

            RequestAdditional requestAdditional = null;
            Optional<RequestAdditional> requestAdditionalOptional = requestAdditionalRepository.findTop1ByRequestId(request.getRecId());
            if (requestAdditionalOptional.isPresent()) {
                requestAdditional = requestAdditionalOptional.get();
            }

            // Add additional data //
            if (requestAdditional != null) {
                requestDto.setBackground(requestAdditional.getBackground());
                requestDto.setEvaluationDate(requestAdditional.getEvaluationDate());
                requestDto.setDepartment(requestAdditional.getDepartment());
                requestDto.setApproveNo(requestAdditional.getApproveNo());
                requestDto.setCostcenter(requestAdditional.getCostcenter());
                requestDto.setRequester(requestDto.getCreatedByName());
                requestDto.setMakingContractReason(requestAdditional.getMakingContractReason());
                requestDto.setMakingRptContractReason(requestAdditional.getMakingRptContractReason());

                requestDto.setPerformanceEvaluation(getOptionByName(YES_NO_OPTION, requestAdditional.getPerformanceEvaluation()));
                requestDto.setPdpaQ01(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ01()));
                requestDto.setPdpaQ02(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ02()));
                requestDto.setPdpaQ03(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ03()));
                requestDto.setPdpaQ04(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ04()));
                requestDto.setPdpaQ05(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ05()));
                requestDto.setPdpaQ06(getOptionByName(PDPA_QUESTION, requestAdditional.getPdpaQ06()));
                requestDto.setRelatePdpa(requestAdditional.getRelatePdpa());
                requestDto.setThirdPartyRole(requestAdditional.getThirdPartyRole());
                requestDto.setDpaType(requestAdditional.getDpaType());
                requestDto.setOutsourceService(getOptionByName(YES_NO_OPTION, requestAdditional.getOutsourceService()));
                requestDto.setMakingContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingContract()));
                requestDto.setMakingRptContract(getOptionByName(YES_NO_OPTION, requestAdditional.getMakingRptContract()));
                requestDto.setNeedWhtCert(getOptionByName(YES_NO_OPTION, requestAdditional.getNeedWhtCert()));

                requestDto.setWhtAbsorbedBy(getOptionByName(WHT_ABSORBED_BY, requestAdditional.getWhtAbsorbedBy()));
                requestDto.setVatAbsorbedBy(getOptionByName(VAT_ABSORBED_BY, requestAdditional.getVatAbsorbedBy()));
                requestDto.setStampDuty(getOptionByName(STAMP_DUTY, requestAdditional.getStampDuty()));
            }

            //EPAuthReviewerDto defaultReportLine = reportLineService.getDefaultReportLine();
            requestDto.setReportLines(new ArrayList<>());
            requestDto.setReviewers(new ArrayList<>());

            // Save Request History : 3 DUPLICATE Duplicate to new request successfully //
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_DUPLICATE.id());
            return requestDto;
        }
        return null;
    }

    @Override
    public RequestDto cancelRequest(RequestCancellationRequest cancellationRequest) {
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestsByRecId(cancellationRequest.getRecId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        boolean isAllowCancel = !request.getRequestDeptApproverList().stream()
                .anyMatch(i -> i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code())
                        || i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()));

        if (isAllowCancel) {
            if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_CANCEL)) {
                TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(TENANT_REQUEST_CANCELLED.code(), request.getTenant());
                TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_CANCELLED.code(), request.getTenant());

                String tenantCode = AppUtil.getTenantId();
                Tenant tenant = tenantService.findByCode(tenantCode);

                if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {
                    try {
                        WorkflowInstanceApprovalRequest wfRequest = new WorkflowInstanceApprovalRequest();
                        WorkflowInstanceApprovalResponse ApprovalResponse = workflowInstanceApprovalService.cancel(request.getWorkflowInstanceId(), wfRequest, cancellationRequest.getReason());
                    } catch (Exception ex) {

                    }
                }

                request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                request.setRequestStatus(tenantRequestStatus);

                DeptApprovalStatus deptApprovalStatusCancelled = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_CANCELLED.code());
                request.setDeptApprovalStatus(deptApprovalStatusCancelled);

                request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                request.setApprovalStatus(tenantApprovalStatus);

                request.setCancellationReason(cancellationRequest.getReason());
                request.setUpdatedBy(AppUtil.getUserName());
                request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestRepository.save(request);

                // Save RequestApprovers status tobe cancelled.
                List<RequestApprover> requestApproverList = requestApproverRepository.findRequestApproversByRequest(request);
                for(RequestApprover requestApprover : requestApproverList) {
                    requestApprover.setDeptApprovalStatus(deptApprovalStatusCancelled);
                    requestApprover.setUpdatedBy(AppUtil.getUserName());
                    requestApprover.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                }
                requestApproverRepository.saveAll(requestApproverList);

                // Save Request History : 4	CANCEL	Cancel request successfully //
                requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_CANCEL.id());

                // Send email notification //
                SendEMailRequest cancelledRequestEmail = new SendEMailRequest();
                cancelledRequestEmail.setRequestId(request.getRecId());
                cancelledRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_CANCELLED);
                cancelledRequestEmail.setActivity(ACTIVITY_CANCEL);
                RequestDto requestDto = this.findRequestSourcingForEditingByRecId(cancelledRequestEmail.getRequestId());
                emailService.sendEMailNotification(cancelledRequestEmail, requestDto);

                String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
                return RequestMapper.INSTANCE.toRequestDto(request, timeZone);
            }
            return null;
        } else {
            throw new BusinessException(ApiMessage.E7087, ApiMessage.E7087.description());
        }
    }

    @Override
    public RequestDto approveRequest(RequestApproveRequest approveRequest) {
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestsByRecId(approveRequest.getRequestId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        TenantRequestStatus tenantRequestStatus;
        TenantApprovalStatus tenantApprovalStatus;

        // Make sure all Request Item has been sourcing for all ExittingPrice/ERFX items completely
        // Checklist :
        // - Status -> Awaiting,
        if (request.getRequestStatus().getName().equals(TENANT_REQUEST_AWAITING.code()) &&
            AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_CONFIRM)
        ) {
            List<RequestItem> requestItemList = request.getRequestItemList(); //requestItemRepository.getRequestItemByRequestId(request.getRecId());


            ForwardedApprover forwardedApprover = null;
            boolean isForwardedApprover = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
            if (isForwardedApprover) {
                forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
            }

            String tenantCode = AppUtil.getTenantId();
            Tenant tenant = tenantService.findByCode(tenantCode);

            if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {
                // Find instanceApproverId by approverName & documentId //
                WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
                criteria.setPageNumber(1);
                criteria.setPageSize(1);

                List<String> refDocumentIds = new ArrayList<>();
                refDocumentIds.add(request.getRecId().toString());
                criteria.setRefDocumentId(refDocumentIds);

                List<String> refReferApproverIds = new ArrayList<>();
                refReferApproverIds.add(isForwardedApprover ? forwardedApprover.getFromApprover() : request.getAssignedBy());
                criteria.setReferApproverId(refReferApproverIds);

                // Optional Params
                List<Long> workflowInstanceIds = new ArrayList<>();
                workflowInstanceIds.add(request.getWorkflowInstanceId());
                criteria.setWorkflowInstanceId(workflowInstanceIds);

                Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);

                List<Long> workflowApproverIds = approvers.getContent().stream()
                        .map(WorkflowInstApproverDto::getRecordId)
                        .collect(Collectors.toList());

                Long instanceApproverId = workflowApproverIds.size() > 0 ? workflowApproverIds.get(0) : 0L;

                WorkflowInstanceApprovalRequest wfRequest = new WorkflowInstanceApprovalRequest();
                wfRequest.setRemark(approveRequest.getReason());
                wfRequest.setInstanceApproverId(instanceApproverId);
                wfRequest.setReferApproverId(isForwardedApprover ? forwardedApprover.getFromApprover() : request.getAssignedBy());
                WorkflowInstanceApprovalResponse ApprovalResponse = workflowInstanceApprovalService.approve(request.getWorkflowInstanceId(), wfRequest);

            }

            //TODO: Checking with the number of remaining required Approver of current Instance is 0, then update Request to be appropriate Status
            updateRequestStatusAfterSourcing(request);

            tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_COMPLETED.code(), request.getTenant());
            request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
            request.setApprovalStatus(tenantApprovalStatus);

            request.setApprovedReason(approveRequest.getReason());
            request.setApprovalDate(DateTimeUtil.getTimestampUTC());
            request.setUpdatedBy(AppUtil.getUserName());
            request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            requestRepository.save(request);

            // Save Request History : 5 CONFIRM	Confirm request successfully //
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_CONFIRM.id());

            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request, timeZone);

            // Send email notification //
            SendEMailRequest approveRequestEmail = new SendEMailRequest();
            approveRequestEmail.setRequestId(request.getRecId());
            approveRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_APPROVED);
            approveRequestEmail.setActivity(ACTIVITY_CONFIRM);
            RequestDto requestDtoForEmail = this.findRequestSourcingForEditingByRecId(approveRequestEmail.getRequestId());
            emailService.sendEMailNotification(approveRequestEmail, requestDtoForEmail);

            return requestDto;
        }
        return null;
    }

    @Override
    @Transactional
    public void updateRequestStatusAfterSourcing(Request request) {
        List<RequestItem> requestItemList = request.getRequestItemList();

        long completelySourcing = requestItemList.stream()
                .filter(r -> r.getSourcingStatus().getRecId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_CANCELLED.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_DELETED.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_NO_QUALIFIED_SUPPLIER.id()))
                .count();

        TenantRequestStatus tenantRequestStatus;
        if (completelySourcing == requestItemList.size()) {
            tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(
                    TENANT_REQUEST_COMPLETED.code(), request.getTenant());
        } else {
            tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(
                    TENANT_REQUEST_PARTIAL_COMPLETED.code(), request.getTenant());
        }

        request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
        request.setRequestStatus(tenantRequestStatus);
    }


    @Override
    @Transactional
    public boolean forwardApproval(RequestForwarderRequest forwarderRequest) {
        Request request = requestRepository.findRequestsByRecId(forwarderRequest.getRequestId());
        if (request != null) {
            RequestForwarder forwarder = new RequestForwarder();
            forwarder.setRequest(request);
            forwarder.setFromApprover(forwarderRequest.getRequestApproveName());
            forwarder.setToApprover(forwarderRequest.getRequestForwarderName());
            forwarder.setCreatedBy(AppUtil.getUserName());
            forwarder.setCreatedDate(DateTimeUtil.getTimestampUTC());

            requestForwarderRepository.saveAndFlush(forwarder);

            // Change assign to new approver //
            request.setAssignedBy(forwarderRequest.getRequestForwarderName());
            requestRepository.save(request);

            // Save history //
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_FORWARD.id());

            // Send mail notification //
            this.sendEmailForward(forwarderRequest);
            return true;
        }
        return false;
    }

    @Override
    public void sendEmailForward(RequestForwarderRequest forwarderRequest) {
        Request request = requestRepository.findRequestsByRecId(forwarderRequest.getRequestId());
        if (request != null) {
            SendEMailRequest forwardedRequestEmail = new SendEMailRequest();
            forwardedRequestEmail.setRequestId(request.getRecId());
            forwardedRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER);
            forwardedRequestEmail.setActivity(ACTIVITY_FORWARD);
            RequestDto requestDtoForEmail = prepareRequestSourcingByRecId(forwardedRequestEmail.getRequestId());
            if (requestDtoForEmail == null) {
                log.info("Send Email Forward Cannot get RequestDto of RecId : " + forwardedRequestEmail.getRequestId());
                return;
            }
            emailService.sendEMailNotification(forwardedRequestEmail, requestDtoForEmail);

            SendEMailRequest receiveForwardedRequestEmail = new SendEMailRequest();
            receiveForwardedRequestEmail.setRequestId(request.getRecId());
            receiveForwardedRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST);
            receiveForwardedRequestEmail.setActivity(ACTIVITY_FORWARD);
            emailService.sendEMailNotification(receiveForwardedRequestEmail, requestDtoForEmail);
        }
    }

    @Override
    public RequestDto rejectRequest(RequestRejectRequest rejectRequest) {
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestsByRecId(rejectRequest.getRequestId());
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_REJECT)) {
            TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(TENANT_REQUEST_REJECTED.code(), request.getTenant());
            TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_REJECTED.code(), request.getTenant());

            // Make sure all Request Item has been sourcing for all ExittingPrice/ERFX items completely
            // Checklist :
            // - Status -> Awaiting,
            if (request.getRequestStatus().getName().equals(TENANT_REQUEST_AWAITING.code())) {
                List<RequestItem> requestItemList = request.getRequestItemList(); //requestItemRepository.getRequestItemByRequestId(request.getRecId());
                long Sourcing = requestItemList.stream().filter(r -> r.getSourcingStatus().getRecId().equals(SOURCING_AWAITING_RESPONSE.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_PENDING.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_AWAITING_ACTIVE.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_AWAITING_SHORTLIST.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_AWAITING_APPROVE_SHORTLIST.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_QUALIFIED_SUPPLIER.id()) ||
                        r.getSourcingStatus().getRecId().equals(SOURCING_NO_QUALIFIED_SUPPLIER.id())).count();

                // If All Items status either CANCELLED or  DELETED, the Request status will be REQUEST_REJECTED //
                if (Sourcing == 0) {

                    ForwardedApprover forwardedApprover = null;
                    boolean isForwardedApprover = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
                    if (isForwardedApprover) {
                        forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
                    }

                    String tenantCode = AppUtil.getTenantId();
                    Tenant tenant = tenantService.findByCode(tenantCode);

                    if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {

                        // Find instanceApproverId by ApproverName & DocumentId //
                        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
                        criteria.setPageNumber(1);
                        criteria.setPageSize(1);

                        List<String> refDocumentIds = new ArrayList<>();
                        refDocumentIds.add(request.getRecId().toString());
                        criteria.setRefDocumentId(refDocumentIds);

                        List<String> referApproverIds = new ArrayList<>();
                        referApproverIds.add(isForwardedApprover ? forwardedApprover.getFromApprover() : request.getAssignedBy());
                        criteria.setReferApproverId(referApproverIds);

                        // Optional Params
                        List<Long> workflowInstanceIds = new ArrayList<>();
                        workflowInstanceIds.add(request.getWorkflowInstanceId());
                        criteria.setWorkflowInstanceId(workflowInstanceIds);

                        Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);

                        List<Long> workflowApproverIds = approvers.getContent().stream()
                                .map(WorkflowInstApproverDto::getRecordId)
                                .collect(Collectors.toList());

                        Long instanceApproverId = workflowApproverIds.size() > 0 ? workflowApproverIds.get(0) : 0L;

                        WorkflowInstanceApprovalRequest wfRequest = new WorkflowInstanceApprovalRequest();
                        wfRequest.setRemark(rejectRequest.getReason());
                        wfRequest.setInstanceApproverId(instanceApproverId);
                        wfRequest.setReferApproverId(isForwardedApprover ? forwardedApprover.getFromApprover() : request.getAssignedBy());
                        WorkflowInstanceApprovalResponse ApprovalResponse = workflowInstanceApprovalService.reject(request.getWorkflowInstanceId(), wfRequest);
                    }


                    request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                    request.setRequestStatus(tenantRequestStatus);
                    request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                    request.setApprovalStatus(tenantApprovalStatus);
                    request.setRejectedReason(rejectRequest.getReason());
                    request.setApprovalDate(DateTimeUtil.getTimestampUTC());
                    request.setUpdatedBy(AppUtil.getUserName());
                    request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    if (request.getAssignedBy() != null && !request.getAssignedBy().equals(AppUtil.getUserName()) && request.getDelegateActionBy() == null) {
                        request.setDelegateActionBy(AppUtil.getUserName());
                        String authHeader = "Bearer " + AppUtil.getJwtToken();
                        DelegationActiveCreateDateRequest delegationActiveCreateDateRequest = new DelegationActiveCreateDateRequest();
                        delegationActiveCreateDateRequest.setDelegateeBy(AppUtil.getUserName());
                        delegationActiveCreateDateRequest.setDelegatorBy(request.getAssignedBy());
                        DelegationActiveCreateDateResponse delegationActiveCreateDateResponse = delegateClient.getDelegationActiveCreateDate(authHeader, delegationActiveCreateDateRequest);
                        if (delegationActiveCreateDateResponse.getCreatedDate() != null) {
                            request.setDelegateActionDate(delegationActiveCreateDateResponse.getCreatedDate());
                        } else {
                            request.setDelegateActionDate(DateTimeUtil.getTimestampUTC());
                        }
                    }
                    requestRepository.save(request);

                    // Save Request History : 6 REJECT	Reject request successfully //
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REJECT.id());

                    // Send email notification //
                    SendEMailRequest rejectedRequestEmail = new SendEMailRequest();
                    rejectedRequestEmail.setRequestId(request.getRecId());
                    rejectedRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_REJECTED);
                    rejectedRequestEmail.setActivity(ACTIVITY_REJECT);
                    RequestDto requestDtoForEmail = this.findRequestSourcingForEditingByRecId(request.getRecId());
                    emailService.sendEMailNotification(rejectedRequestEmail, requestDtoForEmail);

                    String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
                    return RequestMapper.INSTANCE.toRequestDto(request, timeZone);
                }
            }
        }
        return null;
    }



    @Override
    public Request searchRequestByRecId(Long requestId) {
        return requestRepository.findRequestByRecId(requestId);
    }

    @Override
    public RequesterRequestSearchDto searchRequestByCondition(RequestSearchRequest searchRequest, Pageable pageable) {
        String tenantId = AppUtil.getTenantId();
        Page<Request> requests = requestRepository.findAll(Specification.where(getSpecificationByCondition(searchRequest)), pageable);
        int totalPage = requests.getTotalPages();
        long total = requests.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);



        Tenant tenant = tenantService.findByCode(tenantId);
        String copyToPrPrivilegeCodesConfig = tenantConfigService.getCopyToPrPrivilegeCodes(tenant.getRecId());
        String copyToPrOrganizationsConfig = tenantConfigService.getCopyToPrOrganizations(tenant.getRecId());
        Boolean forceSelectAllItemCopyToPR = tenantConfigService.getForceSelectAllItemCopyToPR(tenant.getRecId());
        List<String> copyToPrPrivilegeCodes =
                StringUtils.isNotBlank(copyToPrPrivilegeCodesConfig)
                        ? Arrays.stream(copyToPrPrivilegeCodesConfig.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList())
                        : Collections.emptyList();

        List<String> copyToPrOrganizations =
                StringUtils.isNotBlank(copyToPrOrganizationsConfig)
                        ? Arrays.stream(copyToPrOrganizationsConfig.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList())
                        : Collections.emptyList();

        //boolean allowCopyToPR = AppUtil.checkCanCopyToPRByPrivilegeCode(copyToPrPrivilegeCodes);

        List<RequesterRequestDto> resultList = RequestMapper.INSTANCE.toRequesterRequestDtoList(requests.getContent(), timeZone, copyToPrPrivilegeCodes, forceSelectAllItemCopyToPR, copyToPrOrganizations);

        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

        DelegatorByDelegateeRequest delegatorByDelegateeRequest = new DelegatorByDelegateeRequest();
        delegatorByDelegateeRequest.setDelegateeUserName(AppUtil.getUserName());
        delegatorByDelegateeRequest.setDelegationStatuses(Collections.singletonList(DELEGATION_ACTIVE.id()));

        List<String> delegatorUserNames;
        DelegatorByDelegateeResponse delegatorByDelegateeResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegatorByDelegateeRequest);
        if (delegatorByDelegateeResponse.getDelegatorUserNames() != null) {
            delegatorUserNames = delegatorByDelegateeResponse.getDelegatorUserNames();
        } else {
            delegatorUserNames = null;
        }

        resultList = resultList.stream()
                .peek(requestDto -> {

                    boolean isDelegationActive = delegatorUserNames != null && delegatorUserNames.contains(requestDto.getAssignedBy());

                    boolean allowUpdateRequest = (AppUtil.getPrivilegeScopes().get("SQN") != null ||
                            AppUtil.getPrivilegeScopes().get("SQP") != null) &&
                            !isDelegationActive; // If delegation is active, So this Purchaser can't edit this request.


                    boolean allowCancel = requestDto.getRequestDeptApproverDtoList() != null && !requestDto.getRequestDeptApproverDtoList().stream()
                            .anyMatch(i -> i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code())
                                    || i.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()));

                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    RequestStatusDto requestStatusByPrivilegeCode = updateRequestStatusByPrivilegeCode(
                            requestDto.getRequestStatus(),
                            allowUpdateRequest,
                            requestDto.getRequestStatus().isCanCopyToPR(),
                            allowCancel);
                    requestDto.setRequestStatus(requestStatusByPrivilegeCode);

                    ApprovalStatusDto approvalStatusByPrivilegeCode = updateApprovalStatusByPrivilegeCode(
                            requestDto.getApprovalStatus(),
                            allowUpdateRequest,
                            requestDto.getApprovalStatus().isCanCopyToPR());

                    if(approvalStatusByPrivilegeCode != null) {
                        requestDto.setApprovalStatus(approvalStatusByPrivilegeCode);
                    }

                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
                    // Temporary mock data for Tags of BAY
                    // TODO: Cleanup this later
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        RequestType requestType = requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestType != null)
                            requestDto.setTags(requestType.getTypeName());
                        //requestDto.setTypeName(requestType.getTypeName());
                    }
                    this.setRequesterRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), tenantId, false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }
                }).collect(Collectors.toList());

        RequesterRequestSearchDto requestSearchDto = new RequesterRequestSearchDto();
        requestSearchDto.setRequestDtoList(resultList);
        requestSearchDto.setTotal(total);
        requestSearchDto.setTotalPage(totalPage);
        requestSearchDto.setPageSize(resultList.size());
        return requestSearchDto;
    }

    @Override
    public ApproverRequestSearchDto searchAllApprovalListByCondition(ApprovalSearchRequest searchRequest, Pageable pageable) {
        Page<Request> requests = requestRepository.findAll(Specification.where(getApprovalSpecificationByCondition(searchRequest, true)), pageable);

        int totalPage = requests.getTotalPages();
        long total = requests.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<ApproverRequestDto> resultList = RequestMapper.INSTANCE.toApproverRequestDtoList(requests.getContent(), timeZone);

        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());


        resultList = resultList.stream()
                .peek(requestDto -> {

                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    requestDto.getApprovalStatus().setCanAssignToMe(StringUtils.isEmpty(requestDto.getAssignedBy()) &&
                            requestDto.getApprovalStatus().getName().equals(TENANT_REQUEST_AWAITING.code()));
                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);

                    this.setApproverRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }
                })
                .collect(Collectors.toList());

        ApproverRequestSearchDto requestSearchDto = new ApproverRequestSearchDto();
        requestSearchDto.setRequestDtoList(resultList);
        requestSearchDto.setTotal(total);
        requestSearchDto.setTotalPage(totalPage);
        requestSearchDto.setPageSize(resultList.size());
        return requestSearchDto;
    }

    @Override
    public ApproverRequestSearchDto searchMyApprovalListByCondition(ApprovalSearchRequest searchRequest, Pageable pageable) {
        Page<Request> requests = requestRepository.findAll(Specification.where(getApprovalSpecificationByCondition(searchRequest, false)), pageable);
        int totalPage = requests.getTotalPages();
        long total = requests.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        String tenantId = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantId);
        String copyToPrPrivilegeCodesConfig = tenantConfigService.getCopyToPrPrivilegeCodes(tenant.getRecId());
        String copyToPrOrganizationsConfig = tenantConfigService.getCopyToPrOrganizations(tenant.getRecId());
        Boolean forceSelectAllItemCopyToPR = tenantConfigService.getForceSelectAllItemCopyToPR(tenant.getRecId());
        List<String> copyToPrPrivilegeCodes =
                StringUtils.isNotBlank(copyToPrPrivilegeCodesConfig)
                        ? Arrays.stream(copyToPrPrivilegeCodesConfig.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList())
                        : Collections.emptyList();

        List<String> copyToPrOrganizations =
                StringUtils.isNotBlank(copyToPrOrganizationsConfig)
                        ? Arrays.stream(copyToPrOrganizationsConfig.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList())
                        : Collections.emptyList();
        List<ApproverRequestDto> resultList = RequestMapper.INSTANCE.toApproverRequestDtoList(requests.getContent(), timeZone, copyToPrPrivilegeCodes, forceSelectAllItemCopyToPR, copyToPrOrganizations);
        String myApprovalFullName = UserDetailServiceUtil.getFullName(AppUtil.getUserName());
        Random random = new Random();


        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

        resultList = resultList.stream()
                .peek(requestDto -> {

                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    requestDto.getApprovalStatus().setCanAssignToMe(requestDto.getAssignedBy() == null &&
                            requestDto.getApprovalStatus().getName().equals(TENANT_REQUEST_AWAITING.code()));
                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(myApprovalFullName);
                    // Temporary mock data for Tags of BAY
                    // TODO: Cleanup this later
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        RequestType requestType = requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestType != null)
                            // requestDto.setTypeName(requestType.getTypeName());
                            // to next
                            requestDto.setTags(requestType.getTypeName());
                    }

                    this.setApproverRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }
                })
                .collect(Collectors.toList());

        ApproverRequestSearchDto requestSearchDto = new ApproverRequestSearchDto();
        requestSearchDto.setRequestDtoList(resultList);
        requestSearchDto.setTotal(total);
        requestSearchDto.setTotalPage(totalPage);
        requestSearchDto.setPageSize(resultList.size());
        return requestSearchDto;
    }

    @Override
    public List<RequesterRequestDto> searchRequestExcelByCondition(RequestSearchRequest searchRequest) {
        String tenantId = AppUtil.getTenantId();
        Sort sort;
        String sortBy = searchRequest.getSortBy();
        String sortOrder = searchRequest.getSortOrder();

        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
            sort = Sort.by(REQUEST_NO.description()).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortOrder != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase(REQUEST_NO.description())) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, REQUEST_NO.description()));
            }
            sort = Sort.by(orders);
        }
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);

        Page<Request> pageRequests = requestRepository.findAll(Specification.where(getExcelSpecificationByCondition(searchRequest)), pageable);
        List<Request> requests = pageRequests.getContent();
                String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<RequesterRequestDto> resultList = RequestMapper.INSTANCE.toRequesterRequestDtoList(requests, timeZone);

        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

        resultList = resultList.stream()
                .peek(requestDto -> {
                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
                    // Temporary mock data for Tags of BAY
                    // TODO: Cleanup this later
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        RequestType requestType = requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestType != null)
                            requestDto.setTags(requestType.getTypeName());
                        //requestDto.setTypeName(requestType.getTypeName());
                    }
                    this.setRequesterRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), tenantId, false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }
                }).collect(Collectors.toList());

        return resultList;
    }

    @Override
    public boolean validateRequest(Long requestId) {
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestByRecId(requestId);
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        boolean isDelegationActive = false;
        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
            isDelegationActive = true;
        }

        boolean isOwnerPurchaser = false;
        if (request.getDelegateActionBy() != null) {
            isOwnerPurchaser = AppUtil.isPurchaser() && request.getDelegateActionBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName());
        } else {
            if(isDelegationActive) {
                isOwnerPurchaser = AppUtil.isPurchaser() && request.getApprovalStatus().getName().equals(TENANT_APPROVAL_AWAITING.code());
            } else {
                isOwnerPurchaser = AppUtil.isPurchaser() &&
                        ((request.getAssignedBy() != null && request.getAssignedBy().toLowerCase().equalsIgnoreCase(AppUtil.getUserName())));
            }
        }

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_CONVERT_ERFX)) {
            if(isOwnerPurchaser) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RequestDto saveRequest(RequestRequest reqRequest, EPAuthReviewerResponse response, boolean isSaveDraft) {
        String idp = AppUtil.getIdp();
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        boolean isValidOwnerAndPermission;
        if(reqRequest.getRecId() == 0) {
            isValidOwnerAndPermission = true;
        } else {

            Request request = requestRepository.findRequestsByRecId(reqRequest.getRecId());
            UserDto user = AppUtil.getUser();
            List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
            List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
            List<RequestReviewer> requestReviewers = requestReviewerRepository.getByRequest(request.getRecId());
            List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
            List<RequestReportLine> reportLines = requestReportLineRepository.getByRequest(request.getRecId());

            DelegationDto delegationDto = null;
            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                delegationDto = delegationActiveResponse.getData();
            }
            isValidOwnerAndPermission = AppUtil.isAllowedAction(user, request, RequestPurchasers, requestApprovers, null, null, requestReviewers, reportLines, delegationDto, tenantRequestStatuses, ACTIVITY_EDIT);

        }

        if(isValidOwnerAndPermission) {
            boolean isInValidApprover = checkIsInValidApprover(reqRequest, response);
            if (isInValidApprover) {
                return null;
            }
            Request request = requestRepository.save(this.setRequest(reqRequest, isSaveDraft));
            requestObjectiveService.saveOrUpdate(reqRequest.getObjectiveObj() != null ? Integer.parseInt(reqRequest.getObjectiveObj().getValue()) : null, request);

            if (reqRequest.getCategoryObj() != null && reqRequest.getCategoryObj().getValue() != null) {
                requestCategoryService.saveOrUpdate(Integer.parseInt(reqRequest.getCategoryObj().getValue()), request);
            }

            if(reqRequest.getSubCategoryObj() != null && reqRequest.getSubCategoryObj().getValue() != null) {
                requestSubCategoryService.saveOrUpdate(Integer.parseInt(reqRequest.getSubCategoryObj().getValue()), request);
            }

            if(reqRequest.getPurchaserObj() != null && reqRequest.getPurchaserObj().getValue() != null) {
                requestPurchaserService.saveOrUpdate(Integer.parseInt(reqRequest.getPurchaserObj().getValue()), request);
            }

            if(reqRequest.getDepartmentObj() != null && reqRequest.getDepartmentObj().getValue() != null) {
                requestDepartmentService.saveOrUpdate(reqRequest.getDepartmentObj().getValue(), request);
            }

            if (tenant.getRecId() == 1) {
                if (reqRequest.getTypeObj() != null && !"3".equals(reqRequest.getTypeObj().getValue())) {
                    if(reqRequest.getProjectObj() != null && reqRequest.getProjectObj().getValue() != null) {
                        request.setProjectCode(reqRequest.getProjectObj().getValue());
                        request.setProjectName(reqRequest.getProjectObj().getName());
                    }
                    requestProjectService.saveOrUpdate(reqRequest.getProjectObj() != null ? reqRequest.getProjectObj().getValue() : null, request, tenant.getRecId());
                } else {
                    if(reqRequest.getDepartmentObj() != null && reqRequest.getDepartmentObj().getValue() != null) {
                        request.setProjectCode(reqRequest.getDepartmentObj().getName());
                        request.setProjectName(reqRequest.getDepartmentObj().getLabel());
                    }
                    requestDepartmentService.saveOrUpdate(reqRequest.getDepartmentObj() != null ? reqRequest.getDepartmentObj().getValue() : null, request);
                }
            }
            else if ("bay".equalsIgnoreCase(tenant.getCode())) {
                if(reqRequest.getProjectObj() != null && reqRequest.getProjectObj().getValue() != null) {
                    request.setProjectCode(reqRequest.getProjectObj().getValue());
                    request.setProjectName(reqRequest.getProjectObj().getName());
                }
//                requestAdminProjectService.saveOrUpdate(projectCode, projectName, request, tenant.getRecId());
            } else {
                if(reqRequest.getProjectObj() != null && reqRequest.getProjectObj().getValue() != null) {
                    requestProjectService.saveOrUpdate(reqRequest.getProjectObj().getValue(), request, tenant.getRecId());
                }
            }

            if(reqRequest.getTypeObj() != null) {
                requestTypeService.saveOrUpdate(Integer.parseInt(reqRequest.getTypeObj().getValue()), request);
            }

            if(reqRequest.getBudgetRefNoObj() != null) {
                requestBudgetRefNoService.saveOrUpdate(Integer.parseInt(reqRequest.getBudgetRefNoObj().getValue()), request);
            }

            LocationDto locationDto = null;

            if (reqRequest.getDeliveryLocation() != null) {
                locationDto = reqRequest.getDeliveryLocation();
            } else if (reqRequest.getContactName() != null || reqRequest.getContactPhone() != null || reqRequest.getLocation() != null) {
                Optional<Location> optionalLocation = locationRepository.findByTenantAndName(tenant, "Other");
                if (optionalLocation.isPresent()) {
                    Location location = optionalLocation.get();
                    locationDto = LocationMapper.INSTANCE.toLocationDto(location);
                }
            }
            if (locationDto != null) {
                locationDto.setAddress(reqRequest.getLocation() != null ? reqRequest.getLocation() : null);
                locationDto.setContactName(reqRequest.getContactName() != null ? reqRequest.getContactName() : null);
                locationDto.setPhone(reqRequest.getContactPhone() != null ? reqRequest.getContactPhone() : null);
            }
            requestLocationService.saveOrUpdate(locationDto, request);

            // BAY //
            requestAdditionalService.saveOrUpdate(request, tenant, reqRequest);
            // SAVE REQUEST REVIEWER //
            if (!CollectionUtils.isEmpty(reqRequest.getReviewers())) {
                RequestReviewerRequest requestReviewerRequest = new RequestReviewerRequest();
                requestReviewerRequest.setRequestId(request.getRecId());
                requestReviewerRequest.setReviewers(reqRequest.getReviewers());
                requestReviewerService.saveRequestReviewer(requestReviewerRequest, response);
            } else {
                requestReviewerService.deleteReviewerByRequestId(request.getRecId());
            }

            // SAVE REQUEST REPORT-LINE //
            if (null != reqRequest.getReportLines() && !reqRequest.getReportLines().isEmpty()) {
                requestReportLineService.saveRequestReportLine(request, tenant, reqRequest.getReportLines(), response);
            } else {
                requestReportLineService.deleteByRequestId(request.getRecId());
            }

            // Save Dept Approver
            if(request.getDeptApprovalStatus() == null || (!request.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code()) &&
                    !request.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()))) {
                if (null != reqRequest.getApprovers() && !reqRequest.getApprovers().isEmpty()) {
                    requestDeptApproverService.saveRequestDeptApprover(request, tenant, reqRequest.getApprovers());
                } else {
                    requestDeptApproverService.deleteByRequestId(request.getRecId());
                }
            }

            // Save Request Attachment //
            if (null != reqRequest.getRequestAttachmentList() && !reqRequest.getRequestAttachmentList().isEmpty()) {
                requestAttachmentService.saveRequestAttachment(request, reqRequest);
            } else {
                requestAttachmentService.deleteByRequestId(request);
            }

            if (!isSaveDraft) {

                List<InstanceApproverDto> approvers;
                List<RequestPurchaser> RequestPurchasers = null;

                if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {

                    // Generate Workflow //
                    if (request.getWorkflowInstanceId() == 0) {
                        if (tenant.getRecId() == 1) {
                            Long workflowId = Long.valueOf(tenantConfigService.getWorkflowTemplateId(tenant.getRecId()));
                            Long workflowInstanceId = workFlowInstanceGenerator.generateWorkflowInstance(workflowService, workflowId, request.getRecId().toString());
                            request.setWorkflowInstanceId(workflowInstanceId);
                            requestRepository.save(request);
                        } else {
                            Long workflowId = Long.valueOf(tenantConfigService.getWorkflowTemplateId(tenant.getRecId()));
                            WorkflowParam workflowParam = new WorkflowParam();
                            workflowParam.setType(reqRequest.getTypeObj().getLabel());
                            workflowParam.setCategory(reqRequest.getCategoryLabel().trim());
                            workflowParam.setSubCategory(reqRequest.getSubCategoryLabel().trim());


                            workflowParam.setAmount(0.0);
                            Long workflowInstanceId = workFlowInstanceGenerator.generateWorkflowInstanceWithWorkflowParam(workflowService, workflowId, request.getRecId().toString(), workflowParam);
                            request.setWorkflowInstanceId(workflowInstanceId);
                            requestRepository.save(request);
                        }
                    }


                    try {
                        workflowService.startWorkflow(request.getWorkflowInstanceId());

                        // Snapshot all Workflow Approver related to this Request //
                        approvers = this.getApprovers(tenantCode, idp, request);
                        RequestPurchasers = approvers.stream()
                                .filter(Objects::nonNull)
                                .map(item -> {
                                    Optional<Purchaser> purchaser = purchaserRepository.findByTenantAndLoginId(tenant.getRecId(), item.getLoginId());
                                    return purchaser.map(value -> getRequestPurchaser(request, value)).orElse(null);
                                })
                                .collect(Collectors.toList());

                    } catch (Exception ex) {
                        if (!ex.getMessage().contains("E3023"))
                            throw new AppException(ApiMessage.E7029, String.format(ApiMessage.E7029.description(), ex.getMessage()));
                    }

                } else {
                    if(reqRequest.getPurchaserObj() != null) {
                        if (tenantConfigService.isAutoAssignToPurchaser(tenant.getRecId())) {
                            if (!requestForwarderService.isCurrentForwarder(request.getRecId(), AppUtil.getUserName())) {

                                if(request.getRequestStatus().getName().equals(TENANT_REQUEST_DRAFT.code()) ||
                                        request.getRequestStatus().getName().equals(TENANT_REQUEST_PENDING.code())) {
                                    Integer purchaserId = Integer.parseInt(reqRequest.getPurchaserObj().getValue());
                                    Purchaser purchaser = purchaserRepository.findById(purchaserId)
                                            .orElseThrow(() -> new BusinessException(ApiMessage.E7086, ApiMessage.E7086.description()));
                                    request.setAssignedBy(purchaser.getLoginId());
                                }
                            }
                        }
                    }
                }

                // Save PDPA Due Diligence CheckList Form
                String uniqueIdDueDiligenceCheckListForm = tenantConfigService.getDueDiligenceCheckListForm(request.getTenant().getRecId());
                if(uniqueIdDueDiligenceCheckListForm != null) {
                    if (reqRequest.getThirdPartyRole() != null &&
                            (reqRequest.getThirdPartyRole().equalsIgnoreCase(THIRD_PARTY_ROLE_DATA_PROCESSOR.code()) ||
                                    reqRequest.getThirdPartyRole().equalsIgnoreCase(THIRD_PARTY_ROLE_DATA_CONTROLLER.code()))) {
                        requestAttachmentService.savePDPARequestAttachment(request, reqRequest, uniqueIdDueDiligenceCheckListForm);
                    } else {
                        requestAttachmentService.deletePDPAByRequestId(request);
                    }
                }
        
                // Save (Snapshot) Request Approvers from Approval Workflow Service
                if (RequestPurchasers != null && RequestPurchasers.size() > 0) {
                    RequestPurchasers = RequestPurchasers.stream().filter(Objects::nonNull).collect(Collectors.toList());
                    requestPurchaserRepository.deleteByRequest(request);
                    requestPurchaserRepository.saveAll(RequestPurchasers);

                    if (tenantConfigService.isAutoAssignToPurchaser(tenant.getRecId())) {
                        if (!requestForwarderService.isCurrentForwarder(request.getRecId(), AppUtil.getUserName())) {
                            request.setAssignedBy(RequestPurchasers.get(0).getPurchaser().getLoginId());
                        }
                    }
                }

                // Save Request History : 2	SUBMIT	The request was successfully submitted
                if (null != reqRequest.getMainStatus() && reqRequest.getMainStatus().equalsIgnoreCase("edit")) {
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_EDIT.id());
                } else {
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_SUBMIT.id());
                }

                // Send email notification
                SendEMailRequest submitRequestEmail = new SendEMailRequest();
                submitRequestEmail.setRequestId(request.getRecId());
                submitRequestEmail.setEmailActivity(EmailActivity.PLEASE_REVIEW_AND_APPROVE_SR);
                submitRequestEmail.setActivity(ACTIVITY_SUBMIT);
                RequestDto requestDtoForEmail = this.findRequestSourcingForEditingByRecId(submitRequestEmail.getRequestId());
                emailService.sendEMailNotification(submitRequestEmail, requestDtoForEmail);

                SendEMailRequest reviewRequestEmail = new SendEMailRequest();
                reviewRequestEmail.setRequestId(request.getRecId());
                reviewRequestEmail.setEmailActivity(EmailActivity.PLEASE_REVIEW_SR);
                reviewRequestEmail.setActivity(ACTIVITY_SUBMIT);
                emailService.sendEMailNotification(reviewRequestEmail, requestDtoForEmail);

                if(Objects.requireNonNull(AppUtil.getUserName()).equalsIgnoreCase(request.getCreatedBy())) {
                    // Send email notification to Supplier Contact via Queue/Logging
                    NotifySupplierRequest notifySupplierRequest = new NotifySupplierRequest();
                    notifySupplierRequest.setTenant(tenantCode);
                    notifySupplierRequest.setSrName(reqRequest.getRequestName());
                    notifySupplierRequest.setOrgName(reqRequest.getOrganizationObj() != null ? reqRequest.getOrganizationObj().getLabel() : "");
                    notifySupplierRequest.setOpCat(reqRequest.getCategoryObj() != null ? reqRequest.getCategoryObj().getLabel() : "");
                    notifySupplierRequest.setOpSubCat(reqRequest.getSubCategoryObj() != null ? reqRequest.getSubCategoryObj().getLabel() : "");
                    notifySupplierRequest.setSrSubmitDate(DateTimeUtil.getTimestamp("yyyy-MM-dd HH:mm:ss"));
                    supplierNotificationService.notifySupplier(notifySupplierRequest, false);
                }

            } else {
                // Save Request History : 1	SAVE draft request successfully
                requestHistoryService.saveRequestHistoryByAction(request, (reqRequest.getRecId() != null && !reqRequest.getRecId().equals(0L)) ? ACTIVITY_EDIT.id() : ACTIVITY_SAVE.id());
            }
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            return setRequestDetailsFromRepositories(RequestMapper.INSTANCE.toRequestDto(request, timeZone), request.getRecId(), tenantCode);
        } else {
            return  null;
        }
    }

    private static RequestPurchaser getRequestPurchaser(Request request, Purchaser purchaser) {
        RequestPurchaser requestPurchaser = new RequestPurchaser();
        requestPurchaser.setId(new RequestPurchaserKey(request.getRecId(), purchaser.getRecId(), 1));
        requestPurchaser.setRequest(request);
        requestPurchaser.setPurchaser(purchaser);
        requestPurchaser.setWorkflowPermission(true);
        requestPurchaser.setCreatedBy(AppUtil.getUserName());
        requestPurchaser.setCreatedDate(DateTimeUtil.getTimestampUTC());
        return requestPurchaser;
    }

    private boolean checkIsInValidApprover(RequestRequest request, EPAuthReviewerResponse response) {
        Set<Long> inValidList = new HashSet<>();
        List<Long> approvers = new ArrayList<>();

        if (request != null && request.getReportLines() != null) {
            approvers.addAll(request.getReportLines());
        }
        if (request != null && request.getReviewers() != null) {
            for (String sysUserId : request.getReviewers()) {
                try {
                    Long reviewerId = Long.valueOf(sysUserId);
                    approvers.add(reviewerId);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        for (Long sysUserId : approvers) {
            if(sysUserId != null) {
                Optional<EPAuthReviewerDto> epAuthReviewerDto = response.getData()
                        .stream()
                        .filter(r -> r.getSysUserId().longValue() == sysUserId)
                        .findFirst();

                if (epAuthReviewerDto.isEmpty()) {
                    inValidList.add(sysUserId);
                }
            }
        }

        if (!inValidList.isEmpty()) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteRequestByRecId(Long requestId) {
        UserDto user = AppUtil.getUser();
        boolean result = false;
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_DELETE)) {

            List<RequestPurchaser> RequestPurchaserList = requestPurchaserRepository.findByRequest(request);
            if (!RequestPurchaserList.isEmpty()) {
                requestPurchaserRepository.deleteByRequest(request);
            }

            List<RequestAttachment> requestAttachmentList = requestAttachmentRepository.findRequestAttachmentsByRequest(request);
            if (!requestAttachmentList.isEmpty()) {
                requestAttachmentRepository.deleteRequestAttachmentByRequestId(request.getRecId());
            }

            List<RequestHistory> requestHistoryList = requestHistoryRepository.findRequestHistoryByRequest(request);
            if (!requestHistoryList.isEmpty()) {
                requestHistoryRepository.deleteRequestHistoriesByRequest(request);
            }

            List<RequestReviewer> requestReviewerList = requestReviewerRepository.getByRequest(request.getRecId());
            if (!requestReviewerList.isEmpty()) {
                requestReviewerRepository.deleteRequestReviewerByRequestId(request.getRecId());
            }

            List<RequestBudgetRefNo> requestBudgetRefNoList = requestBudgetRefNoRepository.findByRequest(request);
            if (!requestBudgetRefNoList.isEmpty()) {
                requestBudgetRefNoRepository.deleteByRequest(request);
            }

            List<RequestCategory> requestCatogoryList = requestCategoryRepository.findByRequest(request);
            if (!requestCatogoryList.isEmpty()) {
                requestCategoryRepository.deleteByRequest(request);
            }

            List<RequestDepartment> requestDepartmentList = requestDepartmentRepository.findByRequest(request);
            if (!requestDepartmentList.isEmpty()) {
                requestDepartmentRepository.deleteByRequest(request);
            }

            List<RequestForwarder> requestForwarderList = requestForwarderRepository.findByRequest(request);
            if (!requestForwarderList.isEmpty()) {
                requestForwarderRepository.deleteByRequest(request);
            }

            List<RequestLocation> requestLocationList = requestLocationRepository.findByRequest(request);
            if (!requestLocationList.isEmpty()) {
                requestLocationRepository.deleteByRequest(request);
            }

            List<RequestObjective> requestObjectiveList = requestObjectiveRepository.findByRequest(request);
            if (!requestObjectiveList.isEmpty()) {
                requestObjectiveRepository.deleteByRequest(request);
            }

            List<RequestProject> requestProjectList = requestProjectRepository.findByRequest(request);
            if (!requestProjectList.isEmpty()) {
                requestProjectRepository.deleteByRequest(request);
            }

            List<RequestSubCategory> requestSubCategoryList = requestSubCategoryRepository.findByRequest(request);
            if (!requestSubCategoryList.isEmpty()) {
                requestSubCategoryRepository.deleteByRequest(request);
            }

            List<RequestType> requestTypeList = requestTypeRepository.findByRequest(request);
            if (!requestTypeList.isEmpty()) {
                requestTypeRepository.deleteByRequest(request);
            }

            List<RequestApprover> requestDeptApproverList = requestApproverRepository.findRequestApproversByRequest(request);
            if (!requestDeptApproverList.isEmpty()) {
                requestApproverRepository.deleteByRequest(request);
            }

            requestReportLineService.delete(request);
            requestAdditionalService.delete(request);

            Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(requestId);
            if (requestQuestionnaireOptional.isPresent()) {
                RequestQuestionnaire questionnaire = requestQuestionnaireOptional.get();
                eFormService.deleteQuestionnaire(questionnaire.getFormId(), true);
                requestQuestionnaireRepository.deleteByRequest(request);
            }

            List<RequestItem> requestItemList = request.getRequestItemList();
            for (RequestItem requestItem : requestItemList) {
                requestItemService.deleteRequestItemByRecId(requestItem.getRecId());
            }

            requestRepository.deleteRequestByRecId(requestId);
            result = true;
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean assignRequestByRecId(Long requestId) {
        boolean result = false;
        UserDto user = AppUtil.getUser();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

//        if (request != null
//                && request.getAssignedBy() == null
//                && request.getRequestStatus().getName().equals(TENANT_REQUEST_AWAITING.code())) {

        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_ASSIGN)) {
            request.setAssignedBy(AppUtil.getUserName());
            request.setUpdatedBy(AppUtil.getUserName());
            request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            requestRepository.save(request);

            // Save Request History : 7 ASSIGN	Assign request as my task successfully
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_ASSIGN.id());
            result = true;
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeRequestByRecId(Long requestId) {
        boolean result = false;
        UserDto user = AppUtil.getUser();
        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<RequestPurchaser> requestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
        DelegationDto delegationDto = null;

        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());

        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
        if (delegationActiveResponse.getData() != null) {
            delegationDto = delegationActiveResponse.getData();
        }

//        if (request != null
//                && request.getAssignedBy() != null
//                && request.getRequestStatus().getName().equals(TENANT_REQUEST_AWAITING.code())) {

        if (AppUtil.isAllowedAction(user, request, requestPurchasers, null, null, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_REMOVE)) {
            List<RequestItem> requestItemList = request.getRequestItemList(); //requestItemRepository.getRequestItemByRequestId(request.getRecId());
            long partiallySourcing = requestItemList.stream().filter(r -> !r.getSourcingStatus().getRecId().equals(SOURCING_NONE.id())).count();

            if (partiallySourcing == 0L) {
                request.setAssignedBy(null);
                request.setUpdatedBy(AppUtil.getUserName());
                request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                requestRepository.save(request);

                // Save Request History : 8 REMOVE	Remove request from my task successfully
                requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REMOVE.id());
                result = true;
            }
        }
        return result;
    }

    public boolean isAllowAction(UserDto user, Request request, RequestForwarder requestForwarder, List<TenantApprovalStatus> tenantApprovalStatuses, List<TenantRequestStatus> tenantRequestStatuses) {
        boolean result = false;
        if (user == null || request == null ) {
            return false;
        }

        if (request.getRequestStatus().getName().equals(TENANT_APPROVAL_AWAITING.code())
        ) {
            result = isPermission(user, request, requestForwarder, tenantApprovalStatuses, tenantRequestStatuses);
        }
        return result;
    }

    public boolean isPermission(UserDto user, Request request, RequestForwarder requestForwarder, List<TenantApprovalStatus> tenantApprovalStatuses, List<TenantRequestStatus> tenantRequestStatuses) {
        boolean result = false;
        List<TenantApprovalStatus> tenantApprovalStatusesFilter = tenantApprovalStatuses.stream()
                .filter(tenantApprovalStatus -> tenantApprovalStatus.getApprovalStatus().getRecId() == request.getApprovalStatusId())
                .collect(Collectors.toList());

        List<TenantRequestStatus> tenantRequestStatusesFilter = tenantRequestStatuses.stream()
                .filter(tenantRequestStatus -> tenantRequestStatus.getRequestStatus().getRecId() == request.getStatusId())
                .collect(Collectors.toList());

        TenantApprovalStatus tenantApprovalStatus = !tenantApprovalStatusesFilter.isEmpty() ? tenantApprovalStatusesFilter.get(0): new TenantApprovalStatus();
        TenantRequestStatus tenantRequestStatus = !tenantRequestStatusesFilter.isEmpty() ? tenantRequestStatusesFilter.get(0): new TenantRequestStatus();

        if (tenantApprovalStatus == null || tenantRequestStatus == null) {
            return result;
        }

        if ((user.getUsername().equalsIgnoreCase(request.getAssignedBy()) ||
                user.getUsername().equalsIgnoreCase(request.getDelegateActionBy()) ||
                user.getUsername().equalsIgnoreCase(requestForwarder.getToApprover()))) {
            result = true;
        }
        return result;
    }

    @Override
    public RequestSearchDto searchRequestReviewerByCondition(RequestSearchRequest searchRequest, Pageable pageable) {
        Page<Request> requests = requestRepository.findAll(Specification.where(getRequestReviewerSpecificationByCondition(searchRequest)), pageable);
        int totalPage = requests.getTotalPages();
        long total = requests.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<ReviewerRequestDto> resultList = RequestMapper.INSTANCE.toReviewerRequestDtoList(requests.getContent(), timeZone);
        Random rand = new Random();

        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());


        resultList = resultList.stream()
                .peek(requestDto -> {

                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    requestDto.getRequestStatus().setCanEdit(false);
                    requestDto.getRequestStatus().setCanDelete(false);
                    requestDto.getRequestStatus().setCanDuplicate(false);
                    requestDto.getRequestStatus().setCanCancel(false);
                    requestDto.getRequestStatus().setCanCopyToPR(false);
                    requestDto.getRequestStatus().setCanAssignToMe(false);
                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
                    // Temporary mock data for Tags of BAY
                    // TODO: Cleanup this later
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        RequestType requestType = requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestType != null)
                            // requestDto.setTypeName(requestType.getTypeName());
                            // to next
                            requestDto.setTags(requestType.getTypeName());
                    }
                    this.setReviewerRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);

                        } else if(requestDto.getProjectCode() != null && requestDto.getProjectName() != null) {
                            OptionDto projectObj = new OptionDto();
                            projectObj.setValue(requestDto.getProjectCode());
                            projectObj.setName(requestDto.getProjectName());
                            projectObj.setLabel(requestDto.getProjectCode() + "-" + requestDto.getProjectName());
                            requestDto.setProjectObj(projectObj);
                        }

                        RequestAdditional requestAdditional = requestAdditionalRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestAdditional != null) {
                            requestDto.setBackground(requestAdditional.getBackground());
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }

                }).collect(Collectors.toList());

        RequestSearchDto requestSearchDto = new RequestSearchDto();
        requestSearchDto.setRequestDtoList(resultList);
        requestSearchDto.setTotal(total);
        requestSearchDto.setTotalPage(totalPage);
        requestSearchDto.setPageSize(searchRequest.getPageSize());
        return requestSearchDto;
    }

    @Override
    public RequestDeptApproverSearchDto searchRequestDeptApproverByCondition(RequestDeptApproverSearchRequest searchRequest, List<Long> requestId, Pageable pageable) {
        Page<Request> requests = requestRepository.findAll(Specification.where(getRequestDeptApproverSpecificationByCondition(searchRequest, requestId)), pageable);
        int totalPage = requests.getTotalPages();
        long total = requests.getTotalElements();
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        List<DeptApproverRequestDto> resultList = RequestMapper.INSTANCE.toDeptApproverRequestDtoList(requests.getContent(), timeZone);
        Random rand = new Random();

        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());


        resultList = resultList.stream()
                .peek(requestDto -> {

                    OptionDto organizationOption = organizationList.stream()
                            .filter(organization -> requestDto.getOrganizationId() != null &&
                                    organization.getValue().equals(requestDto.getOrganizationId().toString()))
                            .findFirst()
                            .orElse(null);
                    requestDto.setOrganizationObj(organizationOption);

                    requestDto.getRequestStatus().setCanEdit(false);
                    requestDto.getRequestStatus().setCanDelete(false);
                    requestDto.getRequestStatus().setCanDuplicate(false);
                    requestDto.getRequestStatus().setCanCancel(false);
                    requestDto.getRequestStatus().setCanCopyToPR(false);
                    requestDto.getRequestStatus().setCanAssignToMe(false);
                    requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
                    requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
                    requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
                    // Temporary mock data for Tags of BAY
                    // TODO: Cleanup this later
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        RequestType requestType = requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestType != null)
                            // requestDto.setTypeName(requestType.getTypeName());
                            // to next
                            requestDto.setTags(requestType.getTypeName());
                    }
                    this.setDeptApproverRequestDetailsFromRepositories(requestDto, requestDto.getRecId(), false);
                    if (Objects.requireNonNull(AppUtil.getTenantId()).toUpperCase().contains("BAY")) {
                        if (requestDto.getProjectObj() != null) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                        RequestAdditional requestAdditional = requestAdditionalRepository.findTop1ByRequestId(requestDto.getRecId()).orElse(null);
                        if (requestAdditional != null) {
                            requestDto.setBackground(requestAdditional.getBackground());
                        }
                    } else {
                        //TODO: Customize data for AIT
                        if (requestDto.getProjectObj() != null && requestDto.getTypeObj() != null &&
                                ((requestDto.getTypeObj().getValue().equals("1")) || (requestDto.getTypeObj().getValue().equals("2")))) {
                            requestDto.setProjectCode(requestDto.getProjectObj().getValue());
                            requestDto.setProjectName(requestDto.getProjectObj().getName());
                            requestDto.setProjectLabel(requestDto.getProjectObj() != null ? requestDto.getProjectObj().getValue() + "-" + requestDto.getProjectObj().getName() : requestDto.getProjectObj().getName());
                        } else if (requestDto.getDepartmentObj() != null && requestDto.getTypeObj() != null &&
                                requestDto.getTypeObj().getValue().equals("3")) {
                            String projectName = requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName()) > 0 ?
                                    requestDto.getDepartmentObj().getLabel().substring(requestDto.getDepartmentObj().getLabel().indexOf(requestDto.getDepartmentObj().getName())) : requestDto.getDepartmentObj().getLabel();
                            requestDto.setProjectCode(requestDto.getDepartmentObj().getName());
                            requestDto.setProjectName(projectName);
                            requestDto.setProjectLabel(requestDto.getDepartmentObj() != null ? requestDto.getDepartmentObj().getLabel() : null);
                        }
                    }

                }).collect(Collectors.toList());

        RequestDeptApproverSearchDto requestDeptApproverSearchDto = new RequestDeptApproverSearchDto();
        requestDeptApproverSearchDto.setRequestDtoList(resultList);
        requestDeptApproverSearchDto.setTotal(total);
        requestDeptApproverSearchDto.setTotalPage(totalPage);
        requestDeptApproverSearchDto.setPageSize(searchRequest.getPageSize());
        return requestDeptApproverSearchDto;
    }

    @Override
    public RequestDto getRequestByRequestNo(String requestNo, Integer tenant) {
        Request request = requestRepository.getRequestByRequestNo(requestNo, tenant);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        return RequestMapper.INSTANCE.toRequestDto(request, timeZone);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateRequestStatusByERFX(Request request) {
//        List<RequestItem> requestItemList = request.getRequestItemList();
//
//        List<Long> eRFXDocNums = requestItemList
//                .stream()
//                .map(RequestItem::getSourcingDocNo)
//                .filter(StringUtils::isNotEmpty)
//                .map(Long::parseLong)
//                .distinct()
//                .collect(Collectors.toList());
//
//        this.updateRequestStatusByERFX(request, eRFXDocNums, null);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateRequestStatusByERFX(Request request, List<Long> eRFXDocNums, String refreshToken) {
//        List<RequestItem> requestItemList = request.getRequestItemList();
//        if (!eRFXDocNums.isEmpty()) {
//            ERFXStatusRequest eRFXStatusRequest = ERFXStatusRequest.builder().erfxNum(eRFXDocNums).build();
//            ERFXStatusResponse response = this.getERFXStatusList(eRFXStatusRequest, refreshToken);
//
//            for (RequestItem requestItem : requestItemList) {
//                if (response != null &&
//                        requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                        requestItem.getSourcingDocNo() != null) {
//                    existingPriceItemService.getERFXSourcingStatus(response.getData(), requestItem);
//                }
//            }
//            requestItemRepository.saveAll(requestItemList);
//            requestStatusService.updateRequestAndApprovalStatus(requestItemList, request);
//        }
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateRequestStatusByERFX(List<Long> eRFXDocNums, List<Request> requestList, String refreshToken) {
//        List<RequestItem> requestItemList = requestItemRepository.getRequestItemsBySourcingDocNo(eRFXDocNums);
//        if (!eRFXDocNums.isEmpty()) {
//            ERFXStatusRequest eRFXStatusRequest = ERFXStatusRequest.builder().erfxNum(eRFXDocNums).build();
//            ERFXStatusResponse response = this.getERFXStatusList(eRFXStatusRequest, refreshToken);
//
//            for (RequestItem requestItem : requestItemList) {
//                if (response != null &&
//                        requestItem.getSourcingType().getRecId().equals(SOURCING_TYPE_ERFX.id()) &&
//                        requestItem.getSourcingDocNo() != null) {
//                    existingPriceItemService.getERFXSourcingStatus(response.getData(), requestItem);
//                }
//            }
//            requestItemRepository.saveAll(requestItemList);
//            requestStatusService.updateRequestAndApprovalStatus(requestItemList, requestList);
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void getTopNUpdatedERFXRequest(Tenant tenant, String maxItems, String refreshToken) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        List<Long> requestIdList = requestRepository.getTopNLatestERFXRequest(tenant.getRecId());
//        List<Request> requestList = requestRepository.findRequestByRecIdIn(requestIdList);
//        List<Request> selectedRequestList = new ArrayList<>();
//        List<Long> totalERFXDocNums = new ArrayList<>();
//        LOGGER.info(dtf.format(LocalDateTime.now()) + " Get TopN Update Sourcing Status (eRFX): Request Count -> " + requestList.size() );
//        for (Request request : requestList) {
//
//            List<Long> eRFXDocNums = request.getRequestItemList().stream()
//                    .map(RequestItem::getSourcingDocNo)
//                    .filter(StringUtils::isNotEmpty)
//                    .map(Long::parseLong)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            if(totalERFXDocNums.size() + eRFXDocNums.size() < Integer.parseInt(maxItems)) {
//                totalERFXDocNums.addAll(eRFXDocNums);
//            } else {
//                break;
//            }
//            selectedRequestList.add(request);
//
////            LOGGER.info(dtf.format(LocalDateTime.now()) + " Get TopN Update Sourcing Status (eRFX): eRFXDocNums -> " + eRFXDocNums + ", request -> " + request.getRequestNo() );
//
//        }
//        this.updateRequestStatusByERFX(totalERFXDocNums, selectedRequestList, refreshToken);
//    }

    @Override
    public List<DefaultApprovalDto> getDefaultApproval(String erfxid, Double amount) {
        List<RequestItem> requestItemList = requestItemRepository.getRequestItemBySourcingDocNo(erfxid);
        List<DefaultApprovalDto> responses = new ArrayList<>();
        int sequence = 0;
//        for (RequestItem requestItem : requestItemList) {
        Long requestId = requestItemList.get(0).getRequest().getRecId();
        Integer tenantId = requestItemList.get(0).getTenant().getRecId();

        //generate workflow
        Long eRFXworkflowTemplateId = Long.valueOf(tenantConfigService.getWorkflowTemplateIdForERFX(tenantId));
        WorkflowParamERFX workflowParamERFX = new WorkflowParamERFX();
        Request request = requestRepository.findRequestByRecId(requestId);
        String purchaserName = request.getAssignedBy() != null ? request.getAssignedBy() : null;


        boolean canForwardApprovalWorkflow = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
        if (canForwardApprovalWorkflow) {
            ForwardedApprover forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
            purchaserName = forwardedApprover.getForwardedApprover();

            // CASE FORWARDED WITH DELEGATED
            if (request.getAssignedBy() != null && request.getDelegateActionBy() != null &&
                    !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
                purchaserName = requestItemList.get(0).getUpdatedBy();
            }
        } else if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equals(request.getDelegateActionBy())) {
            purchaserName = request.getDelegateActionBy();
        }

        workflowParamERFX.setPurchaserName(purchaserName);
        workflowParamERFX.setAmount(amount);
        WorkflowInstanceData workflowInstance = null;

        try {
            Long workflowId = workFlowInstanceGenerator.generateWorkflowInstanceWithWorkflowParamERFX(workflowService, eRFXworkflowTemplateId, requestId.toString(), workflowParamERFX);
            workflowInstance = workflowService.getWorkflowApprovers(workflowId);
        } catch (Exception ex) {
            if (ex.getMessage().contains("E3001")) { //"Fail to create approval instance, no condition(s) met"
                //TODO : Populate message in case we have no Purchaser
            }
        }

        // set requester
        DefaultApprovalDto requester = new DefaultApprovalDto();
        requester.setSequence(sequence += 1);
        requester.setApprovalTypeId(2); //Type 2 = Requester SR,  Report Line (Approver)
        requester.setApprovalUserName(requestItemList.get(0).getRequest().getCreatedBy());
        responses.add(requester);
        // set report line
        List<EPAuthReviewerDto> reportLineList = requestReportLineService.getByRequest(requestId);
        for (EPAuthReviewerDto reportLineDto : reportLineList) {
            DefaultApprovalDto response = new DefaultApprovalDto();
            response.setSequence(sequence += 1);
            response.setApprovalTypeId(2); //Type 2 = Requester SR,  Report Line (Approver)
            response.setApprovalUserName(reportLineDto.getLoginId());
            responses.add(response);
        }
        // set purchaser follow workflow
        if (workflowInstance != null) {
            for (WorkflowInstanceStageDto stage : workflowInstance.getInstanceStages()) {
                for (WorkflowInstanceStepDto step : stage.getInstanceSteps()) {
                    for (WorkflowInstanceApproverDto approverDto : step.getInstanceApprovers()) {
                        DefaultApprovalDto approval = new DefaultApprovalDto();
                        approval.setSequence(sequence += 1);
                        approval.setApprovalTypeId(1); //Type 1 = Purchaser
                        approval.setApprovalUserName(approverDto.getReferApproverId());
                        responses.add(approval);
                    }
                }
            }
        }

        return responses;
    }

    @Override
    public boolean approveRequest(RequestDeptApprovalRequest deptApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Request request = requestRepository.findRequestsByRecId(deptApprovalRequest.getRequestId());
        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if(approver.isPresent()) {
            Optional<RequestApprover> requestApprover = requestApproverRepository.findRequestApproversByRequestAndApprover(request, approver.get());
            if(requestApprover.isPresent()) {
                RequestApprover requestApproverObj = requestApprover.get();
                if (requestDeptApproverService.hasApprovalPermission(requestApproverObj)) {
                    DeptApprovalStatus deptApprovalStatusApprovedObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
                    DeptApprovalStatus deptApprovalStatusAwaitingObj = requestApproverObj.getDeptApprovalStatus();

                    requestApproverObj.setComment(deptApprovalRequest.getReason());
                    requestApproverObj.setDeptApprovalStatus(deptApprovalStatusApprovedObj);
                    requestApproverObj.setUpdatedBy(AppUtil.getUserName());
                    requestApproverObj.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestApproverRepository.save(requestApproverObj);

                    // If it's the last approver,either set Request Status to AWAITING or update next approver status to AWAITING
                    DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_NONE.code());
                    List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusNoneObj);
                    if(requestApprovers != null & !requestApprovers.isEmpty()) {
                        // Update next approver status
                        requestApprovers.sort(Comparator.comparing(RequestApprover::getSequence));
                        requestApprovers.get(0).setDeptApprovalStatus(deptApprovalStatusAwaitingObj);
                    } else {
                        // Set Request Status to AWAITING
                        String requestStatus = TENANT_REQUEST_AWAITING.code();
                        TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(requestStatus, request.getTenant());
                        request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                        request.setRequestStatus(tenantRequestStatus);
                        request.setDeptApprovalStatus(deptApprovalStatusApprovedObj);
                        Integer approvalStatusId = APPROVAL_AWAITING.id();
                        request.setApprovalStatusId(approvalStatusId);

                        requestRepository.save(request);
                    }

                    // Save Request History : 2	SUBMIT	The request was successfully submitted
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_APPROVED.id());

                    // Prepare Email template data
                    RequestDto requestDtoForEmail = this.findRequestSourcingForSendMailByRecId(request.getRecId());

                    // send email notification
                    SendEMailRequest approveRequestEmail = new SendEMailRequest();
                    approveRequestEmail.setRequestId(request.getRecId());
                    approveRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_APPROVED);
                    approveRequestEmail.setActivity(ACTIVITY_APPROVED);
                    emailService.sendEMailNotification(approveRequestEmail, requestDtoForEmail);

                    SendEMailRequest reviewAndApproveRequestEmail = new SendEMailRequest();
                    reviewAndApproveRequestEmail.setRequestId(request.getRecId());
                    reviewAndApproveRequestEmail.setEmailActivity(EmailActivity.PLEASE_REVIEW_AND_APPROVE_SR);
                    reviewAndApproveRequestEmail.setActivity(ACTIVITY_APPROVED);
                    emailService.sendEMailNotification(reviewAndApproveRequestEmail, requestDtoForEmail);

//                    SendEMailRequest reviewRequestEmail = new SendEMailRequest();
//                    reviewRequestEmail.setRequestId(request.getRecId());
//                    reviewRequestEmail.setEmailActivity(EmailActivity.PLEASE_REVIEW_SR);
//                    reviewRequestEmail.setActivity(ACTIVITY_APPROVED);
//                    emailService.sendEMailNotification(reviewRequestEmail, requestDtoForEmail);

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean rejectRequest(RequestDeptApprovalRequest deptApprovalRequest) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Request request = requestRepository.findRequestsByRecId(deptApprovalRequest.getRequestId());
        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());

        if(approver.isPresent()) {
            Optional<RequestApprover> requestApprover = requestApproverRepository.findRequestApproversByRequestAndApprover(request, approver.get());
            if(requestApprover.isPresent()) {
                RequestApprover requestApproverObj = requestApprover.get();
                if (requestDeptApproverService.hasApprovalPermission(requestApproverObj)) {
                    DeptApprovalStatus deptApprovalStatusRejectObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_REJECTED.code());

                    requestApproverObj.setComment(deptApprovalRequest.getReason());
                    requestApproverObj.setDeptApprovalStatus(deptApprovalStatusRejectObj);
                    requestApproverObj.setUpdatedBy(AppUtil.getUserName());
                    requestApproverObj.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                    requestApproverRepository.save(requestApproverObj);

                    //Set remaining approvers status from NONE to be CANCELLED
                    DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_NONE.code());
                    DeptApprovalStatus deptApprovalStatusCancelledObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_CANCELLED.code());
                    List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusNoneObj);
                    if(requestApprovers != null & !requestApprovers.isEmpty()) {
                        for(RequestApprover reqApprover : requestApprovers) {
                            reqApprover.setDeptApprovalStatus(deptApprovalStatusCancelledObj);
                            reqApprover.setUpdatedBy(AppUtil.getUserName());
                            reqApprover.setUpdatedDate(DateTimeUtil.getTimestampUTC());
                        }
                        requestApproverRepository.saveAll(requestApprovers);
                    }

                    // Save Request History : 2	SUBMIT	The request was successfully submitted
                    requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_REJECTED.id());

                    // Set Request status to REJECTED
                    String requestStatus = TENANT_REQUEST_REJECTED.code();
                    TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(requestStatus, request.getTenant());
                    request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
                    request.setRequestStatus(tenantRequestStatus);
                    request.setDeptApprovalStatus(deptApprovalStatusRejectObj);
                    TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(TENANT_APPROVAL_CANCELLED.code(), request.getTenant());
                    request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
                    request.setApprovalStatus(tenantApprovalStatus);

                    requestRepository.save(request);

                    // send email notification
                    SendEMailRequest rejectedRequestEmail = new SendEMailRequest();
                    rejectedRequestEmail.setRequestId(request.getRecId());
                    rejectedRequestEmail.setEmailActivity(EmailActivity.SR_HAS_BEEN_REJECTED);
                    rejectedRequestEmail.setActivity(ACTIVITY_REJECTED);
                    RequestDto requestDtoForEmail = this.findRequestSourcingForSendMailByRecId(request.getRecId());
                    emailService.sendEMailNotification(rejectedRequestEmail, requestDtoForEmail);

                    return true;
                }
            }
        }
        return false;
    }

    private RequestDto findRequestSourcingForSendMailByRecId(Long recId) {
        return prepareRequestSourcingByRecId(recId);
    }

    private RequestDto setRequestDetailsFromRepositories(RequestDto requestDto, Long recId, String tenantCode) {
        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }
        requestDto.setRequestTypeObj(requestTypeObj);
        requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
        requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
        requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
        requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));

        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);

        } else {
            if(requestDto.getProjectCode() != null && requestDto.getProjectName() != null) {
                OptionDto projectObj = new OptionDto();
                projectObj.setValue(requestDto.getProjectCode());
                projectObj.setName(requestDto.getProjectName());
                projectObj.setLabel(requestDto.getProjectCode() + "-" + requestDto.getProjectName());
                requestDto.setProjectObj(projectObj);
            }
            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));
        }

        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));
        requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());

        OptionDto organizationOption = organizationList.stream()
                .filter(organization -> requestDto.getOrganizationId() != null &&
                        organization.getValue().equals(requestDto.getOrganizationId().toString()))
                .findFirst()
                .orElse(null);

        requestDto.setOrganizationObj(organizationOption);
        Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
        LocationDto locationDto;
        if (requestLocation.isPresent()) {
            locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
            requestDto.setDeliveryLocation(locationDto);
            requestDto.setLocation(requestLocation.get().getDeliveryLocation());
            requestDto.setContactName(requestLocation.get().getContactName());
            requestDto.setContactPhone(requestLocation.get().getPhone());

        }
        Tenant tenant = tenantService.findByCode(tenantId);
        Map<String, String> buyerFullName = new HashMap<>();
        if (null != tenant && requestDto.getTypeObj() != null) {
            List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantIdAndTypeId(tenant.getRecId(), Integer.parseInt(requestDto.getTypeObj().getValue()), "");

            List<String> buyerNames = subCategories.stream().map(TenantSubCategory::getBuyer).distinct().collect(Collectors.toList());
            for (String buyer : buyerNames) {
                if (buyer != null && !buyer.trim().isEmpty()) {
                    buyerFullName.put(buyer, UserDetailServiceUtil.getFullName(buyer));
                }
            }
        }

        for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
            requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
            requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));
            Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
            if (requestItemSubCategoryOptional.isPresent()) {
                TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                if (null != tenantSubCategoryDto) {
                    requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                    requestItemDto.setDisplayPurchaser(null != buyerFullName.get(tenantSubCategoryDto.getBuyer()) ? buyerFullName.get(tenantSubCategoryDto.getBuyer()) : "");
                    requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                    requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                }
            }
            RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
            if (requestItemLocationDto != null) {

                Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                LocationDto deliveryLocationDto;
                if (requestItemLocation.isPresent()) {
                    deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                    requestItemDto.setDeliveryLocation(deliveryLocationDto);
                }

                requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                requestItemDto.setContactName(requestItemLocationDto.getContactName());
                requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
            }

            if(requestItemDto.getSourcingStatus() != null) {
                // In case Purchaser perform request editing, do not allow to delete request item.
                if (AppUtil.isPurchaser() && !requestDto.getRequestStatus().getName().equalsIgnoreCase(TENANT_REQUEST_DRAFT.code())) {
                    requestItemDto.getSourcingStatus().setCanDelete(false);
                }
            }
        }

        // Sort Item by ItemSequence
        requestDto.getRequestItemList().sort(Comparator.comparing(RequestItemV2Dto::getItemSequence));

        // set questionnaire
        FormDTO formDTO = this.setQuestionnaire(recId);
        requestDto.setFormId(formDTO.getFormId());
        requestDto.setQuestionnaire(formDTO);

        return requestDto;
    }

    private FormDTO setQuestionnaire(Long requestId) {
        FormDTO formDto = new FormDTO();
        //        try {
        Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(requestId);
        if (requestQuestionnaireOptional.isPresent()) {
            RequestQuestionnaire questionnaire = requestQuestionnaireOptional.get();
            EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(questionnaire.getFormId());

//            BeanUtils.copyProperties(questionnaire, formDto);
//            formDto.setCreatedDate(questionnaire.getFormCreatedDate());
//            formDto.setUpdatedDate(questionnaire.getFormUpdatedDate());
//            if (!StringUtils.isEmpty(questionnaire.getFormCreatedBy())) {
//                formDto.setCreatedBy(questionnaire.getFormCreatedBy());
//            } else if (!StringUtils.isEmpty(questionnaire.getCreatedBy())) {
//                formDto.setCreatedBy(UserDetailServiceUtil.getFullName(questionnaire.getCreatedBy()));
//            } else {
//                formDto.setCreatedBy("");
//            }
//            if (!StringUtils.isEmpty(questionnaire.getFormUpdatedBy())) {
//                formDto.setUpdatedBy(questionnaire.getFormUpdatedBy());
//            } else if (!StringUtils.isEmpty(questionnaire.getUpdatedBy())) {
//                formDto.setUpdatedBy(UserDetailServiceUtil.getFullName(questionnaire.getUpdatedBy()));
//            } else {
//                formDto.setUpdatedBy("");
//            }
//
//            List<FormFieldDTO> requestQuestionnaireFormFields = requestQuestionnaireFormFieldRepository.findByRequestQuestionnaireId(questionnaire.getRecId())
//                    .stream().map(i -> {
//                        FormFieldDTO formFieldDTO = new FormFieldDTO();
//                        BeanUtils.copyProperties(i, formFieldDTO);
//                        formFieldDTO.setRequestQuestionnaireFormFieldId(i.getRecId());
//                        return formFieldDTO;
//                    }).collect(Collectors.toList());
//
//            List<FormSectionDTO> formSectionDTOS = new ArrayList<>();
//            requestQuestionnaireFormFields.forEach(it -> {
//                List<FormOptionChoiceDTO> optionChoiceDTOList = requestQuestionnaireFormFieldOptionChoiceRepository.findByRequestQuestionnaireFormFieldId(it.getRequestQuestionnaireFormFieldId())
//                        .stream().map(x -> {
//                            FormOptionChoiceDTO formOptionChoiceDTO = new FormOptionChoiceDTO();
//                            BeanUtils.copyProperties(x, formOptionChoiceDTO);
//                            return formOptionChoiceDTO;
//                        }).collect(Collectors.toList());
//
//                it.setOptionChoices(optionChoiceDTOList);
//
//                List<FormFieldDTO> listFormField = new ArrayList<>();
//                listFormField.add(it);
//
//                FormSectionDTO formSectionDTO = new FormSectionDTO();
//                formSectionDTO.setFormFields(listFormField);
//                formSectionDTOS.add(formSectionDTO);
//            });
//
//            FormTabDTO formTabDTO = new FormTabDTO();
//            formTabDTO.setFormSections(formSectionDTOS);
//
//            List<FormTabDTO> formTabDTOS = new ArrayList<>();
//            formTabDTOS.add(formTabDTO);
//            formDto.setFormTabs(formTabDTOS);
            formDto = eFormViewResponse.getData();

        }
        return formDto;
    }

    private RequesterRequestDto setRequesterRequestDetailsFromRepositories(RequesterRequestDto requestDto, Long recId, String tenantCode, boolean isRequiredItem) {

        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }
        requestDto.setRequestTypeObj(requestTypeObj);
        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);
        }
        else {
            OptionDto projectObj = new OptionDto();
            projectObj.setValue(requestDto.getProjectCode());
            projectObj.setName(requestDto.getProjectName());
            projectObj.setLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            requestDto.setProjectObj(projectObj);

            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));

        }
        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));

        if (isRequiredItem) {

            requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
            requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
            requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));
            requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));

            Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
            LocationDto locationDto;
            if (requestLocation.isPresent()) {
                locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
                requestDto.setDeliveryLocation(locationDto);
                requestDto.setLocation(requestLocation.get().getDeliveryLocation());
                requestDto.setContactName(requestLocation.get().getContactName());
                requestDto.setContactPhone(requestLocation.get().getPhone());

            }

            Tenant tenant = tenantService.findByCode(tenantCode);
            Map<String, String> buyerFullName = new HashMap<>();
            if (null != tenant) {
                if (requestDto.getTypeObj() != null) {
                    List<TenantSubCategory> subCategories = tenantSubCategoryRepository.getSubcategoryByTenantIdAndTypeId(tenant.getRecId(), Integer.parseInt(requestDto.getTypeObj().getValue()), "");
                    if (subCategories != null) {
                        List<String> buyerNames = subCategories.stream().map(TenantSubCategory::getBuyer).distinct().collect(Collectors.toList());
                        for (String buyer : buyerNames) {
                            if (buyer != null && !buyer.trim().isEmpty()) {
                                buyerFullName.put(buyer, UserDetailServiceUtil.getFullName(buyer));
                            }
                        }
                    }
                }
            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
//            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
//            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));

                Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                if (requestItemSubCategoryOptional.isPresent()) {
                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                    if (null != tenantSubCategoryDto) {
                        requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                        requestItemDto.setDisplayPurchaser(null != buyerFullName.get(tenantSubCategoryDto.getBuyer()) ? buyerFullName.get(tenantSubCategoryDto.getBuyer()) : "");
                        requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                        requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                    }
                }
                RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
                if (requestItemLocationDto != null) {

                    Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                    LocationDto deliveryLocationDto;
                    if (requestItemLocation.isPresent()) {
                        deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                        requestItemDto.setDeliveryLocation(deliveryLocationDto);
                    }

                    requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                    requestItemDto.setContactName(requestItemLocationDto.getContactName());
                    requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
                }
            }

            if (!requestDto.getRequestItemList().isEmpty()) {
                requestDto.setPurchaser(requestDto.getRequestItemList().get(0).getPurchaser());
            }

//            try {
//                Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(recId);
//                if (requestQuestionnaireOptional.isPresent()) {
//                    Long formId = requestQuestionnaireOptional.get().getFormId();
//                    requestDto.setFormId(formId);
//                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(formId);
//                    if (null != eFormViewResponse) {
//                        requestDto.setQuestionnaire(eFormViewResponse.getData());
//                    }
//                }
//            } catch (Exception e) {
//                log.error("[ERROR] call eform service : {}", e.getMessage());
//            }

            // set questionnaire
            FormDTO formDTO = this.setQuestionnaire(recId);
            requestDto.setFormId(formDTO.getFormId());
            requestDto.setQuestionnaire(formDTO);

            // Sort Item by ItemSequence
            requestDto.getRequestItemList().sort(Comparator.comparing(RequestItemV2Dto::getItemSequence));
        }

        return requestDto;
    }

    private ReviewerRequestDto setReviewerRequestDetailsFromRepositories(ReviewerRequestDto requestDto, Long recId, boolean isRequiredItem) {

        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }
        requestDto.setRequestTypeObj(requestTypeObj);
        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);
        }
        else {
            OptionDto projectObj = new OptionDto();
            projectObj.setValue(requestDto.getProjectCode());
            projectObj.setName(requestDto.getProjectName());
            projectObj.setLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            requestDto.setProjectObj(projectObj);

            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));

        }
//        requestDto.setProjectObj(mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto));

        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));

        if (isRequiredItem) {
            requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
            requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
            requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));
            requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));

            Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
            LocationDto locationDto;
            if (requestLocation.isPresent()) {
                locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
                requestDto.setDeliveryLocation(locationDto);
                requestDto.setLocation(requestLocation.get().getDeliveryLocation());
                requestDto.setContactName(requestLocation.get().getContactName());
                requestDto.setContactPhone(requestLocation.get().getPhone());

            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
                //            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
                //            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));

                Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                if (requestItemSubCategoryOptional.isPresent()) {
                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                    if (null != tenantSubCategoryDto) {
                        requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                        requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                        requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                    }
                }

                RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
                if (requestItemLocationDto != null) {

                    Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                    LocationDto deliveryLocationDto;
                    if (requestItemLocation.isPresent()) {
                        deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                        requestItemDto.setDeliveryLocation(deliveryLocationDto);
                    }

                    requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                    requestItemDto.setContactName(requestItemLocationDto.getContactName());
                    requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
                }
            }

            try {
                Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(recId);
                if (requestQuestionnaireOptional.isPresent()) {
                    Long formId = requestQuestionnaireOptional.get().getFormId();
                    requestDto.setFormId(formId);
                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(formId);
                    if (null != eFormViewResponse) {
                        String createdBy = eFormViewResponse.getData().getCreatedBy();
                        String updatedBy = eFormViewResponse.getData().getUpdatedBy();
                        eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                        eFormViewResponse.getData().setUpdatedBy(UserDetailServiceUtil.getFullName(updatedBy));
                        requestDto.setQuestionnaire(eFormViewResponse.getData());
                    }
                }
            } catch (Exception e) {
                log.error("[ERROR] call eform service : {}", e.getMessage());
            }
        }

        return requestDto;
    }

    private DeptApproverRequestDto setDeptApproverRequestDetailsFromRepositories(DeptApproverRequestDto requestDto, Long recId, boolean isRequiredItem) {

        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }
        String username = AppUtil.getUserName();
        Optional<DeptApprovalStatusDto> userDeptApprovalStatus = requestDto.getRequestDeptApproverDtoList()
                .stream()
                .filter(i -> username.equalsIgnoreCase(i.getApproverDto().getLoginId()))
                .map(RequestDeptApproverDto::getDeptApprovalStatus)
                .map(i -> DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(i))
                .findFirst();
        userDeptApprovalStatus.ifPresent(requestDto::setDeptApprovalStatus);
        requestDto.setRequestTypeObj(requestTypeObj);
        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);
        }
        else {
            OptionDto projectObj = new OptionDto();
            projectObj.setValue(requestDto.getProjectCode());
            projectObj.setName(requestDto.getProjectName());
            projectObj.setLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            requestDto.setProjectObj(projectObj);

            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));

        }
//        requestDto.setProjectObj(mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto));

        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));

        if (isRequiredItem) {
            requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
            requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
            requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));
            requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));

            Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
            LocationDto locationDto;
            if (requestLocation.isPresent()) {
                locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
                requestDto.setDeliveryLocation(locationDto);
                requestDto.setLocation(requestLocation.get().getDeliveryLocation());
                requestDto.setContactName(requestLocation.get().getContactName());
                requestDto.setContactPhone(requestLocation.get().getPhone());

            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
                //            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
                //            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));

                Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                if (requestItemSubCategoryOptional.isPresent()) {
                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                    if (null != tenantSubCategoryDto) {
                        requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                        requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                        requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                    }
                }

                RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
                if (requestItemLocationDto != null) {

                    Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                    LocationDto deliveryLocationDto;
                    if (requestItemLocation.isPresent()) {
                        deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                        requestItemDto.setDeliveryLocation(deliveryLocationDto);
                    }

                    requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                    requestItemDto.setContactName(requestItemLocationDto.getContactName());
                    requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
                }
            }

            try {
                Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(recId);
                if (requestQuestionnaireOptional.isPresent()) {
                    Long formId = requestQuestionnaireOptional.get().getFormId();
                    requestDto.setFormId(formId);
                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(formId);
                    if (null != eFormViewResponse) {
                        String createdBy = eFormViewResponse.getData().getCreatedBy();
                        String updatedBy = eFormViewResponse.getData().getUpdatedBy();
                        eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                        eFormViewResponse.getData().setUpdatedBy(UserDetailServiceUtil.getFullName(updatedBy));
                        requestDto.setQuestionnaire(eFormViewResponse.getData());
                    }
                }
            } catch (Exception e) {
                log.error("[ERROR] call eform service : {}", e.getMessage());
            }
        }

        return requestDto;
    }

    private SourcingApproverRequestDto setSourcingApproverRequestDetailsFromRepositories(SourcingApproverRequestDto requestDto, Long recId, boolean isRequiredItem) {

        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }
        String username = AppUtil.getUserName();
        Optional<DeptApprovalStatusDto> userDeptApprovalStatus = requestDto.getRequestDeptApproverDtoList()
                .stream()
                .filter(i -> username.equalsIgnoreCase(i.getApproverDto().getLoginId()))
                .map(RequestDeptApproverDto::getDeptApprovalStatus)
                .map(i -> DeptApprovalStatusMapper.INSTANCE.toDeptApprovalStatusDto(i))
                .findFirst();
        userDeptApprovalStatus.ifPresent(requestDto::setDeptApprovalStatus);
        requestDto.setRequestTypeObj(requestTypeObj);
        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);
        }
        else {
            OptionDto projectObj = new OptionDto();
            projectObj.setValue(requestDto.getProjectCode());
            projectObj.setName(requestDto.getProjectName());
            projectObj.setLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            requestDto.setProjectObj(projectObj);

            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));

        }
//        requestDto.setProjectObj(mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto));

        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));

        if (isRequiredItem) {
            requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
            requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
            requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));
            requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));

            Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
            LocationDto locationDto;
            if (requestLocation.isPresent()) {
                locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
                requestDto.setDeliveryLocation(locationDto);
                requestDto.setLocation(requestLocation.get().getDeliveryLocation());
                requestDto.setContactName(requestLocation.get().getContactName());
                requestDto.setContactPhone(requestLocation.get().getPhone());

            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
                //            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
                //            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
                requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));

                Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                if (requestItemSubCategoryOptional.isPresent()) {
                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                    if (null != tenantSubCategoryDto) {
                        requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                        requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                        requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                    }
                }

                RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
                if (requestItemLocationDto != null) {

                    Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                    LocationDto deliveryLocationDto;
                    if (requestItemLocation.isPresent()) {
                        deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                        requestItemDto.setDeliveryLocation(deliveryLocationDto);
                    }

                    requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                    requestItemDto.setContactName(requestItemLocationDto.getContactName());
                    requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
                }
            }

            try {
                Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(recId);
                if (requestQuestionnaireOptional.isPresent()) {
                    Long formId = requestQuestionnaireOptional.get().getFormId();
                    requestDto.setFormId(formId);
                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(formId);
                    if (null != eFormViewResponse) {
                        String createdBy = eFormViewResponse.getData().getCreatedBy();
                        String updatedBy = eFormViewResponse.getData().getUpdatedBy();
                        eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                        eFormViewResponse.getData().setUpdatedBy(UserDetailServiceUtil.getFullName(updatedBy));
                        requestDto.setQuestionnaire(eFormViewResponse.getData());
                    }
                }
            } catch (Exception e) {
                log.error("[ERROR] call eform service : {}", e.getMessage());
            }
        }

        return requestDto;
    }

    private ApproverRequestDto setApproverRequestDetailsFromRepositories(ApproverRequestDto requestDto, Long recId, boolean isRequiredItem) {

        Integer requestTypeId = requestDto.getRequestTypeId();
        OptionDto requestTypeObj = null;
        if (requestTypeId != null) {
            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_QUANTITY.id().toString(),
                        REQUEST_TYPE_QUANTITY.code(),
                        REQUEST_TYPE_QUANTITY.description(),
                        false
                );
            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
                requestTypeObj = new OptionDto(
                        REQUEST_TYPE_CONDITION.id().toString(),
                        REQUEST_TYPE_CONDITION.code(),
                        REQUEST_TYPE_CONDITION.description(),
                        false
                );
            }
        }

        String username = AppUtil.getUserName();
        Optional<DeptApprovalStatusDto> userDeptApprovalStatus = Optional.ofNullable(requestDto.getRequestDeptApproverDtoList())
                .orElse(Collections.emptyList())
                .stream()
                .filter(i -> i != null && i.getApproverDto() != null && username.equalsIgnoreCase(i.getApproverDto().getLoginId()))
                .map(RequestDeptApproverDto::getDeptApprovalStatus)
                .filter(Objects::nonNull)
                .map(DeptApprovalStatusMapper.INSTANCE::toDeptApprovalStatusDto)
                .findFirst();
        userDeptApprovalStatus.ifPresent(requestDto::setDeptApprovalStatus);
        requestDto.setRequestTypeObj(requestTypeObj);
        if (requestDto.getTenant().getRecId() == 1) {
            OptionDto projectObj = mapOptionalToOptionDto(requestProjectRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toProjectOptionDto);
            requestDto.setProjectObj(projectObj);

            OptionDto departmentObj = mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto);
            requestDto.setDepartmentObj(departmentObj);
        }
        else {
            OptionDto projectObj = new OptionDto();
            projectObj.setValue(requestDto.getProjectCode());
            projectObj.setName(requestDto.getProjectName());
            projectObj.setLabel(requestDto.getProjectCode() != null ? requestDto.getProjectCode() + "-" + requestDto.getProjectName() : requestDto.getProjectName());
            requestDto.setProjectObj(projectObj);

            requestDto.setDepartmentObj(mapOptionalToOptionDto(requestDepartmentRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toDepartmentOptionDto));

        }
        requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toTypeOptionDto));

        if (isRequiredItem) {
            requestDto.setObjectiveObj(mapOptionalToOptionDto(requestObjectiveRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toObjectiveOptionDto));
            requestDto.setCategoryObj(mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
            requestDto.setSubCategoryObj(mapOptionalToOptionDto(requestSubCategoryRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));
            requestDto.setPurchaserObj(mapOptionalToOptionDto(requestPurchaserRepository.findLatestPurchaserWithoutAnyPermission(recId), OptionDtoMapper.INSTANCE::toPurchaserOptionDto));
            requestDto.setBudgetRefNoObj(mapOptionalToOptionDto(requestBudgetRefNoRepository.findTop1ByRequestId(recId), OptionDtoMapper.INSTANCE::toBudgetRefNoOptionDto));
            Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(recId);
            LocationDto locationDto;

            if (requestLocation.isPresent()) {
                locationDto = LocationMapper.INSTANCE.toLocationDto(requestLocation.get().getLocation());
                requestDto.setDeliveryLocation(locationDto);
                requestDto.setLocation(requestLocation.get().getDeliveryLocation());
                requestDto.setContactName(requestLocation.get().getContactName());
                requestDto.setContactPhone(requestLocation.get().getPhone());

            }

            for (RequestItemV2Dto requestItemDto : requestDto.getRequestItemList()) {
                requestItemDto.setPurposeObj(mapOptionalToOptionDto(requestItemPurposeRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toPurposeOptionDto));
//            requestItemDto.setCategoryObj(mapOptionalToOptionDto(requestItemCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto));
//            requestItemDto.setSubCategoryObj(mapOptionalToOptionDto(requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toSubCategoryOptionDto));

                //existingPriceItemService.getItemByRequestV2()
                Optional<ExistingPriceItem> existingPriceItem = existingPriceItemRepository.findExistingPriceItemByRequestItemIdAndNullableSourcingDocNo(requestItemDto.getRecId(), requestItemDto.getSourcingDocNo());
                if(existingPriceItem.isPresent()) {
                    requestItemDto.setItemName(existingPriceItem.get().getItemName());
                    requestItemDto.setItemDescription(existingPriceItem.get().getItemDescription());
                    requestItemDto.setCurrencyObj(mapOptionalToOptionDto(currencyRepository.findCurrencyByRecId(existingPriceItem.get().getCurrency().getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));
                } else {
                    requestItemDto.setCurrencyObj(mapOptionalToOptionDto(requestItemCurrencyRepository.findTop1ByRequestItemId(requestItemDto.getRecId()), OptionDtoMapper.INSTANCE::toCurrencyOptionDto));
                }
//                if(requestItemDto.getV)
//                OptionDto.builder()
//                        .value(VatType.INCLUDED_VAT.value().toString())
//                        .name(VatType.INCLUDED_VAT.name())
//                        .label(VatType.INCLUDED_VAT.description()).build(),
//                        OptionDto.builder()
//                                .value(VatType.EXCLUDED_VAT.value().toString())
//                                .name(VatType.EXCLUDED_VAT.name())
//                                .label(VatType.EXCLUDED_VAT.description()).build()
//
//                requestItemDto.setVatTypeObj(mapOptionalToOptionDto());



                Optional<RequestItemSubCategory> requestItemSubCategoryOptional = requestItemSubCategoryRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                if (requestItemSubCategoryOptional.isPresent()) {
                    TenantSubCategoryDto tenantSubCategoryDto = tenantSubCategoryService.findById(requestItemSubCategoryOptional.get().getSubCategory().getId());
                    if (null != tenantSubCategoryDto) {
                        requestItemDto.setPurchaser(tenantSubCategoryDto.getBuyer());
                        requestItemDto.setSubCategoryObj(tenantSubCategoryDto);
                        requestItemSubCategoryOptional.ifPresent(requestItemSubCategory -> requestItemDto.setCategoryObj(mapOptionalToOptionDto(Optional.of(requestItemSubCategory.getSubCategory().getCategory()), OptionDtoMapper.INSTANCE::toTenantCategoryOptionDto)));
                    }
                }

                RequestItemLocationDto requestItemLocationDto = requestItemDto.getRequestItemLocationList().stream().findFirst().orElse(null);
                if (requestItemLocationDto != null) {

                    Optional<RequestItemLocation> requestItemLocation = requestItemLocationRepository.findTop1ByRequestItemId(requestItemDto.getRecId());
                    LocationDto deliveryLocationDto;
                    if (requestItemLocation.isPresent()) {
                        deliveryLocationDto = LocationMapper.INSTANCE.toLocationDto(requestItemLocation.get().getLocation());
                        requestItemDto.setDeliveryLocation(deliveryLocationDto);
                    }

                    requestItemDto.setLocation(requestItemLocationDto.getDeliveryLocation());
                    requestItemDto.setContactName(requestItemLocationDto.getContactName());
                    requestItemDto.setContactPhone(requestItemLocationDto.getPhone());
                }

                if(requestItemDto.getSourcingStatus() != null) {
                    // In case Purchaser view their request, do not allow to edit request item.
                    if(AppUtil.isPurchaser()) {
                        requestItemDto.getSourcingStatus().setCanEdit(false);
                    }
                }
            }

            try {
                Optional<RequestQuestionnaire> requestQuestionnaireOptional = requestQuestionnaireRepository.findByRequestId(recId);
                if (requestQuestionnaireOptional.isPresent()) {
                    Long formId = requestQuestionnaireOptional.get().getFormId();
                    requestDto.setFormId(formId);
                    EFormViewResponse eFormViewResponse = eFormService.viewQuestionnaire(formId);
                    if (null != eFormViewResponse) {
                        String createdBy = eFormViewResponse.getData().getCreatedBy();
                        String updatedBy = eFormViewResponse.getData().getUpdatedBy();
                        eFormViewResponse.getData().setCreatedBy(UserDetailServiceUtil.getFullName(createdBy));
                        eFormViewResponse.getData().setUpdatedBy(UserDetailServiceUtil.getFullName(updatedBy));
                        requestDto.setQuestionnaire(eFormViewResponse.getData());
                    }
                }
            } catch (Exception e) {
                log.error("[ERROR] call eform service : {}", e.getMessage());
            }
        }

        return requestDto;
    }

    private InstanceApproverHeaderDto getDeptApproverHeaders(Request request) {
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();
        approverSection.setSectionName("Approver");
        String deptApproverSectionLabel = tenantConfigService.getDeptApproverSectionLabel(tenant.getRecId());
        approverSection.setSectionLabel(deptApproverSectionLabel);

        List<InstanceApproverDto> instanceApprovers = new ArrayList<>();

        if(request.getRequestDeptApproverList() != null) {
            approverSection.setNumberOfApproverRequired(request.getRequestDeptApproverList().size());
            for(RequestApprover requestApprover : request.getRequestDeptApproverList()) {
                InstanceApproverDto instanceApproverDto = this.getInstanceApproverDto(requestApprover);
                instanceApprovers.add(instanceApproverDto);
            }
        }

        approverSection.setApprovers(instanceApprovers);

        Optional<InstanceApproverDto> instanceApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED") ||
                        item.getStatus().equalsIgnoreCase("REJECTED") ||
                        item.getStatus().equalsIgnoreCase("AWAITING"))
                .findFirst();

        if (instanceApproverDto.isPresent()) {
            approverSection.setStatus(instanceApproverDto.get().getStatus());
        } else {
            approverSection.setStatus("PENDING");
        }
        approverSections.add(approverSection);

        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("APPROVER GROUP");
        approverHeader.setApproverSections(approverSections);

        return approverHeader;
    }

    @Override
    public InstanceApproverHeaderDto getPurchaserHeaders(String tenantId, String idp, Request request) {
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        approverSection.setSectionName("Purchaser");
        String purchaserSectionLabel = tenantConfigService.getPurchaserSectionLabel(tenant.getRecId());
        approverSection.setSectionLabel(purchaserSectionLabel);
        approverSection.setNumberOfApproverRequired(request.getRequestPurchaserList() != null ? request.getRequestPurchaserList().size() : 0);

        List<InstanceApproverDto> instanceApprovers = new ArrayList<>();


        if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {
            instanceApprovers = this.getApprovers(tenantId, idp, request);

            boolean isApproved = instanceApprovers.stream()
                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("APPROVED"));

            boolean isRejected = instanceApprovers.stream()
                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("REJECTED"));

            boolean isCancelled = !isRejected & instanceApprovers.stream()
                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("CANCELLED"));

            if (isApproved) {
                instanceApprovers = instanceApprovers.stream()
                        .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED"))
                        .collect(Collectors.toList());
            } else if (isRejected) {
                instanceApprovers = instanceApprovers.stream()
                        .filter(item -> item.getStatus().equalsIgnoreCase("REJECTED"))
                        .collect(Collectors.toList());
            } else if (isCancelled) {
                instanceApprovers = instanceApprovers.stream()
                        .filter(item -> item.getStatus().equalsIgnoreCase("CANCELLED"))
                        .collect(Collectors.toList());
            }
        } else {

            List<RequestPurchaser> requestPurchaserList = requestPurchaserService.getRequestPurchaserByRequest(request);
            for(RequestPurchaser requestPurchaser : requestPurchaserList) {
                InstanceApproverDto instanceApproverDto = getInstanceApproverDto(requestPurchaser, request);
                instanceApprovers.add(instanceApproverDto);
            }
        }

        approverSection.setApprovers(instanceApprovers);

        Optional<InstanceApproverDto> instanceApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED") ||
                        item.getStatus().equalsIgnoreCase("REJECTED") ||
                        item.getStatus().equalsIgnoreCase("CANCELLED") ||
                        item.getStatus().equalsIgnoreCase("FORWARDED"))
                .findFirst();//.orElse(null);

        Optional<InstanceApproverDto> instanceAwaitingApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("AWAITING"))
                .findFirst();

        if (instanceApproverDto.isPresent()) {
            approverSection.setStatus(instanceApproverDto.get().getStatus());
        } else if (instanceAwaitingApproverDto.isPresent()) {
            approverSection.setStatus("AWAITING");
        } else {
            approverSection.setStatus("PENDING");
        }
        approverSections.add(approverSection);
        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("PURCHASER GROUP");
        approverHeader.setApproverSections(approverSections);

        List<InstanceApproverDto> approvers = approverSections.get(0).getApprovers();
        if(approvers != null && !approvers.isEmpty()) {

            InstanceApproverDto AssignedPurchaser = approvers.get(0);
            InstanceApproverDto forwardedPurchaser = null;
            InstanceApproverDto delegatedPurchaser = null;

            boolean canForwardApprovalWorkflow = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
            if (canForwardApprovalWorkflow) {
                ForwardedApprover forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
                log.info("Forward Approver : {}", forwardedApprover);
                ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, forwardedApprover.getForwardedApprover(), new HashMap<>());


                if (contractDetail != null) {
                    forwardedPurchaser = new InstanceApproverDto();
                    forwardedPurchaser.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                    forwardedPurchaser.setLoginId(contractDetail.getUsername());
                    forwardedPurchaser.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                    forwardedPurchaser.setEmail(contractDetail.getEmail());
                    forwardedPurchaser.setMobilePhone(contractDetail.getMobilePhone());
                    forwardedPurchaser.setPhone(contractDetail.getPhone());
                    // Set lasted status
                    forwardedPurchaser.setStatus(!approverSections.get(0).getStatus().isEmpty()
                            ? approverSections.get(0).getStatus()
                            : "AWAITING");
                    forwardedPurchaser.setRequired(true);
                    forwardedPurchaser.setComment(AssignedPurchaser.getComment());
                    approvers.add(forwardedPurchaser);
                    AssignedPurchaser.setComment(null);
                }
                AssignedPurchaser.setStatus("FORWARED");
                AssignedPurchaser.setForwardedDate(forwardedApprover.getForwaredDate());

                // CASE FORWARDED WITH DELEGATED
                if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
                    ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
                    ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());

                    if (contractDetailDelegatee != null) {
                        delegatedPurchaser = new InstanceApproverDto();
                        delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
                        delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
                        delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
                        delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
                        delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
                        delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());

                        if (forwardedPurchaser != null) {
                            delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
                            delegatedPurchaser.setComment(forwardedPurchaser.getComment());
                        } else {
                            delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
                            delegatedPurchaser.setComment(AssignedPurchaser.getComment());
                        }
                        delegatedPurchaser.setRequired(true);
                        delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
                        approvers.add(delegatedPurchaser);
                    }
                    if (forwardedPurchaser != null) {
                        forwardedPurchaser.setStatus("DELEGATED");
                        forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                        forwardedPurchaser.setComment(null);
                    } else {
                        AssignedPurchaser.setStatus("DELEGATED");
                        AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                        AssignedPurchaser.setComment(null);
                    }

                }
            } else if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
                ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
                ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());

                if (contractDetailDelegatee != null) {
                    delegatedPurchaser = new InstanceApproverDto();
                    delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
                    delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
                    delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
                    delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
                    delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
                    delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());

                    if (forwardedPurchaser != null) {
                        delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
                        delegatedPurchaser.setComment(forwardedPurchaser.getComment());
                    } else {
                        delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
                        delegatedPurchaser.setComment(AssignedPurchaser.getComment());
                    }
                    delegatedPurchaser.setRequired(true);
                    delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
                    approvers.add(delegatedPurchaser);
                }
                if (forwardedPurchaser != null) {
                    forwardedPurchaser.setStatus("DELEGATED");
                    forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                    forwardedPurchaser.setComment(null);
                } else {
                    AssignedPurchaser.setStatus("DELEGATED");
                    AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
                    AssignedPurchaser.setComment(null);
                }
            }
        }

        return approverHeader;
    }

    private static InstanceApproverDto getInstanceApproverDto(RequestApprover requestApprover) {
        InstanceApproverDto instanceApproverDto = new InstanceApproverDto();
        Approver approver = requestApprover.getApprover();
        instanceApproverDto.setSysUserId(approver.getUserId());
        instanceApproverDto.setLoginId(approver.getLoginId());
        instanceApproverDto.setFullName(approver.getApproverName());
        instanceApproverDto.setEmail(approver.getEmail());
        instanceApproverDto.setPhone(approver.getPhone());
        instanceApproverDto.setComment(requestApprover.getComment());
        instanceApproverDto.setStatus(requestApprover.getDeptApprovalStatus().getName().equalsIgnoreCase("None") ? "PENDING" : requestApprover.getDeptApprovalStatus().getName().toUpperCase());
        instanceApproverDto.setRequired(true);
        instanceApproverDto.setAddedBy(UserDetailServiceUtil.getFullName(requestApprover.getCreatedBy()));
        return instanceApproverDto;
    }
    private static InstanceApproverDto getInstanceApproverDto(RequestPurchaser requestPurchaser, Request request) {
        InstanceApproverDto instanceApproverDto = new InstanceApproverDto();

        Purchaser purchaser = requestPurchaser.getPurchaser();
        instanceApproverDto.setSysUserId(purchaser.getUserId());
        instanceApproverDto.setLoginId(purchaser.getLoginId());
        instanceApproverDto.setFullName(purchaser.getPurchaserName());
        instanceApproverDto.setEmail(purchaser.getEmail());
        instanceApproverDto.setPhone(purchaser.getPhone());

        if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_AWAITING.code()) ||
                request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_PARTIAL_COMPLETED.code())) {
            instanceApproverDto.setStatus("AWAITING");
        } else if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_COMPLETED.code())) {
            instanceApproverDto.setStatus("APPROVED");
        } else {
            instanceApproverDto.setStatus(request.getApprovalStatus().getName().toUpperCase());
        }

        if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_COMPLETED.code())) {
            instanceApproverDto.setComment(request.getApprovedReason());
        } else if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_REJECTED.code())) {
            instanceApproverDto.setComment(request.getRejectedReason());
        }

        instanceApproverDto.setRequired(true);
        return instanceApproverDto;
    }

    private List<InstanceApproverHeaderDto> getDefaultApproverHeaders(String tenantId, String idp) {
        List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();

        approverSection.setSectionName("Purchaser");
        approverSection.setNumberOfApproverRequired(1);
        List<InstanceApproverDto> instanceApprovers = this.getDefaultApprovers(tenantId, idp);

        boolean isApproved = instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("APPROVED"));

        boolean isRejected = instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("REJECTED"));

        boolean isCancelled = !isRejected & instanceApprovers.stream()
                .anyMatch(item -> item.getStatus().equalsIgnoreCase("CANCELLED"));

        if (isApproved) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED"))
                    .collect(Collectors.toList());
        } else if (isRejected) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("REJECTED"))
                    .collect(Collectors.toList());
        } else if (isCancelled) {
            instanceApprovers = instanceApprovers.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase("CANCELLED"))
                    .collect(Collectors.toList());
        }


        approverSection.setApprovers(instanceApprovers);

        InstanceApproverDto instanceApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED") ||
                        item.getStatus().equalsIgnoreCase("REJECTED") ||
                        item.getStatus().equalsIgnoreCase("CANCELLED") ||
                        item.getStatus().equalsIgnoreCase("FORWARDED"))
                .findFirst().orElse(null);

        InstanceApproverDto instanceAwaitingApproverDto = instanceApprovers.stream()
                .filter(item -> item.getStatus().equalsIgnoreCase("AWAITING"))
                .findFirst().orElse(null);

        if (instanceApproverDto != null) {
            approverSection.setStatus(instanceApproverDto.getStatus());
        } else if (instanceAwaitingApproverDto != null) {
            approverSection.setStatus("AWAITING");
        } else {
            approverSection.setStatus("PENDING");
        }
        approverSections.add(approverSection);
        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("PURCHASER GROUP");
        approverHeader.setApproverSections(approverSections);
        approverHeaders.add(approverHeader);

        if (instanceApprovers.size() == 0)
            approverHeaders = null;

        return approverHeaders;
    }

    private List<InstanceApproverDto> getApprovers(String tenantId, String idp, Request request) {

        // Find instanceApproverId by ApproverName & DocumentId
        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
        criteria.setPageNumber(1);
        criteria.setPageSize(100);

        List<String> refDocumentIds = new ArrayList<>();
        refDocumentIds.add(request.getRecId().toString());
        criteria.setRefDocumentId(refDocumentIds);

        List<String> statusList = new ArrayList<>();
        statusList.add("PENDING");
        statusList.add("AWAITING");
        statusList.add("APPROVED");
        statusList.add("REJECTED");
        statusList.add("CANCELLED");
        statusList.add("FORWARDED");
        criteria.setStatus(statusList);

        // Optional Params
        List<Long> workflowInstanceIds = new ArrayList<>();
        workflowInstanceIds.add(request.getWorkflowInstanceId());
        criteria.setWorkflowInstanceId(workflowInstanceIds);

        Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);

        return approvers.getContent().stream()
                .map(item -> {
                    Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
                    ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, item.getReferApproverId(), userDetailMap);
                    if (contractDetail != null) {
                        InstanceApproverDto approver = new InstanceApproverDto();
                        approver.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                        approver.setLoginId(contractDetail.getUsername());
                        approver.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                        approver.setEmail(contractDetail.getEmail());
                        approver.setMobilePhone(contractDetail.getMobilePhone());
                        approver.setPhone(contractDetail.getPhone());
                        approver.setStatus(item.getStatus());
                        approver.setRequired(item.getIsRequired());
                        approver.setComment(item.getRemark());
                        return approver;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<InstanceApproverDto> getDefaultApprovers(String tenantId, String idp) {

        // Find instance ApproverId by WorkflowTemplateId

        Tenant tenant = tenantService.findByCode(tenantId);
        Long workflowTemplateId = Long.parseLong(tenantConfigService.getWorkflowTemplateId(tenant.getRecId()));
        WorkflowInstanceData workflowDto = workflowService.getWorkflowApprovers(workflowTemplateId);

        if (workflowDto == null) return Collections.emptyList();

        WorkflowInstanceStageDto workflowStageDto = workflowDto.getInstanceStages().stream().findFirst().get();
        WorkflowInstanceStepDto workflowStepDto = workflowStageDto.getInstanceSteps().stream().findFirst().get();

        return workflowStepDto.getInstanceApprovers().stream()
                .map(item -> {
                    Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
                    ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, item.getReferApproverId(), userDetailMap);
                    if (contractDetail != null) {
                        InstanceApproverDto approver = new InstanceApproverDto();
                        approver.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
                        approver.setLoginId(contractDetail.getUsername());
                        approver.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
                        approver.setEmail(contractDetail.getEmail());
                        approver.setMobilePhone(contractDetail.getMobilePhone());
                        approver.setPhone(contractDetail.getPhone());
                        approver.setStatus(item.getStatus());
                        approver.setRequired(item.getIsRequired());
                        return approver;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Request setRequest(RequestRequest reqRequest, boolean isSaveDraft) {
        Tenant tenant = tenantRepository.findTenantByRecId(reqRequest.getTenantId());
        Request request = new Request();

        if (!reqRequest.getRecId().equals(0L) && reqRequest.getRecId() != null) {

            // Update existing Request
            Request requestHeader = requestRepository.findRequestsByRecId(reqRequest.getRecId());
            if (requestHeader != null) {
                request = requestHeader;
            }
        } else {
            // Create new Request
            //request.setRequestNo(this.generateRequestNo(tenant));
            request.setRequestNo(sourcingReferenceService.generateRequestNo("SOURCING_REQUEST", tenant.getRecId(), reqRequest.getOrganizationId()));
            request.setWorkflowInstanceId(0L); // Initialize Workflow Instance Id
            request.setCreatedBy(AppUtil.getUserName());
            request.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        request.setTenant(tenant);

        request.setOrganizationId(reqRequest.getOrganizationObj() != null ? Integer.parseInt(reqRequest.getOrganizationObj().getValue()) : null);
        request.setRequestTypeId(reqRequest.getRequestTypeObj() != null ? Integer.parseInt(reqRequest.getRequestTypeObj().getValue()) : null);
        request.setRequestName(reqRequest.getRequestName());
        if (reqRequest.getExpectedDate() != null) {
            request.setExpectedDate(reqRequest.getExpectedDate());
        }
        if (reqRequest.getBudgetRefNoObj() != null) {
            request.setBudgetRefNo(reqRequest.getBudgetRefNoObj().getLabel());
        } else {
            request.setBudgetRefNo(reqRequest.getBudgetRefNo());
        }

        request.setProjectCode(reqRequest.getProjectCode() != null ? reqRequest.getProjectCode() : null);
        request.setProjectName(reqRequest.getProjectName() != null ? reqRequest.getProjectName() : null);

        if (reqRequest.getObjective() != null) {
            request.setObjective(reqRequest.getObjective());
        }
        Currency currency = currencyRepository.findCurrenciesByRecId(Integer.parseInt(reqRequest.getCurrencyObj().getValue()));
        request.setCurrency(currency);

        request.setBudget(reqRequest.getBudget() != null && !reqRequest.getBudget().isEmpty() && CommonUtils.isNumeric(reqRequest.getBudget().replace(",", "")) ?
                new BigDecimal(reqRequest.getBudget().replace(",", "")) : null);

        // Case Submit
        if (!isSaveDraft) {
            if(AppUtil.isRequester() && reqRequest.getPathUrl().equalsIgnoreCase("sourcing-request")) {
                request.setRequestDate(DateTimeUtil.getTimestampUTC());
            }
            String approvalType = tenantConfigService.getWorkflowApprovalType(reqRequest.getTenantId());
            request.setApprovalType(approvalType);
            if (Objects.requireNonNull(AppUtil.getTenantId()).contains("BAY")) {
                int i = new Random().nextInt(3 - 1 + 1) + 1;
                request.setAssignedBy("Purchaser0" + i);
            }
        }
        // Case Save Draft
        else {
            request.setCreatedBy(AppUtil.getUserName());
            request.setCreatedDate(DateTimeUtil.getTimestampUTC());
        }

        request.setPhone(reqRequest.getPhone());
        request.setMobile(reqRequest.getMobile());
        request.setEmail(reqRequest.getEmail());

        boolean isEnableDeptApprover = tenantConfigService.isEnableDeptApprover(tenant.getRecId());
        String requestStatus = isSaveDraft ? TENANT_REQUEST_DRAFT.code() : isEnableDeptApprover ? TENANT_REQUEST_PENDING.code() : TENANT_REQUEST_AWAITING.code();
        TenantRequestStatus tenantRequestStatus = tenantRequestStatusRepository.findByNameAndTenant(requestStatus, request.getTenant());
        if(request.getRequestStatus() == null || !request.getRequestStatus().getName().equalsIgnoreCase(TENANT_REQUEST_AWAITING.code())) {
            request.setStatusId(tenantRequestStatus.getRequestStatus().getRecId());
            request.setRequestStatus(tenantRequestStatus);
        }

        DeptApprovalStatus deptApprovalStatusObj = deptApprovalStatusRepository.findByName(isSaveDraft ? DEPT_APPROVAL_NONE.code() : DEPT_APPROVAL_AWAITING.code());
        if(request.getDeptApprovalStatus() == null || (!request.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_APPROVED.code()) &&
                !request.getDeptApprovalStatus().getName().equalsIgnoreCase(DEPT_APPROVAL_REJECTED.code()))) {
            request.setDeptApprovalStatus(deptApprovalStatusObj);
        }

        String approvalStatus = isSaveDraft ? TENANT_APPROVAL_DRAFT.code() : isEnableDeptApprover ? TENANT_APPROVAL_PENDING.code() : TENANT_APPROVAL_AWAITING.code();
        TenantApprovalStatus tenantApprovalStatus = tenantApprovalStatusRepository.findByNameAndTenant(approvalStatus, request.getTenant());
        if(request.getApprovalStatus() == null || !request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_AWAITING.code())) {
            request.setApprovalStatusId(tenantApprovalStatus.getApprovalStatus().getRecId());
            request.setApprovalStatus(tenantApprovalStatus);
        }

        request.setCancellationReason(reqRequest.getCancellationReason());
        request.setNoteToApprovers(reqRequest.getNoteToApprovers());
        request.setUpdatedBy(AppUtil.getUserName());
        request.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        request.setObjective(reqRequest.getObjective());
        request.setVatType(reqRequest.getVatType());
        request.setProjectCode(reqRequest.getProjectCode());
        request.setProjectName(reqRequest.getProjectName());

        return request;
    }

//    private String generateRequestNo(Tenant tenant) {
//        String requestNo;
//        Date date = new Date(DateTimeUtil.getTimestampUTC().getTime());
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
//        String strYYYYMM = formatter.format(date);
//        int index = 1;
//        int runningNo = 0;
//
//        Optional<String> latestRunningRequestNo = requestRepository.findLatestRequestNoByTenantId(tenant.getRecId());
//        if (latestRunningRequestNo.isPresent()) {
//            runningNo = Integer.parseInt(latestRunningRequestNo.get());
//        }
//        // Format : yyyymm000001
//        do {
//            requestNo = strYYYYMM + String.format("%06d", runningNo + index++);
//        }
//        while (requestRepository.getRequestByRequestNo(requestNo, tenant.getRecId()) != null);
//        return requestNo;
//    }

    private Specification<Request> getSpecificationByCondition(RequestSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            String createdBy = AppUtil.getUserName();
            if (!StringUtils.isEmpty(createdBy)) {
                predicates.add(criteriaBuilder.equal(root.get(CREATED_BY), createdBy));
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));

            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));

            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        if (REQUESTER.description().equalsIgnoreCase(searchField) ||
                                REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(criteriaBuilder.in(root.get(REQUESTER.description())).value(loginIds));

                        } else if (PROJECT_CODE.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            predicates.add(criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));

                        } else if (PROJECT_NAME.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(joinProject.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateDepartment));

                        } else if (PROJECT_LABEL.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = (criteriaBuilder.like(join.get(PROJECT_CODE.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProject = (criteriaBuilder.like(join.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));

                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProjectCode, predicateProject, predicateDepartment));

                        } else {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        }
                    }

                }
            }

            if (searchRequest.getRequestStatusList() != null && !searchRequest.getRequestStatusList().isEmpty()) {
                List<Integer> requestStatusIdList = searchRequest.getRequestStatusList().stream().map(RequestStatusDto::getRecId).collect(Collectors.toList());
                predicates.add(criteriaBuilder.in(root.get(REQUEST_STATUS_ID)).value(requestStatusIdList));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Request> getExcelSpecificationByCondition(RequestSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            String createdBy = AppUtil.getUserName();
            if (!StringUtils.isEmpty(createdBy)) {
                predicates.add(criteriaBuilder.equal(root.get(CREATED_BY), createdBy));
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (!StringUtils.isEmpty(searchField) && !StringUtils.isEmpty(searchValue)) {
                        if (REQUESTER.description().equalsIgnoreCase(searchField) ||
                                REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(criteriaBuilder.in(root.get(REQUESTER.description())).value(loginIds));

                        } else if (PROJECT_CODE.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            predicates.add(criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));

                        } else if (PROJECT_NAME.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(joinProject.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateDepartment));

                        } else if (PROJECT_LABEL.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = (criteriaBuilder.like(join.get(PROJECT_CODE.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(join.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));

                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProjectCode, predicateProjectName, predicateDepartment));

                        } else {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        }
                    }
                }
            }

            List<RequestStatusDto> requestStatusDtoList = searchRequest.getRequestStatusList();
            if (requestStatusDtoList != null && !requestStatusDtoList.isEmpty()) {
                List<Integer> statusIds = requestStatusDtoList.stream()
                        .map(RequestStatusDto::getRecId)
                        .collect(Collectors.toList());
                predicates.add(root.get(REQUEST_STATUS_ID).in(statusIds));
            }

            if (searchRequest.getRequestStatusList() != null && !searchRequest.getRequestStatusList().isEmpty()) {
                List<Integer> requestStatusIdList = searchRequest.getRequestStatusList().stream().map(RequestStatusDto::getRecId).collect(Collectors.toList());
                predicates.add(criteriaBuilder.in(root.get(REQUEST_STATUS_ID)).value(requestStatusIdList));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Request> getApprovalSpecificationByCondition(ApprovalSearchRequest searchRequest, boolean isAllApproval) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            String assignedBy = AppUtil.getUserName();
            if (!isAllApproval) {
                if (!StringUtils.isEmpty(assignedBy)) {
                    String authHeader = "Bearer " + AppUtil.getJwtToken();
                    DelegatorActiveRequest delegatorActiveRequest = new DelegatorActiveRequest();
                    delegatorActiveRequest.setDelegateeUserName(assignedBy);
                    DelegatorActiveResponse delegatorActiveResponse = delegateClient.getDelegatorActive(authHeader, delegatorActiveRequest);
                    List<String> delegators = delegatorActiveResponse.getDelegatorUserNames() != null ? delegatorActiveResponse.getDelegatorUserNames() : new ArrayList<>();
                    List<Predicate> userPredicates = new ArrayList<>();
                    userPredicates.add(criteriaBuilder.equal(root.get(ASSIGNED_BY), assignedBy));
                    userPredicates.add(criteriaBuilder.equal(root.get(DELEGATE_ACTION_BY), assignedBy));
                    for (String delegator : delegators) {
                        userPredicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(ASSIGNED_BY), delegator), criteriaBuilder.equal(root.get(APPROVAL_STATUS).get(NAME), TENANT_REQUEST_AWAITING.code())));
                    }
                    predicates.add(criteriaBuilder.or(userPredicates.toArray(new Predicate[]{})));
                }
            } else {
                if (!StringUtils.isEmpty(assignedBy)) {
                    Join<Request, RequestPurchaser> join = root.join("requestPurchaserList");
                    predicates.add(criteriaBuilder.equal(join.get("purchaser").get("loginId"), assignedBy));
                }
            }
            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (REQUESTER.description().equalsIgnoreCase(searchField) ||
                                REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(criteriaBuilder.in(root.get(REQUESTER.description())).value(loginIds));

                        } else if (PROJECT_CODE.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            predicates.add(criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProject = (criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectCode = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateProjectCode));

                        } else if (PROJECT_NAME.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(joinProject.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateDepartment, predicateProjectName));

                        } else if (PROJECT_LABEL.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = (criteriaBuilder.like(join.get(PROJECT_CODE.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(join.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));

                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProjectCode, predicateProjectName, predicateDepartment));

                        } else {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        }
                    }
                }
            }
            if (searchRequest.getApprovalStatusList() != null && !searchRequest.getApprovalStatusList().isEmpty()) {
                List<Integer> approvalStatusIdList = searchRequest.getApprovalStatusList().stream().map(ApprovalStatusDto::getRecId).collect(Collectors.toList());
                predicates.add(criteriaBuilder.in(root.get(APPROVAL_STATUS_ID)).value(approvalStatusIdList));
            } else {
                if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                    List<TenantApprovalStatus> tenantApprovalStatusList =
                            tenantApprovalStatusRepository.findByTenantIdAndNameIn(
                                    tenantId,
                                    Arrays.asList(TENANT_APPROVAL_DRAFT.code(), TENANT_APPROVAL_PENDING.code(), TENANT_APPROVAL_CANCELLED.code()));

                    tenantApprovalStatusList.forEach(
                            i -> predicates.add(criteriaBuilder.notEqual(root.get(APPROVAL_STATUS_ID), i.getApprovalStatus().getRecId()))
                    );

                    DeptApprovalStatus tenantRequestStatus = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_REJECTED.code());
                    predicates.add(criteriaBuilder.notEqual(root.get(DEPT_APPROVAL_STATUS).get(REC_ID), tenantRequestStatus.getRecId()));
                } else {

                    List<TenantApprovalStatus> tenantApprovalStatusList =
                            tenantApprovalStatusRepository.findByTenantIdAndNameIn(
                                    tenantId,
                                    Arrays.asList(TENANT_APPROVAL_DRAFT.code()));

                    tenantApprovalStatusList.forEach(
                            i -> predicates.add(criteriaBuilder.notEqual(root.get(APPROVAL_STATUS_ID), i.getApprovalStatus().getRecId()))
                    );
                }
            }

            if(tenantConfigService.isEnableWorkflowEngine(tenantId)) {
                predicates.add(criteriaBuilder.notEqual(root.get(WORKFLOW_INSTANCE_ID), 0));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> getLoginIdByUsername(String searchValue, String privilegeCode) {
        List<ConditionSearchRequest> conditionSearchList = new ArrayList<>();
        conditionSearchList.add(ConditionSearchRequest.builder().searchField("username").searchValue(searchValue).build());

        EPAuthUserSearchRequest ePAuthUserSearchRequest = new EPAuthUserSearchRequest();
        ePAuthUserSearchRequest.setTenantId(AppUtil.getTenantId());
        ePAuthUserSearchRequest.setPage(1);
        ePAuthUserSearchRequest.setPageSize(99999);// Checking passed
        ePAuthUserSearchRequest.setSortBy("username");
        ePAuthUserSearchRequest.setSortOrder("desc");
        ePAuthUserSearchRequest.setConditionSearchList(conditionSearchList);

        EPAuthUserListResponse response = epAuthService.getListByConditions(ePAuthUserSearchRequest, privilegeCode);

        if (response != null) {
            return response.getData().stream().map(EPAuthUserDTO::getLoginId).collect(Collectors.toList());
        }
        return null;
    }

    private Specification<Request> getRequestReviewerSpecificationByCondition(RequestSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

//            List<Long> filteredRequestIds = requestId.stream()
//                    .filter(Objects::nonNull)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            if (!filteredRequestIds.isEmpty()) {
//                predicates.add(criteriaBuilder.in(root.get(REC_ID)).value(filteredRequestIds));
//            }

            Optional<ReportLine> reportLine = reportLineRepository.findByLoginId(tenantId, AppUtil.getUserName());
            Optional<Reviewer> reviewer = reviewerRepository.findByLoginId(tenantId, AppUtil.getUserName());

            if(reportLine.isPresent() && reviewer.isPresent()) {
                Join<Request, RequestReportLine> joinReportLine = root.join("requestReportLineList", JoinType.LEFT);
                Predicate predicateReportLine = (criteriaBuilder.equal(joinReportLine.get("reportLine"), reportLine.get()));

                Join<Request, RequestReviewer> joinReviewer = root.join("requestReviewerList", JoinType.LEFT);
                Predicate predicateReviewer = (criteriaBuilder.equal(joinReviewer.get("reviewer"), reviewer.get()));
                predicates.add(criteriaBuilder.or(predicateReviewer, predicateReportLine));
            } else if(reportLine.isPresent()) {
                Join<Request, RequestReportLine> joinReportLine = root.join("requestReportLineList", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(joinReportLine.get("reportLine"), reportLine.get()));
            }
            else {
                Join<Request, RequestReviewer> joinReviewer = root.join("requestReviewerList", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(joinReviewer.get("reviewer"), reviewer.get()));
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (REQUESTER.description().equalsIgnoreCase(searchField) ||
                                REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(criteriaBuilder.in(root.get(REQUESTER.description())).value(loginIds));

                        } else if (PROJECT_CODE.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            predicates.add(criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProject = (criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectCode = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateProjectCode));

                        } else if (PROJECT_NAME.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(joinProject.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateDepartment, predicateProjectName));

                        } else if (PROJECT_LABEL.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = (criteriaBuilder.like(join.get(PROJECT_CODE.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(join.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));

                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProjectCode, predicateProjectName, predicateDepartment));

                        } else {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        }
                    }
                }
            }

            if (searchRequest.getRequestStatusList() != null && !searchRequest.getRequestStatusList().isEmpty()) {
                List<Integer> requestStatusIdList = searchRequest.getRequestStatusList().stream().map(RequestStatusDto::getRecId).collect(Collectors.toList());
                predicates.add(criteriaBuilder.in(root.get(REQUEST_STATUS_ID)).value(requestStatusIdList));
            } else {
                if(tenantConfigService.isEnableDeptApprover(tenantId)) {
                    List<TenantRequestStatus> tenantRequestStatusList =
                            tenantRequestStatusRepository.findByTenantIdAndNameIn(
                                    tenantId,
                                    Arrays.asList(TENANT_REQUEST_DRAFT.code(), TENANT_REQUEST_CANCELLED.code()));

                    tenantRequestStatusList.forEach(
                            i -> predicates.add(criteriaBuilder.notEqual(root.get(REQUEST_STATUS_ID), i.getRequestStatus().getRecId()))
                    );
                } else {
                    List<TenantRequestStatus> tenantRequestStatusList =
                            tenantRequestStatusRepository.findByTenantIdAndNameIn(
                                    tenantId,
                                    Arrays.asList(TENANT_REQUEST_DRAFT.code()));

                    tenantRequestStatusList.forEach(
                            i -> predicates.add(criteriaBuilder.notEqual(root.get(REQUEST_STATUS_ID), i.getRequestStatus().getRecId()))
                    );
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Request> getRequestDeptApproverSpecificationByCondition(RequestDeptApproverSearchRequest searchRequest, List<Long> requestId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Integer tenantId = searchRequest.getTenant().getRecId();
            if (tenantId != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenantId));
            }

            // predicates.add(criteriaBuilder.in(root.get(REC_ID)).value(requestId));
            List<Long> filteredRequestIds = requestId.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!filteredRequestIds.isEmpty()) {
                predicates.add(root.get(REC_ID).in(filteredRequestIds));
            }

            Date fromDateCondition = searchRequest.getFromDate();
            Date toDateCondition = searchRequest.getToDate();
            if (fromDateCondition != null && toDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.between(root.get(REQUEST_DATE), fromDate, toDate));
            } else if (fromDateCondition != null) {
                Timestamp fromDate = DateTimeUtil.getTimestampUTC(DateTimeUtil.subtractHour(fromDateCondition, 7));
                predicates.add(criteriaBuilder.greaterThan(root.get(REQUEST_DATE), fromDate));
            } else if (toDateCondition != null) {
                Timestamp toDate = DateTimeUtil.getTimestampUTC(
                        DateTimeUtil.subtractHour(
                                DateTimeUtil.addDate(toDateCondition, 1), 7
                        )
                );
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(REQUEST_DATE), toDate));
            }

            List<ConditionSearchRequest> conditionSearchRequestList = searchRequest.getConditionSearchList();
            if (conditionSearchRequestList != null && !conditionSearchRequestList.isEmpty()) {
                for (ConditionSearchRequest condition : conditionSearchRequestList) {
                    String searchField = condition.getSearchField();
                    String searchValue = condition.getSearchValue();
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (REQUESTER.description().equalsIgnoreCase(searchField) ||
                                REQUESTER_NAME.description().equalsIgnoreCase(searchField)) {
                            List<String> loginIds = getLoginIdByUsername(searchValue, Role.REQUESTER.privilegeCode());
                            predicates.add(criteriaBuilder.in(root.get(REQUESTER.description())).value(loginIds));

                        } else if (PROJECT_CODE.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(join.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectCode = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateProjectCode));

                        } else if (PROJECT_NAME.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> joinProject = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProject = (criteriaBuilder.like(joinProject.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProject, predicateDepartment, predicateProjectName));

                        } else if (PROJECT_LABEL.description().equalsIgnoreCase(searchField)) {
                            Join<Request, RequestProject> join = root.join("requestProjectList", JoinType.LEFT);
                            Predicate predicateProjectCode = (criteriaBuilder.like(join.get(PROJECT_CODE.description()), "%" + searchValue.toLowerCase() + "%"));
                            Predicate predicateProjectName = (criteriaBuilder.like(join.get(PROJECT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));

                            Join<Request, RequestDepartment> joinDepartment = root.join("requestDepartmentList", JoinType.LEFT);
                            Predicate predicateDepartment = (criteriaBuilder.like(joinDepartment.get(DEPARTMENT_NAME.description()), "%" + searchValue.toLowerCase() + "%"));
                            predicates.add(criteriaBuilder.or(predicateProjectCode, predicateProjectName, predicateDepartment));

                        } else {
                            predicates.add(criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%"));
                        }
                    }
                }
            }

            if (searchRequest.getApproveStatusList() != null && !searchRequest.getApproveStatusList().isEmpty()) {
                List<Integer> deptApprovalStatusIdList = searchRequest.getApproveStatusList().stream().map(DeptApprovalStatusDto::getRecId).collect(Collectors.toList());
                Join<Request, RequestApprover> joinDeptApprover = root.join("requestDeptApproverList", JoinType.INNER);
                predicates.add(criteriaBuilder.in(joinDeptApprover.get(DEPT_APPROVAL_STATUS).get(REC_ID)).value(deptApprovalStatusIdList));


                Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
                Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
                Predicate predicateDeptApproverOwner = (criteriaBuilder.equal(joinDeptApprover.get("approverId"), approver.isPresent() ? approver.get().getRecId() : -1));
                predicates.add(predicateDeptApproverOwner);

//                Optional<Approver> approver = approverRepository.findApproverByLoginId(AppUtil.getUserName());
//                Join<Request, RequestApprover> joinDeptApprover = root.join("requestDeptApproverList", JoinType.INNER);
//                Predicate predicateDeptApproverOwner = (criteriaBuilder.equal(joinDeptApprover.get("approverId"), approver.isPresent() ? approver.get().getRecId() : -1));
//                predicates.add(predicateDeptApproverOwner);
//
//                Predicate predicateDeptApproverAwaiting = null;
//                Predicate predicateDeptApproverApproved = null;
//                Predicate predicateDeptApproverRejected = null;
//
//                for(Integer deptApprovalStatusId : deptApprovalStatusIdList) {
//                    if(deptApprovalStatusId == DEPT_APPROVAL_AWAITING.id()) {
//                        predicateDeptApproverAwaiting = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_AWAITING.id()));
//                        //predicates.add(criteriaBuilder.or(predicateDeptApproverAwaiting));
//                    } else if(deptApprovalStatusId == DEPT_APPROVAL_APPROVED.id()) {
//                        predicateDeptApproverApproved = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_APPROVED.id()));
//                        //predicates.add(criteriaBuilder.or(predicateDeptApproverApproved));
//                    } else if(deptApprovalStatusId == DEPT_APPROVAL_REJECTED.id()) {
//                        predicateDeptApproverRejected = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_REJECTED.id()));
//                        //predicates.add(criteriaBuilder.or(predicateDeptApproverRejected));
//                    }
//                }
//                predicates.add(criteriaBuilder.or(predicateDeptApproverAwaiting, predicateDeptApproverApproved, predicateDeptApproverRejected));
            } else {

                Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
                Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
                Join<Request, RequestApprover> joinDeptApprover = root.join("requestDeptApproverList", JoinType.INNER);
                Predicate predicateDeptApproverAwaiting = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_AWAITING.id()));
                Predicate predicateDeptApproverApproved = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_APPROVED.id()));
                Predicate predicateDeptApproverRejected = (criteriaBuilder.equal(joinDeptApprover.get("deptApprovalStatusId"), DEPT_APPROVAL_REJECTED.id()));
                Predicate predicateDeptApproverOwner = (criteriaBuilder.equal(joinDeptApprover.get("approverId"), approver.isPresent() ? approver.get().getRecId() : -1));
                predicates.add(criteriaBuilder.or(predicateDeptApproverAwaiting, predicateDeptApproverApproved, predicateDeptApproverRejected));
                predicates.add(predicateDeptApproverOwner);

            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private RequestStatusDto updateRequestStatusByPrivilegeCode(RequestStatusDto requestStatusDto, boolean allowUpdateRequest, boolean allowCopyToPR) {
        if(requestStatusDto != null) {
            requestStatusDto.setCanEdit(requestStatusDto.isCanEdit() & allowUpdateRequest);
            requestStatusDto.setCanCancel(requestStatusDto.isCanCancel() & allowUpdateRequest);
            requestStatusDto.setCanDelete(requestStatusDto.isCanDelete() & allowUpdateRequest);
            requestStatusDto.setCanDuplicate(requestStatusDto.isCanDuplicate() & allowUpdateRequest);
            requestStatusDto.setCanCopyToPR(requestStatusDto.isCanCopyToPR() & allowCopyToPR);
            return requestStatusDto;
        }
        return null;
    }

    private ApprovalStatusDto updateApprovalStatusByPrivilegeCode(ApprovalStatusDto approvalStatusDto, boolean allowUpdateRequest, boolean allowCopyToPR) {
        if(approvalStatusDto != null) {
            approvalStatusDto.setCanEdit(approvalStatusDto.isCanEdit() & allowUpdateRequest);
            approvalStatusDto.setCanCancel(approvalStatusDto.isCanCancel() & allowUpdateRequest);
            approvalStatusDto.setCanDelete(approvalStatusDto.isCanDelete() & allowUpdateRequest);
            approvalStatusDto.setCanDuplicate(approvalStatusDto.isCanDuplicate() & allowUpdateRequest);
            approvalStatusDto.setCanCopyToPR(approvalStatusDto.isCanCopyToPR() & allowCopyToPR);
            return approvalStatusDto;
        }
        return null;
    }

    private RequestStatusDto updateRequestStatusByPrivilegeCode(RequestStatusDto requestStatusDto,
                                                                boolean allowUpdateRequest,
                                                                boolean allowCopyToPR,
                                                                boolean allowCancel) {
        requestStatusDto.setCanEdit(requestStatusDto.isCanEdit() & allowUpdateRequest);
        requestStatusDto.setCanCancel(requestStatusDto.isCanCancel() & allowUpdateRequest & allowCancel);
        requestStatusDto.setCanDelete(requestStatusDto.isCanDelete() & allowUpdateRequest);
        requestStatusDto.setCanDuplicate(requestStatusDto.isCanDuplicate() & allowUpdateRequest);
        requestStatusDto.setCanCopyToPR(requestStatusDto.isCanCopyToPR() & allowCopyToPR);
        return requestStatusDto;
    }

//    private ERFXStatusResponse getERFXStatusList(ERFXStatusRequest eRFXStatusRequest, String refreshToken) {
//        String authHeader = "Bearer " + (refreshToken == null ? AppUtil.getJwtToken() : refreshToken);
//        return erfxClient.getErfxStatus(authHeader, eRFXStatusRequest);
//    }
}
