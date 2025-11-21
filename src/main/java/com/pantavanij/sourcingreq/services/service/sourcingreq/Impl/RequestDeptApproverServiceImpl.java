package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;
import com.pantavanij.sourcingreq.services.domain.dto.eform.FormDTO;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import com.pantavanij.sourcingreq.services.enums.Role;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.ApprovalStatus.APPROVAL_AWAITING;
import static com.pantavanij.sourcingreq.services.enums.DeptApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_CONDITION;
import static com.pantavanij.sourcingreq.services.enums.RequestType.REQUEST_TYPE_QUANTITY;
import static com.pantavanij.sourcingreq.services.enums.Role.*;
import static com.pantavanij.sourcingreq.services.enums.TenantApprovalStatus.*;
import static com.pantavanij.sourcingreq.services.enums.TenantRequestStatus.*;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;
import static com.pantavanij.sourcingreq.services.util.Constant.*;
import static com.pantavanij.sourcingreq.services.util.Constant.STAMP_DUTY;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestDeptApproverServiceImpl implements RequestDeptApproverService {
    private final RequestApproverRepository requestApproverRepository;
    private final RequestRepository requestRepository;
    private final UaaService uaaService;
    private final TenantConfigService tenantConfigService;
    private final TenantService tenantService;
    private final EpAuthClient epAuthClient;
    private final ApproverRepository approverRepository;
    private final DeptApprovalStatusRepository deptApprovalStatusRepository;
    private final ApproverService approverService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRequestDeptApprover(Request request, Tenant tenant, List<Integer> approvers) {
        List<RequestApprover> requestApprovers = new ArrayList<>();
        Integer sequence = 1;

        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_NONE.code());
        for(Integer approverId : approvers) {
            Optional<Approver> approver = approverRepository.findApproverByTenantAndUserId(tenant, approverId);
            if(approver.isPresent()) {
                RequestApprover requestApprover = RequestApprover.builder()
                        .request(request)
                        .approver(approver.get())
                        .deptApprovalStatus(sequence != 1 ? deptApprovalStatusNoneObj : request.getDeptApprovalStatus())
                        .sequence(sequence++)
                        .createdBy(AppUtil.getUserName())
                        .createdDate(DateTimeUtil.getTimestampUTC())
                        .build();
                requestApprovers.add(requestApprover);
            } else {

                //List<RequestApprover> requestApprovers = new ArrayList<>();
                ApproverSearchRequest requestSearchRequest = getApproverSearchRequest(tenant);
                EPAuthDeptApproverResponse epAuthDeptApproverResponse = approverService.getApproverListByConditions(requestSearchRequest);
                List<EPAuthDeptApproverDto> epAuthDeptApproverList = new ArrayList<>();

                if(epAuthDeptApproverResponse.getData() != null) {
                    epAuthDeptApproverList = epAuthDeptApproverResponse.getData();
                }

                Optional<EPAuthDeptApproverDto> epAuthDeptApproverDto = null;
                if(!epAuthDeptApproverList.isEmpty()) {
                    epAuthDeptApproverDto = epAuthDeptApproverList.stream()
                            .filter(da -> da.getSysUserId().equals(approverId)).findFirst();
                }

                if(epAuthDeptApproverDto != null && epAuthDeptApproverDto.isPresent()) {
                    Optional<Approver> newApprover = approverRepository.findApproverByTenantAndUserId(tenant, approverId);
                    if(newApprover.isPresent()) {
                        RequestApprover requestApprover = RequestApprover.builder()
                                .request(request)
                                .approver(newApprover.get())
                                .deptApprovalStatus(sequence != 1 ? deptApprovalStatusNoneObj : request.getDeptApprovalStatus())
                                .sequence(sequence++)
                                .createdBy(AppUtil.getUserName())
                                .createdDate(DateTimeUtil.getTimestampUTC())
                                .build();
                        requestApprovers.add(requestApprover);
                    }
                }
            }
        }

        // Delete request report line.
        requestApproverRepository.deleteRequestApproverByRequestId(request.getRecId());

        if (!requestApprovers.isEmpty()) {
            requestApproverRepository.saveAll(requestApprovers);
        }
    }

    public ApproverSearchRequest getApproverSearchRequest(Tenant tenant) {
        ApproverSearchRequest requestSearchRequest = new ApproverSearchRequest();
        requestSearchRequest.setPage(1);
        requestSearchRequest.setPageSize(99999);// Checking passed
        requestSearchRequest.setSortBy("username");
        requestSearchRequest.setSortOrder("desc");
        requestSearchRequest.setTenantId(tenant.getCode());
        return requestSearchRequest;
    }

    @Override
    public List<RequestDeptApproverDto> findByRequest(Long requestId) {
        List<RequestDeptApproverDto> requestDeptApproverDtos = new ArrayList<>();
        Request request = requestRepository.findRequestsByRecId(requestId);
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequest(request);
        if(requestApprovers != null && !requestApprovers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestDeptApproverDtos = RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(requestApprovers, timeZone);
            requestDeptApproverDtos.stream()
                    .peek(requestDeptApproverDto -> {
                        requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return requestDeptApproverDtos;
    }

    @Override
    public RequestDeptApproverDto findCurrentAwaitingDeptApprover(Long requestId) {
        RequestDeptApproverDto requestDeptApproverDto = null;
        Optional<RequestApprover> requestApprover = requestApproverRepository.findCurrentAwaitingDeptApprover(requestId);
        if(requestApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
        }
        return requestDeptApproverDto;
    }

    @Override
    public RequestDeptApproverDto findLatestApprovedDeptApprover(Long requestId) {
        RequestDeptApproverDto requestDeptApproverDto = null;
        Optional<RequestApprover> requestApprover = requestApproverRepository.findLatestApprovedDeptApprover(requestId);
        if(requestApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
        }
        return requestDeptApproverDto;
    }

    @Override
    public RequestDeptApproverDto find1StCancelledDeptApprover(Long requestId) {
        RequestDeptApproverDto requestDeptApproverDto = null;
        Optional<RequestApprover> requestApprover = requestApproverRepository.find1StCancelledDeptApprover(requestId);
        if(requestApprover.isPresent() ) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestDeptApproverDto = RequestApproverMapper.INSTANCE.toRequestDeptApproverDto(requestApprover.get(), timeZone);
            requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
        }
        return requestDeptApproverDto;
    }

    @Override
    public List<RequestDeptApproverDto> findAllApprovedDeptApprover(Long requestId) {
        List<RequestDeptApproverDto> requestDeptApproverDtos = new ArrayList<>();
        Request request = requestRepository.findRequestsByRecId(requestId);
        DeptApprovalStatus deptApprovalStatusNoneObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_APPROVED.code());
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusNoneObj);
        if(requestApprovers != null && !requestApprovers.isEmpty()) {
            String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
            requestDeptApproverDtos = RequestApproverMapper.INSTANCE.toRequestDeptApproverDtoList(requestApprovers, timeZone);
            requestDeptApproverDtos.stream()
                    .peek(requestDeptApproverDto -> {
                        requestDeptApproverDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDeptApproverDto.getCreatedBy()));
                    }).collect(Collectors.toList());
        }
        return requestDeptApproverDtos;
    }

    @Override
    public List<Long> findAllRelatedRequestIds() {
        List<Long> requestIds = new ArrayList<>();


        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<Approver> approver = approverRepository.findApproverByTenantAndLoginId(tenant, AppUtil.getUserName());
        if(approver.isPresent()) {
            List<RequestApprover> requestApproverList = requestApproverRepository.findRequestApproverByApprover(approver.get());

            if (requestApproverList != null && !requestApproverList.isEmpty()) {
                for (RequestApprover requestApprover : requestApproverList) {
                    requestIds.add(requestApprover.getRequest().getRecId());
                }
            }
        }

        return requestIds;
    }

    @Override
    public EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
        String privilegeToUse = Boolean.TRUE.equals(request.getIsExceptionalSourcing())
                ? Role.EXC_DEPT_APPROVER.privilegeCode()
                : Role.DEPT_APPROVER.privilegeCode();

        String[] privilegeCode = new String[] { privilegeToUse };
//        String privilegeCode = tenantConfigService.getEPAuthPrivilegeCode(tenant.getRecId(), Role.DEPT_APPROVER.roleName());
        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCode, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        List<EPAuthUserDTO> epAuthUserList = epAuthUserListResponse.getData();


        // Sort data by fullname
        epAuthUserList.sort(Comparator.comparing(EPAuthUserDTO::getFullName));

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserList) {
            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

    @Override
    public EPAuthDeptApproverResponse getDeptApproverListByConditions(DeptApproverSearchRequest request, String[] privilegeCodes) {
        EPAuthUserSearchRequest epAuthUserSearchRequest = EPAuthUserSearchRequest.builder()
                .conditionSearchList(request.getConditionSearchList())
                .tenantId(request.getTenantId())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .build();

        EPAuthUserListResponse epAuthUserListResponse = new EPAuthUserListResponse();
        try {
            epAuthUserListResponse = epAuthClient.getEPAuthUserList(privilegeCodes, epAuthUserSearchRequest);
        } catch(Exception ex) {
            ex.getMessage();
        }
        List<EPAuthDeptApproverDto> epAuthDeptApproverDtoList = new ArrayList<>();

        if (epAuthUserListResponse.getData() == null) {
            return EPAuthDeptApproverResponse.builder()
                    .data(null)
                    .page(epAuthUserListResponse.getPage())
                    .pageSize(epAuthUserListResponse.getPageSize())
                    .total(epAuthUserListResponse.getTotal())
                    .totalPage(epAuthUserListResponse.getTotalPage())
                    .build();
        }

        for (EPAuthUserDTO epAuthUserDTO: epAuthUserListResponse.getData()) {
            epAuthDeptApproverDtoList.add(
                    EPAuthDeptApproverDto.builder()
                            .sysUserId(epAuthUserDTO.getSysUserId())
                            .loginId(epAuthUserDTO.getLoginId())
                            .fullName(epAuthUserDTO.getFullName())
                            .email(epAuthUserDTO.getEmail())
                            .mobilePhone(epAuthUserDTO.getMobilePhone())
                            .phone(epAuthUserDTO.getPhone())
                            .timezone(epAuthUserDTO.getTimezone())
                            .build());
        }

        epAuthDeptApproverDtoList.sort(Comparator.comparing(EPAuthDeptApproverDto::getFullName));
        return EPAuthDeptApproverResponse.builder()
                .data(epAuthDeptApproverDtoList)
                .page(epAuthUserListResponse.getPage())
                .pageSize(epAuthUserListResponse.getPageSize())
                .total(epAuthUserListResponse.getTotal())
                .totalPage(epAuthUserListResponse.getTotalPage())
                .build();
    }

//    private RequestReviewer getRequestReviewer(String reviewerName, Request request, Tenant tenant, String comment) {
//        RequestReviewer requestReviewer = new RequestReviewer();
//        requestReviewer.setRequest(request);
//        requestReviewer.setTenant(tenant);
//        requestReviewer.setReviewerName(reviewerName);
//        requestReviewer.setComment(comment);
//        requestReviewer.setCreatedBy(AppUtil.getUserName());
//        requestReviewer.setCreatedDate(DateTimeUtil.getTimestampUTC());
//        requestReviewer.setUpdatedBy(AppUtil.getUserName());
//        requestReviewer.setUpdatedDate(DateTimeUtil.getTimestampUTC());
//        return requestReviewer;
//    }

//    public boolean duplicateChecker(List<RequestHistoryDto> requestHistoryDtos) {
//        UserDto user = AppUtil.getUser();
//        boolean userExists = requestHistoryDtos.stream()
//                .anyMatch(dto -> Objects.requireNonNull(user).getUsername().equalsIgnoreCase(dto.getCreatedBy()));
//
//        // If this user was commented in this request.
//        // Mean this user is in history of this request already.
//        if (userExists) {
//            return true;
//        }
//        return false;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRequestId(Long requestId) {
        log.info("Delete all Dept Approver by Request ID : {}", requestId);
        requestApproverRepository.deleteRequestApproverByRequestId(requestId);
    }

    @Override
    public boolean hasApprovalPermission(RequestApprover requestApprover) {
        boolean result = false;
        DeptApprovalStatus deptApproverStatus = requestApprover.getDeptApprovalStatus();
        if(deptApproverStatus.getName().equalsIgnoreCase(DEPT_APPROVAL_AWAITING.code())) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean isLastDeptApprover(Long requestId) {
        boolean result = false;
        Request request = requestRepository.findRequestsByRecId(requestId);

        DeptApprovalStatus deptApprovalStatusAwaitingObj = deptApprovalStatusRepository.findByName(DEPT_APPROVAL_AWAITING.code());
        List<RequestApprover> requestApprovers = requestApproverRepository.findRequestApproversByRequestAndDeptApprovalStatus(request, deptApprovalStatusAwaitingObj);

        if(requestApprovers == null || requestApprovers.isEmpty()) {
            result = true;
        }
        return result;
    }



//    private RequestDto findRequestSourcingForEditingByRecId(Long recId) {
//        UserDto user = AppUtil.getUser();
//        Request request = requestRepository.findRequestsByRecId(recId);
//        List<TenantRequestStatus> tenantRequestStatuses = tenantRequestStatusService.getRequestStatuses();
//        List<RequestPurchaser> RequestPurchasers = requestPurchaserRepository.findByRequest(request.getRecId());
//        DelegationDto delegationDto = null;
//
//        DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
//        delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
//        delegationActiveRequest.setDelegatorBy(request.getAssignedBy());
//
//        DelegationActiveResponse delegationActiveResponse = delegationService.getActiveDelegationByDeletatorAndDelegatee(delegationActiveRequest);
//        if (delegationActiveResponse.getData() != null) {
//            delegationDto = delegationActiveResponse.getData();
//        }
//
//        if (AppUtil.isAllowedAction(user, request, RequestPurchasers, null, null, null, delegationDto, tenantRequestStatuses, ACTIVITY_EDIT)) {
//            return prepareRequestSourcingByRecId(recId);
//        }
//        return null;
//    }

//    private RequestDto prepareRequestSourcingByRecId (Long recId) {
//        String tenantId = AppUtil.getTenantId();
//        String idp = AppUtil.getIdp();
//        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
//
//        Request request = requestRepository.findRequestsByRecId(recId);
//        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request, timeZone);
//
//        List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
//        InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenantId, idp, request);
//        approverHeaders.add(InstancePurchaserHeaderDto);
//        requestDto.setApproverHeaders(approverHeaders);
//
//        requestDto.setCreatedByName(UserDetailServiceUtil.getFullName(requestDto.getCreatedBy()));
//        requestDto.setUpdatedByName(requestDto.getUpdatedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getUpdatedBy()) : null);
//        requestDto.setAssignedByName(requestDto.getAssignedBy() != null ? UserDetailServiceUtil.getFullName(requestDto.getAssignedBy()) : null);
//
//        requestDto.setIsOwnerPurchaser(AppUtil.isPurchaser());
//        return this.setRequestDetailsFromRepositories(requestDto, recId, tenantId);
//    }

//    private InstanceApproverHeaderDto getPurchaserHeaders(String tenantId, String idp, Request request) {
//        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
//        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();
//        String tenantCode = AppUtil.getTenantId();
//        Tenant tenant = tenantService.findByCode(tenantCode);
//
//        approverSection.setSectionName("Purchaser");
//        String purchaserSectionLabel = tenantConfigService.getPurchaserSectionLabel(tenant.getRecId());
//        approverSection.setSectionLabel(purchaserSectionLabel);
//        approverSection.setNumberOfApproverRequired(1);
//
//        List<InstanceApproverDto> instanceApprovers = new ArrayList<>();
//
//
//        if(tenantConfigService.isEnableWorkflowEngine(tenant.getRecId())) {
//            instanceApprovers = this.getApprovers(tenantId, idp, request);
//
//            boolean isApproved = instanceApprovers.stream()
//                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("APPROVED"));
//
//            boolean isRejected = instanceApprovers.stream()
//                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("REJECTED"));
//
//            boolean isCancelled = !isRejected & instanceApprovers.stream()
//                    .anyMatch(item -> item.getStatus().equalsIgnoreCase("CANCELLED"));
//
//            if (isApproved) {
//                instanceApprovers = instanceApprovers.stream()
//                        .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED"))
//                        .collect(Collectors.toList());
//            } else if (isRejected) {
//                instanceApprovers = instanceApprovers.stream()
//                        .filter(item -> item.getStatus().equalsIgnoreCase("REJECTED"))
//                        .collect(Collectors.toList());
//            } else if (isCancelled) {
//                instanceApprovers = instanceApprovers.stream()
//                        .filter(item -> item.getStatus().equalsIgnoreCase("CANCELLED"))
//                        .collect(Collectors.toList());
//            }
//        } else {
//
//            List<RequestPurchaser> requestPurchaserList = requestPurchaserService.getRequestPurchaserByRequest(request);
//
//            for(RequestPurchaser requestPurchaser : requestPurchaserList) {
//                InstanceApproverDto instanceApproverDto = getInstanceApproverDto(requestPurchaser, request);
//                instanceApprovers.add(instanceApproverDto);
//            }
//        }
//
//        approverSection.setApprovers(instanceApprovers);
//
//        Optional<InstanceApproverDto> instanceApproverDto = instanceApprovers.stream()
//                .filter(item -> item.getStatus().equalsIgnoreCase("APPROVED") ||
//                        item.getStatus().equalsIgnoreCase("REJECTED") ||
//                        item.getStatus().equalsIgnoreCase("CANCELLED") ||
//                        item.getStatus().equalsIgnoreCase("FORWARDED"))
//                .findFirst();//.orElse(null);
//
//        Optional<InstanceApproverDto> instanceAwaitingApproverDto = instanceApprovers.stream()
//                .filter(item -> item.getStatus().equalsIgnoreCase("AWAITING"))
//                .findFirst();
//
//        if (instanceApproverDto.isPresent()) {
//            approverSection.setStatus(instanceApproverDto.get().getStatus());
//        } else if (instanceAwaitingApproverDto.isPresent()) {
//            approverSection.setStatus("AWAITING");
//        } else {
//            approverSection.setStatus("PENDING");
//        }
//        approverSections.add(approverSection);
//        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
//        approverHeader.setHeaderName("PURCHASER GROUP");
//        approverHeader.setApproverSections(approverSections);
//
//        List<InstanceApproverDto> approvers = approverSections.get(0).getApprovers();
//        if(approvers != null && !approvers.isEmpty()) {
//
//            InstanceApproverDto AssignedPurchaser = approvers.get(0);
//            InstanceApproverDto forwardedPurchaser = null;
//            InstanceApproverDto delegatedPurchaser = null;
//
//            boolean canForwardApprovalWorkflow = requestForwarderService.isCurrentForwarder(request.getRecId(), request.getAssignedBy());
//            if (canForwardApprovalWorkflow) {
//                ForwardedApprover forwardedApprover = requestForwarderService.getForwardedApprover(request.getRecId());
//                log.info("Forward Approver : {}", forwardedApprover);
//                ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, forwardedApprover.getForwardedApprover(), new HashMap<>());
//
//
//                if (contractDetail != null) {
//                    forwardedPurchaser = new InstanceApproverDto();
//                    forwardedPurchaser.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
//                    forwardedPurchaser.setLoginId(contractDetail.getUsername());
//                    forwardedPurchaser.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
//                    forwardedPurchaser.setEmail(contractDetail.getEmail());
//                    forwardedPurchaser.setMobilePhone(contractDetail.getMobilePhone());
//                    forwardedPurchaser.setPhone(contractDetail.getPhone());
//                    // Set lasted status
//                    forwardedPurchaser.setStatus(!approverSections.get(0).getStatus().isEmpty()
//                            ? approverSections.get(0).getStatus()
//                            : "AWAITING");
//                    forwardedPurchaser.setRequired(true);
//                    forwardedPurchaser.setComment(AssignedPurchaser.getComment());
//                    approvers.add(forwardedPurchaser);
//                    AssignedPurchaser.setComment(null);
//                }
//                AssignedPurchaser.setStatus("FORWARED");
//                AssignedPurchaser.setForwardedDate(forwardedApprover.getForwaredDate());
//
//                // CASE FORWARDED WITH DELEGATED
//                if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
//                    ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
//                    ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());
//
//                    if (contractDetailDelegatee != null) {
//                        delegatedPurchaser = new InstanceApproverDto();
//                        delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
//                        delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
//                        delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
//                        delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
//                        delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
//                        delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());
//
//                        if (forwardedPurchaser != null) {
//                            delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
//                            delegatedPurchaser.setComment(forwardedPurchaser.getComment());
//                        } else {
//                            delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
//                            delegatedPurchaser.setComment(AssignedPurchaser.getComment());
//                        }
//                        delegatedPurchaser.setRequired(true);
//                        delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
//                        approvers.add(delegatedPurchaser);
//                    }
//                    if (forwardedPurchaser != null) {
//                        forwardedPurchaser.setStatus("DELEGATED");
//                        forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
//                        forwardedPurchaser.setComment(null);
//                    } else {
//                        AssignedPurchaser.setStatus("DELEGATED");
//                        AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
//                        AssignedPurchaser.setComment(null);
//                    }
//
//                }
//            } else if (request.getAssignedBy() != null && request.getDelegateActionBy() != null && !request.getAssignedBy().equalsIgnoreCase(request.getDelegateActionBy())) {
//                ContractDetailClientDto contractDetailDelegatee = uaaService.getContractDetail(tenantId, idp, request.getDelegateActionBy(), new HashMap<>());
//                ContractDetailClientDto contractDetailDelegator = uaaService.getContractDetail(tenantId, idp, request.getAssignedBy(), new HashMap<>());
//
//                if (contractDetailDelegatee != null) {
//                    delegatedPurchaser = new InstanceApproverDto();
//                    delegatedPurchaser.setSysUserId(Integer.parseInt(contractDetailDelegatee.getUserId()));
//                    delegatedPurchaser.setLoginId(contractDetailDelegatee.getUsername());
//                    delegatedPurchaser.setFullName(String.format("%s %s", contractDetailDelegatee.getFirstName(), contractDetailDelegatee.getLastName()));
//                    delegatedPurchaser.setEmail(contractDetailDelegatee.getEmail());
//                    delegatedPurchaser.setMobilePhone(contractDetailDelegatee.getMobilePhone());
//                    delegatedPurchaser.setPhone(contractDetailDelegatee.getPhone());
//
//                    if (forwardedPurchaser != null) {
//                        delegatedPurchaser.setStatus(forwardedPurchaser.getStatus());
//                        delegatedPurchaser.setComment(forwardedPurchaser.getComment());
//                    } else {
//                        delegatedPurchaser.setStatus(AssignedPurchaser.getStatus());
//                        delegatedPurchaser.setComment(AssignedPurchaser.getComment());
//                    }
//                    delegatedPurchaser.setRequired(true);
//                    delegatedPurchaser.setDelegatedBy(String.format("%s %s", contractDetailDelegator.getFirstName(), contractDetailDelegator.getLastName()));
//                    approvers.add(delegatedPurchaser);
//                }
//                if (forwardedPurchaser != null) {
//                    forwardedPurchaser.setStatus("DELEGATED");
//                    forwardedPurchaser.setDelegatedDate(request.getDelegateActionDate());
//                    forwardedPurchaser.setComment(null);
//                } else {
//                    AssignedPurchaser.setStatus("DELEGATED");
//                    AssignedPurchaser.setDelegatedDate(request.getDelegateActionDate());
//                    AssignedPurchaser.setComment(null);
//                }
//            }
//        }
//
//        return approverHeader;
//    }

//    private List<InstanceApproverDto> getApprovers(String tenantId, String idp, Request request) {
//
//        // Find instanceApproverId by ApproverName & DocumentId
//        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
//        criteria.setPageNumber(1);
//        criteria.setPageSize(100);
//
//        List<String> refDocumentIds = new ArrayList<>();
//        refDocumentIds.add(request.getRecId().toString());
//        criteria.setRefDocumentId(refDocumentIds);
//
//        List<String> statusList = new ArrayList<>();
//        statusList.add("PENDING");
//        statusList.add("AWAITING");
//        statusList.add("APPROVED");
//        statusList.add("REJECTED");
//        statusList.add("CANCELLED");
//        statusList.add("FORWARDED");
//        criteria.setStatus(statusList);
//
//        // Optional Params
//        List<Long> workflowInstanceIds = new ArrayList<>();
//        workflowInstanceIds.add(request.getWorkflowInstanceId());
//        criteria.setWorkflowInstanceId(workflowInstanceIds);
//
//        Page<WorkflowInstApproverDto> approvers = workflowInstanceApprovalService.getByCriteria(criteria);
//
//        return approvers.getContent().stream()
//                .map(item -> {
//                    Map<String, UserDetailResponse> userDetailMap = new HashMap<>();
//                    ContractDetailClientDto contractDetail = uaaService.getContractDetail(tenantId, idp, item.getReferApproverId(), userDetailMap);
//                    if (contractDetail != null) {
//                        InstanceApproverDto approver = new InstanceApproverDto();
//                        approver.setSysUserId(Integer.parseInt(contractDetail.getUserId()));
//                        approver.setLoginId(contractDetail.getUsername());
//                        approver.setFullName(String.format("%s %s", contractDetail.getFirstName(), contractDetail.getLastName()));
//                        approver.setEmail(contractDetail.getEmail());
//                        approver.setMobilePhone(contractDetail.getMobilePhone());
//                        approver.setPhone(contractDetail.getPhone());
//                        approver.setStatus(item.getStatus());
//                        approver.setRequired(item.getIsRequired());
//                        approver.setComment(item.getRemark());
//                        return approver;
//                    } else {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    private static InstanceApproverDto getInstanceApproverDto(RequestPurchaser requestPurchaser, Request request) {
//        InstanceApproverDto instanceApproverDto = new InstanceApproverDto();
//
//        Purchaser purchaser = requestPurchaser.getPurchaser();
//        instanceApproverDto.setSysUserId(purchaser.getUserId());
//        instanceApproverDto.setLoginId(purchaser.getLoginId());
//        instanceApproverDto.setFullName(purchaser.getPurchaserName());
//        instanceApproverDto.setEmail(purchaser.getEmail());
//        instanceApproverDto.setPhone(purchaser.getPhone());
//
//        if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_AWAITING.code()) ||
//                request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_PARTIAL_COMPLETED.code())) {
//            instanceApproverDto.setStatus("AWAITING");
//        } else if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_COMPLETED.code())) {
//            instanceApproverDto.setStatus("APPROVED");
//        } else {
//            instanceApproverDto.setStatus(request.getApprovalStatus().getName().toUpperCase());
//        }
//
//        if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_COMPLETED.code())) {
//            instanceApproverDto.setComment(request.getApprovedReason());
//        } else if(request.getApprovalStatus().getName().equalsIgnoreCase(TENANT_APPROVAL_REJECTED.code())) {
//            instanceApproverDto.setComment(request.getRejectedReason());
//        }
//
//        instanceApproverDto.setRequired(true);
//        return instanceApproverDto;
//    }
//
//    private RequestDto setRequestDetailsFromRepositories(RequestDto requestDto, Long recId, String tenantCode) {
//        Integer requestTypeId = requestDto.getRequestTypeId();
//        OptionDto requestTypeObj = null;
//        if (requestTypeId != null) {
//            if (requestTypeId == REQUEST_TYPE_QUANTITY.id()) {
//                requestTypeObj = new OptionDto(
//                        REQUEST_TYPE_QUANTITY.id().toString(),
//                        REQUEST_TYPE_QUANTITY.code(),
//                        REQUEST_TYPE_QUANTITY.description()
//                );
//            } else if (requestTypeId == REQUEST_TYPE_CONDITION.id()) {
//                requestTypeObj = new OptionDto(
//                        REQUEST_TYPE_CONDITION.id().toString(),
//                        REQUEST_TYPE_CONDITION.code(),
//                        REQUEST_TYPE_CONDITION.description()
//                );
//            }
//        }
//        requestDto.setRequestTypeObj(requestTypeObj);
//        String tenantId = AppUtil.getTenantId();
//        String idp = AppUtil.getIdp();
//        String username = AppUtil.getUserName();
//        OrganizationClientDto organizationClientDto = uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
//        List<OptionDto> organizationList = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());
//        OptionDto organizationOption = organizationList.stream()
//                .filter(organization -> organization.getValue().equals(requestDto.getOrganizationId().toString()))
//                .findFirst()
//                .orElse(null);
//        requestDto.setOrganizationObj(organizationOption);
//
//        // Sort Item by ItemSequence
//        requestDto.getRequestItemList().sort(Comparator.comparing(RequestItemV2Dto::getItemSequence));
//
//        return requestDto;
//    }

}
